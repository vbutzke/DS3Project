package app.database;

import org.apache.commons.math3.exception.NoDataException;
import org.bson.Document;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;

public class DatabaseManager {

	 private MongoClient mongoClient;
	 private MongoDatabase database;

	 public void startDB(){
		 createDB();
	 }

	 private void createDB(){
	    MongoClientURI connectionString = new MongoClientURI("mongodb://localhost:27017");
	    this.mongoClient 				= new MongoClient(connectionString);
	    this.database 					= mongoClient.getDatabase("Obsecao");
	 }
	    
	 public void closeConnection(){
	    this.mongoClient.close();
	 }

	 public MongoClient getMongoClient() {
		return mongoClient;
	 }

	 public void setMongoClient(MongoClient mongoClient) {
		this.mongoClient = mongoClient;
	 }

	 public MongoDatabase getDatabase() {
		return database;
	 }

	 public void setDatabase(MongoDatabase database) {
	 	this.database = database;
	 }
	 
	 public boolean findRecord(BasicDBObject record, String collection) {
			FindIterable<Document> i = database.getCollection(collection).find(record);

			for(Document d : i) {
				d.remove("_id");
				if(d.toString().equals(record.toString())) {
					return true;
				}
			}
			
			return false;
		}

	public boolean findRecord(Document record, String collection) {
		FindIterable<Document> i = database.getCollection(collection).find(record);

		for(Document d : i) {
			d.remove("_id");
			if(d.toString().equals(record.toString())) {
				return true;
			}
		}
		
		return false;
	}
	
	public boolean findRecordBy(String field, String value, String collection) {
		FindIterable<Document> i = database.getCollection(collection).find(Filters.eq(field, value));
		return i.first() != null;
	}

	public Document getRecordBy(String value, String collection) {
		FindIterable<Document> i = database.getCollection(collection).find(Filters.exists(value));
		return i.first();
	}
	
	public Document getRecord(Document record, String collection) {
		
		System.out.println("record: "+record);
		
		FindIterable<Document> i = database.getCollection(collection).find(record);

		if(i.first() != null){
		    return i.first();
        }
		
		throw new NoDataException();
	}
	
	public Document getRecord(BasicDBObject record, String collection) {
		
		System.out.println("record: "+record);
		
		FindIterable<Document> i = database.getCollection(collection).find(record);

		if(i.first() != null){
		    return i.first();
        }
		
		throw new NoDataException();
	}
	
	public void removeObject(BasicDBObject record, String collection) {
		database.getCollection(collection).deleteOne(record);
 	}
	
	public void addObject(Document record, String collection) {
		database.getCollection(collection).insertOne(record);
	}
	
	public void removeObject(Document record, String collection) {
		if(findRecord(record, collection)) {
			database.getCollection(collection).deleteOne(record);
		}
	}
	
	public void updateObject(Document record, String collection) {
		database.getCollection(collection).updateOne(Filters.eq("_id", record.get("_id")), new Document("$set", record));
	}

	public FindIterable<Document> getAllObjectsFromCollection(String collection){
		return database.getCollection(collection).find();
	}

	public FindIterable<Document> filterObjectsFromCollection(BasicDBObject record, String collection){
		return database.getCollection(collection).find(record);
	}
	
	public FindIterable<Document> filterObjectsFromCollection(Document record, String collection){
		return database.getCollection(collection).find(record);
	}
	
}
