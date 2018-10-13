package app.entities;

import java.security.InvalidParameterException;
import com.fasterxml.jackson.core.JsonProcessingException;
import app.database.DatabaseController;

public class Adopter extends User{

	public Adopter(String email, String firstName, String lastName, String password, String passwordConf, String accessCode) throws InvalidParameterException, JsonProcessingException {
		super(email, firstName, lastName, password, passwordConf);
		checkAccessCode(accessCode);
		DatabaseController.INSTANCE.addObject(this, collection);
	}

}
