package app.database;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class DatabaseFilter {
	public Map<String,String> entries;
	
	public DatabaseFilter() {
		entries = new HashMap<>();
	}
	
	public void add(String key, Object value) {
		try {
			entries.put(key, convertToString(value));
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void add(String key, String value) {
		entries.put(key, value);
	}
	
	public void update(String key, String value) {
		entries.replace(key, value);
	}
	
	public void delete(String key) {
		entries.remove(key);
	}
	
	private String convertToString(Object o) throws JsonProcessingException {
		return new ObjectMapper().writeValueAsString(o);
	}
	
	public String toJson() throws JsonProcessingException {
		return new ObjectMapper().writeValueAsString(entries);
	}
	
	public Map<String,String> getEntries() {
		return entries;
	}	
}
