package app.entities;

import java.nio.file.AccessDeniedException;
import java.security.InvalidParameterException;
import com.fasterxml.jackson.core.JsonProcessingException;
import app.database.DatabaseController;
import app.exceptions.DuplicateEntityException;
import org.springframework.dao.PermissionDeniedDataAccessException;

public abstract class User {
	
	private String email;
	private String firstName;
	private String lastName;
	private String password;
	private String permission = "Anonymous";
	protected final String collection = "user";
	
	public User(String email, String firstName, String lastName, String password, String passwordConf) throws InvalidParameterException {
		this.email     = email;
		this.firstName = firstName;
		this.lastName  = lastName;
		
		if(password.equals(passwordConf)) {
			this.password = password;
		}
	}

	public String getEmail() {
		return email;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public String getPassword() {
		return password;
	}

	public String getCollection() {
		return collection;
	}

	public String getPermission(){
		return permission;
	}

	public void setPermission(String permission){
		this.permission = permission;
	}

	public void checkAccessCode(AccessCode accessCode) throws JsonProcessingException {
		if(accessCode == null || !DatabaseController.INSTANCE.isAccessCodeValid(accessCode)) {
			throw new InvalidParameterException();
		}
	}
	
	public void addUser() throws JsonProcessingException, DuplicateEntityException {
		if(DatabaseController.INSTANCE.findRecordBy("email", this.getEmail(), collection)) {
			throw new DuplicateEntityException("This email is already in use.");
		} else {
			DatabaseController.INSTANCE.addObject(this, collection);
		}

	}

	public Announcement createAnnouncement() throws JsonProcessingException, IllegalAccessException {
		if(this.permission.equals("Guardian")){
			return Guardian.createNewAnnouncement();
		}

		throw new IllegalAccessException("User doesn't have permission to add new announcement");
	}
	
}
