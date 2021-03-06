//////////////////////////////////////////////////////////////////////////////
//
//       DBConnection.java - Kite Messenger - Threaded chat
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


package database;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

import app.MainClass;

public class DBConnection {
	
	private Connection c;

	public DBConnection(String host, String user, String password) throws ClassNotFoundException, SQLException {
		Class.forName("org.hsqldb.jdbcDriver");
		c = DriverManager.getConnection("jdbc:hsqldb:hsql://"+host, user, password);
		
		MainClass.log("Checking database");
		String sql = "SELECT COUNT(*) FROM usuario";
		try{
			c.createStatement().executeQuery(sql);
		} catch (SQLException e){
			Scanner s = new Scanner(System.in);
			String answer = MainClass.prompt("Kite server tables not found in the database. Create the tables", MainClass.WARNING_MESSAGE, "(Y/N)", true);
			if(answer.equals("Y")){
				MainClass.log("Adding tables to the database.");
				StringBuilder sb = new StringBuilder();
				InputStream stream = getClass().getResourceAsStream("database.sql");
				
				MainClass.log("Loading queries");
				try {
					int b = stream.read();
					while(b > -1){
						sb.append((char) b);
						b = stream.read();
					}
					
					MainClass.log("Running queries");
					c.createStatement().execute(sb.toString());
					MainClass.log("The tables were created successfully.");
				} catch (IOException ex){
					MainClass.log("Unable to load queries. Exiting...", MainClass.ERROR_MESSAGE);
				}
			} else {
				MainClass.log("Kite server cannot run without its tables. Exiting...", MainClass.ERROR_MESSAGE);
				System.exit(1);
			}
		}
		
	}
	
	public ResultSet executeQuery(String sql){
		try {
			ResultSet rs = c.createStatement().executeQuery(sql);
			return rs;
		} catch(SQLException e){
			e.printStackTrace();
			return null;
		}
	}
	
	public boolean execute(String sql) throws SQLException {
		return c.createStatement().execute(sql);
	}
	
	public static String realEscape(String str){
		return str.replace("'", "''");
	}
	
}
