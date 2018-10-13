package app.database;

import org.bson.Document;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.FindIterable;
//import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class DatabaseManager {

	 private MongoClient mongoClient;
	 private MongoDatabase database;

	 public void startDB(){
		 createDB();
	//	 createCollections();
	 }

	 private void createDB(){
	    MongoClientURI connectionString = new MongoClientURI("mongodb://localhost:27017");
	    this.mongoClient 				= new MongoClient(connectionString);
	    this.database 					= mongoClient.getDatabase("Obsecao");
	 }

	// private void createCollections(){
	    //create collections
	// }
	    
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

	public void addObject(Document record, String collection) {
		database.getCollection(collection).insertOne(record);
	}
	
	public void removeObject(Document record, String collection) {
		if(findRecord(record, collection)) {
			database.getCollection(collection).deleteOne(record);
		}
	}
	
}
