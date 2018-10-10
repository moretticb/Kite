//////////////////////////////////////////////////////////////////////////////
//
//         Preferences.java - Kite Messenger - Threaded chat
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


package data;

public class Preferences {
	
	private String host;
	private int port;
	
	public Preferences(String host, int port){
		this.host=host;
		this.port=port;
	}
	
	public String getHost(){
		return host;
	}
	
	public void setHost(String host){
		this.host=host;
	}
	
	public int getPort(){
		return this.port;
	}
	
	public void setPort(int port){
		this.port=port;
	}

}
