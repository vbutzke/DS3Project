package singletons;

import app.entities.AccessCode;

public enum UserRecord {

	ADMINISTRATOR("emaildummytestersender@gmail.com", "Administrator", "A", "admin123", "admin123", new AccessCode("ABC123")),
	GUARDIAN("emaildummytestersender@gmail.com", "Guardian", "A", "guardian123", "guardian123", new AccessCode("DEF123")),
	ADOPTER("emaildummytestersender@gmail.com", "Adopter", "A", "adopter123", "adopter123", null);
	
	private String     email;
	private String     firstName;
	private String     lastName;
	private String     password;
	private String     passwordConf;
	private AccessCode code;
	
	private UserRecord(String email, String firstName, String lastName, String password, String passwordConf, AccessCode code) {
		this.email 		  = email;
		this.firstName    = firstName;
		this.lastName     = lastName;
		this.password     = password;
		this.passwordConf = passwordConf;
		this.code         = code;
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

	public String getPasswordConf() {
		return passwordConf;
	}
	
	public AccessCode getCode() {
		return code;
	}
}
