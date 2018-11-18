package tests;

import com.fasterxml.jackson.core.JsonProcessingException;

import app.database.DatabaseController;
import app.entities.User;

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
}
