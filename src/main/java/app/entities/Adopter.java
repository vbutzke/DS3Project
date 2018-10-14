package app.entities;

import java.security.InvalidParameterException;
import com.fasterxml.jackson.core.JsonProcessingException;
import app.database.DatabaseController;
import app.exceptions.DuplicateEntityException;

public class Adopter extends User{

	public Adopter(String email, String firstName, String lastName, String password, String passwordConf) throws InvalidParameterException, JsonProcessingException, DuplicateEntityException {
		super(email, firstName, lastName, password, passwordConf);
		addUser();
	}

}
