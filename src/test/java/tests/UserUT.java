package tests;

import static org.junit.Assert.assertTrue;

import java.security.InvalidParameterException;

import org.junit.*;

import com.fasterxml.jackson.core.JsonProcessingException;

import app.entities.Administrator;
import app.entities.User;
import singletons.UserRecord;

public class UserUT extends AbstractUT{
	
	@Before
	public void start() {
		startDB();
	}
	
	@After
	public void end() {
		closeDB();
	}
	
	@Test
	public void createAdminUser() {
		User admin;
		try {
			admin = new Administrator(UserRecord.ADMINISTRATOR.getEmail(), UserRecord.ADMINISTRATOR.getFirstName(), UserRecord.ADMINISTRATOR.getLastName(), UserRecord.ADMINISTRATOR.getPassword(), UserRecord.ADMINISTRATOR.getPasswordConf(), UserRecord.ADMINISTRATOR.getCode());
			assertTrue(findRecord(admin, "users"));
		} catch (InvalidParameterException | JsonProcessingException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void createAdopter() {
		
	}
	
	@Test
	public void createGuardian() {
		
	}
	
}
