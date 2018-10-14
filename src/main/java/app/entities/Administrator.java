package app.entities;

import java.security.InvalidParameterException;
import com.fasterxml.jackson.core.JsonProcessingException;
import app.exceptions.DuplicateEntityException;
import app.singletons.Permissions;

public class Administrator extends User{

	public Administrator(String email, String firstName, String lastName, String password, String passwordConf, String accessCode) throws InvalidParameterException, JsonProcessingException, DuplicateEntityException {
		super(email, firstName, lastName, password, passwordConf);
		checkAccessCode(accessCode);
		addUser();
	}
	
	public String generateAccessCode(String level) {
		if(level.equals(Permissions.ADMIN.getLevel())) {
			return Permissions.ADMIN.generateCode();
		} else if(level.equals(Permissions.GUARDIAN.getLevel())) {
			return Permissions.GUARDIAN.generateCode();
		}
		throw new InvalidParameterException("Level of permission is invalid");
	}
	
}
