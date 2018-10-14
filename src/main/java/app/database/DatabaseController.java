package app.database;

import org.bson.Document;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

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
		//if(dm.findRecord(convertToDocument(accessCode), "accessCodes")) {
			//verificar atributo "used", se estiver como falso, ok e atualiza ele pra true, se não, dá erro. por enquanto pra minimizar o esforço em geração de dados, vou deixar fixo
			return true;
		//}
		//return false;
	}
	
	public void addObject(Object o, String collection) throws JsonProcessingException {
		dm.addObject(convertToDocument(o), collection);
	}
	
	public void removeObject(Object o, String collection) throws JsonProcessingException {
		dm.removeObject(convertToDocument(o), collection);
	}

	public boolean findRecord(Object o, String collection) throws JsonProcessingException {
		if(dm.findRecord(convertToDocument(o), collection)) {
			return true;
		}
		return false;
	}
	
	public boolean findRecordBy(String field, String value, String collection) {
		if(dm.findRecordBy(field, value, collection)) {
			return true;
		}	
		return false;
	}
	
	private Document convertToDocument(Object o) throws JsonProcessingException {
		return Document.parse(om.writeValueAsString(o));
	}
	
}
