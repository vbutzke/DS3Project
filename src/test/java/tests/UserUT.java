package tests;

import org.junit.*;

//import app.entities.Administrator;
//import app.entities.Adopter;
//import app.entities.Guardian;
@Ignore
public class UserUT extends AbstractUT{

	@Before
	public void start() {
		startDB();
	}
	
	@After
	public void end() {
		resetAccessCodes();
		closeDB();
	}

//	@Test
//	public void createAdminUser() {
//		User admin = null;
//		try {
//			admin = new Administrator(UserRecord.ADMINISTRATOR.getEmail(), UserRecord.ADMINISTRATOR.getFirstName(), UserRecord.ADMINISTRATOR.getLastName(), UserRecord.ADMINISTRATOR.getPassword(), UserRecord.ADMINISTRATOR.getPasswordConf(), UserRecord.ADMINISTRATOR.getCode());
//			assertTrue(findRecord(admin, "user"));
//			DatabaseController.INSTANCE.removeObject(admin, "user");
//		} catch (InvalidParameterException | JsonProcessingException | DuplicateEntityException e) {
//			try {
//				DatabaseController.INSTANCE.removeObject(admin, "user");
//			} catch (JsonProcessingException e1) {
//				e1.printStackTrace();
//			}
//			e.printStackTrace();
//		}
//	}
//	
//	@Test
//	public void createAdopter() {
//		User adopter = null;
//		try {
//			adopter = new Adopter(UserRecord.ADOPTER.getEmail(), UserRecord.ADOPTER.getFirstName(), UserRecord.ADOPTER.getLastName(), UserRecord.ADOPTER.getPassword(), UserRecord.ADOPTER.getPasswordConf());
//			assertTrue(findRecord(adopter, "user"));
//			DatabaseController.INSTANCE.removeObject(adopter, "user");
//		} catch (InvalidParameterException | JsonProcessingException | DuplicateEntityException e) {
//			try {
//				DatabaseController.INSTANCE.removeObject(adopter, "user");
//			} catch (JsonProcessingException e1) {
//				e1.printStackTrace();
//			}
//			e.printStackTrace();
//		}
//	}
//	
//	@Test
//	public void createGuardian() {
//		User guardian = null;
//		try {
//			guardian = new Guardian(UserRecord.GUARDIAN.getEmail(), UserRecord.GUARDIAN.getFirstName(), UserRecord.GUARDIAN.getLastName(), UserRecord.GUARDIAN.getPassword(), UserRecord.GUARDIAN.getPasswordConf(), UserRecord.GUARDIAN.getCode());
//			assertTrue(findRecord(guardian, "user"));
//			DatabaseController.INSTANCE.removeObject(guardian, "user");
//		} catch (InvalidParameterException | JsonProcessingException | DuplicateEntityException e) {
//			try {
//				DatabaseController.INSTANCE.removeObject(guardian, "user");
//			} catch (JsonProcessingException e1) {
//				e1.printStackTrace();
//			}
//			e.printStackTrace();
//		}
//	}
}
