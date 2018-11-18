package tests;

import com.fasterxml.jackson.core.JsonProcessingException;

import app.database.DatabaseController;
import app.entities.User;
import org.bson.Document;

abstract class AbstractUT {
	
	void startDB() {
		DatabaseController.INSTANCE.startDB();
	}
	
	void closeDB() {
		DatabaseController.INSTANCE.closeDB();
	}

	boolean findRecord(User user, String collection) throws JsonProcessingException {
        return DatabaseController.INSTANCE.findRecord(user, collection);
    }

	void resetAccessCodes(){

		DatabaseController.INSTANCE.getDM().getDatabase()
				.getCollection("accessCodes")
				.updateMany(new Document(), new Document("$set", new Document("used", false)));
	}

	void resetAnnouncements(){
		DatabaseController.INSTANCE.getDM().getDatabase().getCollection("announcements").deleteMany(new Document());
	}
}
