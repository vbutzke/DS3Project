package app.database;

import org.springframework.data.mongodb.core.mapping.Document;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class DatabaseManager {

	 private MongoClient mongoClient;
	 private MongoDatabase database;
	// private MongoCollection<Document> users;

	 public void startDB(){
		 createDB();
		 createCollections();
	 }

	 private void createDB(){
	    MongoClientURI connectionString = new MongoClientURI("mongodb://localhost:27017");
	    this.mongoClient 				= new MongoClient(connectionString);
	    this.database 					= mongoClient.getDatabase("MediaSharing");
	 }

	 private void createCollections(){
	    //create collections
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
	
}
