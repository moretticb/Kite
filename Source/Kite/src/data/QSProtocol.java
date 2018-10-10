package data;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

public class QSProtocol {
	
	private final char ATTRIB_SEPARATOR = '&';
	private final char VALUE_SEPARATOR = '=';
	private final char ESCAPE = '\\';
	private final char END_OF_PROTOCOL = '\n';
	
	private HashMap<String, String> params;
	private String queryString;
	
	public QSProtocol(InputStream input) throws QSProtocolException {
		this();
		StringBuilder sb = new StringBuilder();
		try {
			char b = (char) input.read();
			while(b!=-1){
				char last = b;
				sb.append(b);
				b = (char) input.read();
				if(b=='\n' && last != '\\'){
					sb.append(b);
					break;
				}
			}
			queryString = sb.toString();
			parseQueryString();
		} catch (IOException e){
			throw new QSProtocolException(QSProtocolException.CONNECTION_FAILED);
		}
	}
	
	public QSProtocol(String queryString) throws QSProtocolException {
		this();
		this.queryString = queryString;
		parseQueryString();
	}
	
	public QSProtocol(){
		params = new HashMap<String, String>();
	}
	
	public QSProtocol(HashMap<String, String> params){
		this.params = params;
		updateQueryString();
	}
	
	private void parseQueryString() throws QSProtocolException {
		boolean foundEscape = false;
		StringBuilder buf = new StringBuilder();
		String tempString = "";
		for(int i=0, ii=queryString.length(); i<ii; i++){
			char current = queryString.charAt(i);
			
			if(foundEscape){
				
				if(current != ATTRIB_SEPARATOR && current != VALUE_SEPARATOR && current != ESCAPE && current != END_OF_PROTOCOL)
					throw new QSProtocolException(syntaxErrorMessage(i, 5));
				else {
					buf.append(current);
					foundEscape = false;
				}
				
			} else if(current == ESCAPE && !foundEscape) foundEscape = true;
			else if(current == VALUE_SEPARATOR){
				if(tempString.length()>0){
					throw new QSProtocolException(syntaxErrorMessage(i, buf.length()+5));
				} else {
					tempString = buf.toString();
					buf.delete(0, buf.length());
				}
			} else if(current == ATTRIB_SEPARATOR || current == END_OF_PROTOCOL){
				
				if(tempString.length() == 0){
					throw new QSProtocolException(syntaxErrorMessage(i, 5));
				} else {
					params.put(tempString, buf.toString());
					tempString = "";
					buf.delete(0, buf.length());
				}
				
			} else {
				buf.append(current);
			}
		}
	}
	
	private String syntaxErrorMessage(int currentPosition, int rangeToShow){
		int beginIndex = currentPosition>rangeToShow?currentPosition-rangeToShow:currentPosition-rangeToShow<0?0:currentPosition-rangeToShow;
		int endIndex = currentPosition+rangeToShow<queryString.length()?currentPosition+rangeToShow:queryString.length()-1;
		return String.format("Syntax error at offset %d: `%s%s%s`", currentPosition, beginIndex>0?"...":"", queryString.substring(beginIndex, endIndex), endIndex<queryString.length()-1?"...":"");
	}
	
	public String getString(String param){
		return params.get(param);
	}
	
	public int getInt(String param){
		return Integer.parseInt(params.get(param));
	}
	
	public void add(String param, String value){
		params.put(param, value);
		updateQueryString();
	}
	
	public void add(String param, int value){
		params.put(param, String.valueOf(value));
		updateQueryString();
	}
	
	public void remove(String param){
		params.remove(param);
	}
	
	public void rename(String param, String newName){
		try {
			add(newName, getInt(param));
		} catch (NumberFormatException e){
			add(newName, getString(param));
		}
		remove(param);
	}
	
	public void updateQueryString(){
		Iterator<Entry<String, String>> i = params.entrySet().iterator();
		StringBuilder sb = new StringBuilder();
		while(i.hasNext()){
//			sb.append(i.next());
			Entry<String, String> e = i.next();
			sb.append(String.format("%s=%s", escapeString(e.getKey()), escapeString(e.getValue())));
			sb.append(i.hasNext()?'&':'\n');
		}
		queryString=sb.toString();
	}
	
	public String escapeString(String str){
		return str.replace("\\", "\\\\").replace("&", "\\&").replace("=", "\\=").replace("\n", "\\\n");
	}
	
	public String toString(){
		return this.queryString;
	}
}
