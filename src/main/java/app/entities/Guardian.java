package app.entities;

import java.security.InvalidParameterException;

import com.fasterxml.jackson.core.JsonProcessingException;

import app.database.DatabaseController;

public class Guardian extends User{

	public Guardian(String email, String firstName, String lastName, String password, String passwordConf) throws InvalidParameterException, JsonProcessingException {
		super(email, firstName, lastName, password, passwordConf);
		DatabaseController.INSTANCE.addObject(this, collection);
	}

}
