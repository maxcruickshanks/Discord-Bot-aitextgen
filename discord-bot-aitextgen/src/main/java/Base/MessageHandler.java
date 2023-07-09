package Base;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class MessageHandler extends ListenerAdapter {
	@Override
    public void onMessageReceived(MessageReceivedEvent event) {
		if (!JDABase.is_running || event.getAuthor().isBot()) return;
		System.out.println(event.getMessage().getMentionedUsers().contains(JDABase.jda.getSelfUser()));
		if (Constants.DEBUG_TOGGLE) {
			System.out.println(event.getAuthor().getName() + ": " + event.getMessage().getContentRaw());
		}
		if (Constants.LLM_TOGGLE) {
			SecureRandom sr = new SecureRandom(); sr.setSeed(System.nanoTime());
			if (event.getMessage().getMentionedUsers().contains(JDABase.jda.getSelfUser()) || sr.nextInt(Constants.MESSAGE_FREQUENCY) == 0) {
				event.getMessage().getChannel().sendTyping().complete();
				List<Message> original_messages = event.getTextChannel().getHistory().retrievePast(Constants.MESSAGE_GROUP).complete();
				Collections.reverse(original_messages);
				ArrayList<TrainMessage> encoded_messages = new ArrayList<TrainMessage>();
				for (Message m : original_messages) {
					encoded_messages.add(new TrainMessage(LLM.encode_message(m.getContentRaw()), m.getAuthor().getName()));
				}
				String res = "";
				int attempts = 0;
				while (++attempts <= Constants.MAXIMUM_GENERATION_ATTEMPTS) {
					try {
						res = LLM.generate_message(encoded_messages);
						if (!res.isEmpty()) break;
					} catch (Exception e) {
						e.printStackTrace();
					}
					res = "";
					if (Constants.DEBUG_TOGGLE) {
						System.out.println("Failed attempt " + attempts);
					}
				}
				
				if (res.isEmpty()) {
					res = "Failed to generate message";
				}
				if (Constants.DEBUG_TOGGLE) {
					System.out.println("Final message: " + res);
				}
				event.getMessage().reply(res).complete();
			}
		}
	}
}
