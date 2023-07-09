package Base;

public class Main {
	public static JDABase JDA;
	public static void main(String[] Arguments) throws Exception {
		JDA = new JDABase();
		LLM.train_user(true);
	}
}
