package app.entities;

import java.security.InvalidParameterException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import app.database.DatabaseController;
import app.exceptions.DuplicateEntityException;
import app.utils.MongoDbId;

public class User {

	@JsonSerialize(using = ToStringSerializer.class)
	private MongoDbId _id;

	public String get_id() {
		if (_id == null)
			return null;

		return _id.get$oid();
	}

	public void set_id(MongoDbId _id) {
		this._id = _id;
	}

	private String email;
	private String firstName;
	private String lastName;
	private String password;
	private String permission = "Anonymous";
	private LinkedList<String> favoriteAnnouncements;
	private final String collection = "user";
  
	public User() {
	}

	public User(String email, String firstName, String lastName, String password, String passwordConf, AccessCode code) throws InvalidParameterException, JsonProcessingException, DuplicateEntityException {

		if (email.equals("") || firstName.equals("") || lastName.equals("") || password.equals("") || passwordConf.equals("")) {
			throw new InvalidParameterException("Email, first name, last name, password and passoword confirmation are mandatory fields. Please revise.");
		} 
    
    this.email     = email;
    this.firstName = firstName;
    this.lastName  = lastName;
    
    if (password.equals(passwordConf)) {
				this.password = password;
		} else {
				throw new DuplicateEntityException("The Password and Password Confirm must be equal.");
		}
    
		favoriteAnnouncements = new LinkedList<>();
		addUser();
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

	public String getPermission() {
		return permission;
	}

	public void setPermission(String permission) {
		this.permission = permission;
	}
    
	public void checkAccessCode(AccessCode accessCode) throws JsonProcessingException {
		if (accessCode == null || !DatabaseController.INSTANCE.isAccessCodeValid(accessCode)) {
			throw new InvalidParameterException();
		}
	}
    
	public void addUser() throws JsonProcessingException, DuplicateEntityException {
		if (DatabaseController.INSTANCE.findRecordBy("email", this.getEmail(), collection)) {
			throw new DuplicateEntityException("This email is already in use.");
		} else {
			this.set_id((MongoDbId) DatabaseController.INSTANCE.addObject(this, collection));
		}

	}

	public boolean setPassword(String password, String passwordConf){
		if(password.equals(passwordConf)){
			this.password = password;
			return true;
		}
		return false;
  }
  
	public LinkedList<String> getFavoriteAnnouncements() {
		if(favoriteAnnouncements  == null)
			favoriteAnnouncements = new LinkedList<>();
		
		return favoriteAnnouncements;
	}

	public void setFavoriteAnnouncements(LinkedList<String> favoriteAnnouncements) {
		this.favoriteAnnouncements = favoriteAnnouncements;
	}
}
