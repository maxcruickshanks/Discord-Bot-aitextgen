package Base;

import java.io.BufferedWriter;

public class TrainMessage {
	String message, user;
	public TrainMessage(String m, String u) {
		message = m; user = u;
	}
	public void output(BufferedWriter BW) throws Exception {
		BW.write(user + Constants.DIVIDER + message + "\n");
	}
}
