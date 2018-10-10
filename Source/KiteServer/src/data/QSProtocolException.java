package data;

public class QSProtocolException extends Exception {
	
	public static final int CONNECTION_FAILED = 1;
	
	int flag;
	
	public QSProtocolException(String message){
		super(message);
		flag = 0;
	}
	
	public QSProtocolException(int flag){
		this.flag=flag;
	}
	
	public int getFlag(){
		return this.flag;
	}

}
