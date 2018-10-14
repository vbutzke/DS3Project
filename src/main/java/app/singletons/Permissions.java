package app.singletons;

import app.entities.AccessCode;
import app.utils.RandomGenerator;

public enum Permissions {
	
	ADMIN("ADMIN"), 
	GUARDIAN("GUARDIAN");
	
	RandomGenerator rg = new RandomGenerator();
	private String level;
	
	private Permissions(String level) {
		this.level = level;
	}
	
	public AccessCode generateCode() {
		return new AccessCode(level+rg.generateAlphaNumericCode(4, 0, 99));
	}
	
	public String getLevel() {
		return level;
	}
}
