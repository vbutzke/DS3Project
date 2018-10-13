package app.entities;

import java.security.InvalidParameterException;

public abstract class User {
	
	private String  email;
	private String  firstName;
	private String  lastName;
	private String  password;
	protected final String collection = "user";
	
	public User(String email, String firstName, String lastName, String password, String passwordConf) throws InvalidParameterException {
		this.email        = email;
		this.firstName    = firstName;
		this.lastName     = lastName;
		
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
	
}
