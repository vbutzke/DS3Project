package app.entities;

import java.security.InvalidParameterException;
import com.fasterxml.jackson.core.JsonProcessingException;
import app.exceptions.DuplicateEntityException;

public class Guardian extends User{

	public Guardian(String email, String firstName, String lastName, String password, String passwordConf, AccessCode accessCode) throws InvalidParameterException, JsonProcessingException, DuplicateEntityException {
		super(email, firstName, lastName, password, passwordConf);
		checkAccessCode(accessCode);
		addUser();
	}

	public Announcement createAnnouncement() throws JsonProcessingException {

		return new Announcement();
	}

}
