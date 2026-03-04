import java.util.*;

final class SQLQueryResult {
	
	private boolean result;
	
	private Hashtable<String, String> querys;
	
	SQLQueryResult (boolean isSucsecfull) {
		this.result = isSucsecfull;
		
		querys = new Hashtable<String, String>();
	}
	
	SQLQueryResult (boolean isSucsecfull, String[] pairs) {
		this.result = isSucsecfull;
		
		querys = new Hashtable<String, String>();
		
		for (int i = 0; i < pairs.length; i+= 2) {
			querys.put(pairs[i], pairs[i + 1]);
		}
	}
	
	SQLQueryResult (SQLQueryResult copyFrom) {
		this.result = copyFrom.result;
		
		this.querys = new Hashtable<String,String>(copyFrom.querys);
	}
	
	
	public Enumeration<String> getKeys() {
		return querys.keys();
	}
	
	public String get(String key) {
		return querys.get(key);
	}
	
	public boolean getResult() {
		return result;
	}
	
	public SQLQueryResult insertNewPair(String key, String value) {
		SQLQueryResult out = new SQLQueryResult(this);
		
		out.querys.put(key, value);
		
		return out;
	}
}