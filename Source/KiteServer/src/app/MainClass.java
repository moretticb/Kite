package app;
import java.util.Date;
import java.util.Scanner;

public class MainClass {
	
	private static final int DEFAULT_PORT = 2323;
	
	private static final String[] MESSAGES = {"", " ERROR:", " WARNING:"};
	
	public static final int NORMAL_MESSAGE = 0;
	public static final int ERROR_MESSAGE = 1;
	public static final int WARNING_MESSAGE = 2;
	
	
	public static void main(String[] args){
		try {
			System.out.println("***************************************************");
			System.out.println("*****                Kite Server              *****");
			System.out.println("***************************************************");
			
			int port = args.length>0?Integer.parseInt(args[0]):DEFAULT_PORT;
			Server s = new Server(port);
			s.initiate();
		} catch (Exception ex){
			ex.printStackTrace();
			System.exit(1);
		}
	}
	
	public static void log(String str){
		log(str, NORMAL_MESSAGE);
	}
	
	public static void log(String str, int messageType){
		System.out.println(String.format("[%s]%s %s ", new Date().toString(), MESSAGES[messageType], str));
	}
	
	public static String prompt(String str, String answerFormat, boolean question){
		return prompt(str, NORMAL_MESSAGE, answerFormat, question);
	}
	
	public static String prompt(String str, int messageType, String answerFormat, boolean question){
		System.out.print(String.format("[%s]%s %s %s%s ", new Date().toString(), MESSAGES[messageType], str, answerFormat, question?"?":"."));
		return new Scanner(System.in).next();
	}

}
