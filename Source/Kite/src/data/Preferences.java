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
