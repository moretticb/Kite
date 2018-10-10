package app;

import telas.MainWindow;


public class MainClass {
	
	public static void main(String[] args){
		System.setProperty("awt.useSystemAAFontSettings","on");
		System.setProperty("swing.aatext", "true");
		
		new MainWindow();
	}

}
