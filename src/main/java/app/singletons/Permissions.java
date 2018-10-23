package app.singletons;

import app.entities.AccessCode;
import app.utils.RandomGenerator;

@SuppressWarnings("unused")
public enum Permissions {
	
	ADMIN("ADMIN"), 
	GUARDIAN("GUARDIAN");
	
	final RandomGenerator rg = new RandomGenerator();
	private final String level;
	
	@SuppressWarnings("unused")
	Permissions(String level) {
		this.level = level;
	}
	
	public AccessCode generateCode() {
		return new AccessCode(level+rg.generateAlphaNumericCode(4, 0, 99));
	}
	
	public String getLevel() {
		return level;
	}
}
