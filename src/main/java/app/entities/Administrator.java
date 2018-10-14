package app.entities;

import java.security.InvalidParameterException;

import com.fasterxml.jackson.core.JsonProcessingException;

import app.database.DatabaseController;
import app.exceptions.DuplicateEntityException;

public class Administrator extends User{

	public Administrator(String email, String firstName, String lastName, String password, String passwordConf, String accessCode) throws InvalidParameterException, JsonProcessingException, DuplicateEntityException {
		super(email, firstName, lastName, password, passwordConf);
		checkAccessCode(accessCode);
		addUser();
	}
	
}
