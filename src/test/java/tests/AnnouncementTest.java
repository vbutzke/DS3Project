package tests;

import app.controllers.FeedController;
import app.database.DatabaseController;
import app.entities.Announcement;
import app.entities.Guardian;
import app.entities.User;
import app.exceptions.DuplicateEntityException;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import singletons.AnnouncementsRecord;
import singletons.UserRecord;

import java.io.IOException;
import java.util.LinkedList;

import static org.junit.Assert.assertEquals;
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
        resetAnnouncements();
        resetAccessCodes();
        closeDB();
    }

    @Test
    public void createAnnouncement(){
        Announcement announcement = null;
        try {
            announcement = FeedController.INSTANCE.createAnnouncement(guardian, AnnouncementsRecord.BLACK_DOG.getTitle(), AnnouncementsRecord.BLACK_DOG.getDescription(), AnnouncementsRecord.BLACK_DOG.getAddress(), AnnouncementsRecord.BLACK_DOG.getRace(), AnnouncementsRecord.BLACK_DOG.getAge(), AnnouncementsRecord.BLACK_DOG.getSize());
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

    @Test
    public void getAllAnnouncements(){
        LinkedList<Announcement> announcementsList = new LinkedList<>();
        try {
            for(int i=0; i<5; i++) {
                announcementsList.add(FeedController.INSTANCE.createAnnouncement(guardian, AnnouncementsRecord.BLACK_DOG.getTitle(), AnnouncementsRecord.BLACK_DOG.getDescription(), AnnouncementsRecord.BLACK_DOG.getAddress(), AnnouncementsRecord.BLACK_DOG.getRace(), AnnouncementsRecord.BLACK_DOG.getAge(), AnnouncementsRecord.BLACK_DOG.getSize()));
            }
            LinkedList<Announcement> dbAnnouncementsList = FeedController.INSTANCE.getAllAnnouncements();
            for(int i=0; i<5; i++){
                assertEquals(announcementsList.get(i).getTitle(), dbAnnouncementsList.get(i).getTitle());
            }

        } catch (IllegalAccessException | IOException e) {
            e.printStackTrace();
        } finally {
            try {
                for(int i=0; i<5; i++) {
                    DatabaseController.INSTANCE.removeObject(announcementsList.get(i), "announcements");
                }
            } catch (JsonProcessingException | IndexOutOfBoundsException e1) {
                e1.printStackTrace();
            }
        }
    }

}
