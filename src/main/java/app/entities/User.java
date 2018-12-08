package app.entities;

import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.LinkedList;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
	protected final String collection = "user";

	public User() {
	}

	public User(String email, String firstName, String lastName, String password, String passwordConf, AccessCode code)
			throws InvalidParameterException, JsonProcessingException, DuplicateEntityException {

		if (email == "") {
			throw new DuplicateEntityException("The email cannot be empty.");
		} else {
			this.email = email;
		}
		if (firstName == "") {
			throw new DuplicateEntityException("The First Name cannot be empty.");
		} else {
			this.firstName = firstName;
		}
		if (lastName == "") {
			throw new DuplicateEntityException("The Last Name cannot be empty.");
		} else {
			this.lastName = lastName;
		}
		if (password == "" || passwordConf == "") {
			throw new DuplicateEntityException("The Password and Password Confirm cannot be empty.");
		} else {
			if (password.equals(passwordConf)) {
				this.password = password;
			} else
			{
				throw new DuplicateEntityException("The Password and Password Confirm must be equals.");
			}
			
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
}
