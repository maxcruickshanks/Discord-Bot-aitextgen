package Base;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;

public class JDABase {
	public static JDA jda;
	public static boolean is_running = false;
	public static void start() {
		jda.addEventListener(new MessageHandler());
		jda.getPresence().setStatus(OnlineStatus.ONLINE);
		is_running = true;
	}
	public static String read_token() throws Exception {
		BufferedReader BR = new BufferedReader(new InputStreamReader(new FileInputStream(Constants.TOKEN_FILE)));
		String token = BR.readLine();
		BR.close();
		return token;
	}
	JDABase() throws Exception {
		if (Constants.DEBUG_TOGGLE) {
			System.out.println("Starting the bot!");
		}
		String token = read_token();
		jda = JDABuilder.createDefault(token).build().awaitReady();
		start();
		if (Constants.DEBUG_TOGGLE) {
			System.out.println("Finished starting the bot!");
		}
	}
}
