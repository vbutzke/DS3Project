package app.database;

import java.security.InvalidParameterException;

import org.bson.Document;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import app.entities.AccessCode;

public enum DatabaseController {
	
	INSTANCE();
	
	final DatabaseManager dm;
	final ObjectMapper om;
	
	DatabaseController() {
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
	
	public boolean isAccessCodeValid(AccessCode accessCode) throws JsonProcessingException {
		
		Document d = dm.getRecord(convertToDocument(accessCode), "accessCodes");
		if(!((Boolean)d.get("used"))) {
			d.put("used", true);
			dm.updateObject(d, "accessCodes");
			return true;
		}
		throw new InvalidParameterException("The provided access code is not valid.");
	}
	
	public void addObject(Object o, String collection) throws JsonProcessingException {
		dm.addObject(convertToDocument(o), collection);
	}
	
	public void removeObject(Object o, String collection) throws JsonProcessingException {
		dm.removeObject(convertToDocument(o), collection);
	}

	public boolean findRecord(Object o, String collection) throws JsonProcessingException {
		return dm.findRecord(convertToDocument(o), collection);
	}
	
	public boolean findRecordBy(String field, String value, String collection) {
		return dm.findRecordBy(field, value, collection);
	}
	
	private Document convertToDocument(Object o) throws JsonProcessingException {
		return Document.parse(om.writeValueAsString(o));
	}
	
}
