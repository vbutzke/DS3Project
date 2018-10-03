package app.entities;

import java.security.InvalidParameterException;

import app.database.DatabaseController;

public abstract class User {
	
	private String  email;
	private String  firstName;
	private String  lastName;
	private String  password;
	private String  passwordConf;
	protected final String collection = "user";
	
	public User(String email, String firstName, String lastName, String password, String passwordConf) throws InvalidParameterException {
		this.email        = email;
		this.firstName    = firstName;
		this.lastName     = lastName;
		this.password     = password;
		this.passwordConf = passwordConf;
	}
	
	
	
	
}
