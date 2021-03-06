package app.database;

import java.io.IOException;
import java.util.LinkedList;
import app.utils.MongoDbId;
import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import org.apache.commons.math3.exception.NoDataException;
import org.bson.Document;
import org.bson.types.ObjectId;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@SuppressWarnings("unchecked")
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

	public void updateObject(Object o, String collection) throws JsonProcessingException {
		BasicDBObject record = BasicDBObject.parse(om.writeValueAsString(o));
		dm.updateObject(record, collection);
	}
	
	public Object addObject(Object o, String collection) throws JsonProcessingException {
		Document d = convertToDocument(o);
		d.remove("_id");
		dm.addObject(d, collection);
		
		ObjectId id = (ObjectId)d.get("_id");

		return new MongoDbId(id.toHexString());
	}
	
	public void removeObject(BasicDBObject o, String collection) {
		dm.removeObject(o, collection);
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

	public LinkedList<Object> getAllObjectsFromCollection(String collection, Class c) throws IOException {
		FindIterable<Document> i = dm.getAllObjectsFromCollection(collection);
		LinkedList<Object> list = new LinkedList<>();
		for(Document d : i){
			list.add(om.readValue(d.toJson(), c));
		}
		return list;
	}
	
	public Object filter(BasicDBObject source, String collection, Class c) throws NoDataException, IOException {
		Document d = this.dm.getRecord(source, collection);
		
		return om.readValue(d.toJson(), c);
	}

	public LinkedList<Object> getList(BasicDBObject filter, String collection, Class c) throws IOException {
		// TODO Auto-generated method stub
		FindIterable<Document> i = dm.filterObjectsFromCollection(filter, collection);
		LinkedList<Object> list = new LinkedList<>();
		for(Document d : i){
			list.add(om.readValue(d.toJson(), c));
		}
		return list;
	}

	public <T> T getRecord(Object source, String collection, Class<T> c) throws NoDataException, IOException {
		Document d = this.dm.getRecord(convertToDocument(source), collection);
		
		return om.readValue(d.toJson(), c);
	}
	
	public Object getRecordBy(String value, String collection, Class c) throws IOException {
		Document d = dm.getRecordBy(value, collection);

		d.remove(value);

		//TypeFactory typeFactory = om.getTypeFactory();
		//MapType countryMapType = typeFactory.constructMapType(HashMap.class, String.class, Country.class);
		//HashMap<String, Country> map = om.readValue(d.toJson(), countryMapType);
		//TODO tratar exceção da location
		return om.readValue(d.toJson(), c);
	}

	private Document convertToDocument(Object o) throws JsonProcessingException {
		return Document.parse(om.writeValueAsString(o));
	}
	
}
