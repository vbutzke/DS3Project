package app.entities;

import java.security.InvalidParameterException;

import app.database.DatabaseController;
import com.fasterxml.jackson.core.JsonProcessingException;
import app.exceptions.DuplicateEntityException;

public class Guardian extends User{

	public Guardian(String email, String firstName, String lastName, String password, String passwordConf, AccessCode accessCode) throws InvalidParameterException, JsonProcessingException, DuplicateEntityException {
		super(email, firstName, lastName, password, passwordConf);
		setPermission("Guardian");
		checkAccessCode(accessCode);
		addUser();
	}

	public static Announcement createNewAnnouncement(String title) throws JsonProcessingException {
		Announcement a = new Announcement(title);
		DatabaseController.INSTANCE.addObject(a, "announcements");
		return a;
	}

}
