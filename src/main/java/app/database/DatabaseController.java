package app.database;

import org.bson.Document;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import app.entities.User;

public enum DatabaseController {
	
	INSTANCE();
	
	DatabaseManager dm;
	ObjectMapper om;
	
	private DatabaseController() {
		this.dm = new DatabaseManager();
		this.om = new ObjectMapper();
		
	}
	
	public DatabaseManager getDM() {
		return dm;
	}
	
	public void startDB() {
		dm.startDB();
	}
	
	public void closeDB() {
		dm.closeConnection();
	}
	
	public boolean isAccessCodeValid(String accessCode) throws JsonProcessingException {
		if(dm.findRecord(convertToDocument(accessCode), "accessCodes")) {
			return true;
		}
		return false;
	}
	
	public void addObject(Object o, String collection) throws JsonProcessingException {
		dm.addObject(convertToDocument(o), collection);
	}

	public boolean findRecord(Object o, String collection) throws JsonProcessingException {
		if(dm.findRecord(convertToDocument(o), collection)) {
			return true;
		}
		return false;
	}
	
	private Document convertToDocument(Object o) throws JsonProcessingException {
		String json = om.writeValueAsString(o);
		Document d  = new Document();
		d           = Document.parse(json);
		return d;
	}
	
}
