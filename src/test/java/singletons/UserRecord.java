package singletons;

public enum UserRecord {

	ADMINISTRATOR("emaildummytestersender@gmail.com", "Administrator", "A", "admin123", "admin123", "123");
	//GUARDIAN();
	//ADOPTER();
	
	private String email;
	private String firstName;
	private String lastName;
	private String password;
	private String passwordConf;
	private String code;
	
	private UserRecord(String email, String firstName, String lastName, String password, String passwordConf, String code) {
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
	
	public String getCode() {
		return code;
	}
}
