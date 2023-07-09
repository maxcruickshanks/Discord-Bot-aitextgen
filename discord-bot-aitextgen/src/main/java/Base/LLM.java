package Base;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

public class LLM {
	@SuppressWarnings("unused")
	public static void train_user(boolean create_dataset) throws Exception {
		JDABase.is_running = false;
		assert Constants.CHANNEL_TO_TRAIN > 0 : " Channel to train on not defined";
		if (create_dataset) {
			create_dataset(Constants.DATA_FILE, Constants.CHANNEL_TO_TRAIN);
		}
		else {
			assert Constants.DATA_FILE.exists() : " Dataset does not exist. Please generate it with create_dataset.";
		}
		ArrayList<TrainMessage> training_messages = get_dataset(Constants.DATA_FILE);
		python_training(training_messages);
		JDABase.is_running = true;
	}
	private static void python_training(ArrayList<TrainMessage> training_messages) throws Exception {
		if (Constants.DEBUG_TOGGLE) {
			System.out.println("Started training on the contexts!");
		}
		Process python_trainer = Runtime.getRuntime().exec((System.getProperty("os.name").startsWith("Windows") ? "wsl " : "") + "python3 aitextgen/do_train.py");
		BufferedReader std_out = new BufferedReader(new InputStreamReader(python_trainer.getInputStream())), 
				std_error = new BufferedReader(new InputStreamReader(python_trainer.getErrorStream()));
		while (python_trainer.isAlive()) {
			String captured_output = "";
			while (std_out.ready()) captured_output += (char)std_out.read();
			while (std_error.ready()) captured_output += (char)std_error.read();
			if (!captured_output.isEmpty()) System.err.print("    > " + captured_output);
		}
		if (Constants.DEBUG_TOGGLE) {
			System.out.println("Finished training on the contexts!");
		}
	}
	public static String encode_message(String message) {
		return message.replaceAll("\n", ". ");
	}

	private static ArrayList<TrainMessage> get_dataset(File data_file) throws Exception {
		if (Constants.DEBUG_TOGGLE) {
			System.out.println("Loading dataset from " + data_file + "!");
		}
		ArrayList<TrainMessage> ret = new ArrayList<TrainMessage>();
		BufferedReader BR = new BufferedReader(new FileReader(data_file));
		String line;
		while ((line = BR.readLine()) != null) {
			if (!line.contains(Constants.DIVIDER)) {
				continue;
			}
			String user = line.split(Constants.DIVIDER)[0], message = line.replaceFirst(user + Constants.DIVIDER, "");
			if (!message.isEmpty()) {
				ret.add(new TrainMessage(encode_message(message), user));
			}
		}
		BR.close();
		Collections.reverse(ret);
		if (Constants.DEBUG_TOGGLE) {
			System.out.println("Finished loading " + ret.size() + " messages from dataset!");
		}
		return ret;
	}
	private static void create_dataset(File scrape_file, long channel_id) throws Exception {
		if (Constants.DEBUG_TOGGLE) {
			System.out.println("Creating dataset with output to " + scrape_file + " from " + channel_id + " (WARNING: THIS ONLY PROCESSES 5,000 MESSAGES PER MINUTE)!");
		}
		Guild guild = JDABase.jda.getGuildById(Constants.GUILD_ID);
		TextChannel channel = guild.getTextChannelById(channel_id);
		List<Message> res = new ArrayList<>();
		BufferedWriter BW = new BufferedWriter(new FileWriter(scrape_file));
		channel.getIterableHistory().forEach(
	    		(message) ->
	    	     {
	    	    	 if (!message.getAuthor().isBot()) {
	    	    		 res.add(message);
		    	         try {
		    	        	 String line = message.getAuthor().getName() + Constants.DIVIDER + encode_message(message.getContentRaw()) + "\n";
							BW.write(line);
							BW.flush();
						 } catch (Exception e) {
							 e.printStackTrace();
						 }
	    	    	 }
	    	     });
		BW.close();
		if (Constants.DEBUG_TOGGLE) {
			System.out.println("Finished creating dataset with a length of " + res.size() + "!");
		}
	}
	public static String generate_message(ArrayList<TrainMessage> context) throws Exception {
		String ret = "";
		BufferedWriter BW = new BufferedWriter(new FileWriter(Constants.CURRENT_CONTEXT_FILE));
		for (TrainMessage m : context) {
			m.output(BW);
		}
		BW.close();
		Process python_trainer = Runtime.getRuntime().exec((System.getProperty("os.name").startsWith("Windows") ? "wsl " : "") + "python3 aitextgen/do_generate.py");
		BufferedReader std_out = new BufferedReader(new InputStreamReader(python_trainer.getInputStream()));
		long time = System.currentTimeMillis();
		while (python_trainer.isAlive() && (System.currentTimeMillis() - time) <= Constants.GENERATE_TIME_LIMIT) {
			Thread.sleep(Constants.SLEEP_AMOUNT);
			String captured_output = "", error_output = "";
			while (std_out.ready()) captured_output += (char)std_out.read();
			ret += captured_output;
			if (!error_output.isEmpty() && Constants.DEBUG_TOGGLE) {
				System.err.print("    > " + error_output);
			}
			if (!captured_output.isEmpty() && Constants.DEBUG_TOGGLE) {
				System.out.print("    > " + captured_output);
			}
		}
		return ret;
	}
}
