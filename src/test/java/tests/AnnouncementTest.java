package tests;

import app.database.DatabaseController;
import app.entities.Announcement;
import app.entities.Guardian;
import app.entities.User;
import app.exceptions.DuplicateEntityException;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import singletons.UserRecord;

import static org.junit.Assert.assertTrue;

public class AnnouncementTest extends AbstractUT {

    private User guardian;
    @Before
    public void start() {
        startDB();

        try {
            guardian = new Guardian(UserRecord.GUARDIAN.getEmail(), UserRecord.GUARDIAN.getFirstName(), UserRecord.GUARDIAN.getLastName(), UserRecord.GUARDIAN.getPassword(), UserRecord.GUARDIAN.getPasswordConf(), UserRecord.GUARDIAN.getCode());
        } catch (JsonProcessingException | DuplicateEntityException e) {
            e.printStackTrace();
            try {
                DatabaseController.INSTANCE.removeObject(guardian, "user");
            } catch (JsonProcessingException e1) {
                e1.printStackTrace();
            }
        }
    }

    @After
    public void end() {
        try {
            DatabaseController.INSTANCE.removeObject(guardian, "user");
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        closeDB();
    }

    @Test
    public void createAnnouncement(){
        Announcement announcement = null;
        try {
            announcement = guardian.createAnnouncement();
            assertTrue(DatabaseController.INSTANCE.findRecord(announcement, "announcements"));
        } catch (JsonProcessingException | IllegalAccessException e) {
            e.printStackTrace();
        } finally {
            try {
                DatabaseController.INSTANCE.removeObject(announcement, "announcements");
            } catch (JsonProcessingException e1) {
                e1.printStackTrace();
            }
        }
    }

}
