package app.entities;

import java.security.InvalidParameterException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import app.database.DatabaseController;
import app.exceptions.DuplicateEntityException;
import app.utils.MongoDbId;

public class User {

	@JsonSerialize(using=ToStringSerializer.class)
	private MongoDbId _id;
		
	public String get_id() {
		if(_id == null)
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
	private final String collection = "user";
	
	public User(){}
	
	public User(String email, String firstName, String lastName, String password, String passwordConf) throws InvalidParameterException, JsonProcessingException, DuplicateEntityException {
		this.email     = email;
		this.firstName = firstName;
		this.lastName  = lastName;
		
		if(password.equals(passwordConf)) {
			this.password = password;
		}
		
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

	public String getPermission(){
		return permission;
	}

	public void setPermission(String permission){
		this.permission = permission;
	}
	
	public void addUser() throws JsonProcessingException, DuplicateEntityException {
		if(DatabaseController.INSTANCE.findRecordBy("email", this.getEmail(), collection)) {
			throw new DuplicateEntityException("This email is already in use.");
		} else {
			DatabaseController.INSTANCE.addObject(this, collection);
		}

	}
}
