package tests;

import com.fasterxml.jackson.core.JsonProcessingException;

import app.database.DatabaseController;
import app.entities.User;

public abstract class AbstractUT {
	
	public void startDB() {
		DatabaseController.INSTANCE.startDB();
	}
	
	public void closeDB() {
		DatabaseController.INSTANCE.closeDB();
	}

	public boolean findRecord(User user, String collection) throws JsonProcessingException {
		if(DatabaseController.INSTANCE.findRecord(user, collection)) {
			return true;
		}
		return false;
	}
}
