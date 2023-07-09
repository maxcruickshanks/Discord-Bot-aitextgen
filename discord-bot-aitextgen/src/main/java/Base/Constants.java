package Base;

import java.io.File;

public class Constants {
	//Constants that need to be changed:
	public static final long CHANNEL_TO_TRAIN = UPDATE_THIS_TO_CHANNEL_ID;
	public static final long GUILD_ID = UPDATE_THIS_TO_GUILD_ID;
	
	public static final File TOKEN_FILE = new File("TOKEN.txt");
	
	//Toggles:
	public static final boolean DEBUG_TOGGLE = true;
	public static final boolean LLM_TOGGLE = true;
	
	//LLM settings:
	public static final String DIVIDER = "#";
	public static final int MESSAGE_GROUP = 5, MESSAGE_FREQUENCY = 10, MAXIMUM_GENERATION_ATTEMPTS = 3, GENERATE_TIME_LIMIT = 30000,  SLEEP_AMOUNT = 500;
	public static final File DATA_FILE = new File("aitextgen/messages.txt"), CURRENT_CONTEXT_FILE = new File("aitextgen/current_context.txt");
}
