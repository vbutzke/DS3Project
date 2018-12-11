package singletons;

@SuppressWarnings("SameParameterValue")
public enum UserRecord {

	ADMINISTRATOR("emaildummytestersender@gmail.com", "Administrator", "A", "admin123", "admin123"),
	GUARDIAN("emaildummytestersender@gmail.com", "Guardian", "A", "guardian123", "guardian123"),
	ADOPTER("emaildummytestersender@gmail.com", "Adopter", "A", "adopter123", "adopter123");
	
	private final String     email;
	private final String     firstName;
	private final String     lastName;
	private final String     password;
	private final String     passwordConf;
	
	UserRecord(String email, String firstName, String lastName, String password, String passwordConf) {
		this.email 		  = email;
		this.firstName    = firstName;
		this.lastName     = lastName;
		this.password     = password;
		this.passwordConf = passwordConf;
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
}
