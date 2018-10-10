//////////////////////////////////////////////////////////////////////////////
//
//        MainClass.java - Kite Messenger - Threaded chat
//  Copyright (c) 2012 Caio Benatti Moretti <caiodba@gmail.com>
//                 http://www.moretticb.com/Kite
//
//  Last update: 9 October 2018
//
//  This is free software: you can redistribute it and/or modify
//  it under the terms of the GNU General Public License as published by
//  the Free Software Foundation, either version 3 of the License, or
//  (at your option) any later version.
//
//  This is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
//  GNU General Public License for more details.
//
//  You should have received a copy of the GNU General Public License
//  along with this program. If not, see <http://www.gnu.org/licenses/>.
//
//////////////////////////////////////////////////////////////////////////////


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
			System.out.println("*****  Kite Server -- www.moretticb.com/Kite  *****");
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
