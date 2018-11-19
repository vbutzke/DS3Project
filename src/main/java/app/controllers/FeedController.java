package app.controllers;

import app.database.DatabaseController;
import app.entities.Address;
import app.entities.Announcement;
import app.entities.Guardian;
import app.entities.User;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.IOException;
import java.util.LinkedList;

public enum FeedController {
    INSTANCE();

    FeedController(){

    }

    public LinkedList<Announcement> getAllAnnouncements() throws IOException {
        LinkedList<Announcement> announcementsList = new LinkedList<>();
        LinkedList<Object> objectsList             = DatabaseController.INSTANCE.getAllObjectsFromCollection("announcements", Announcement.class);

        for(int i=0; i<objectsList.size(); i++){
            announcementsList.add((Announcement) objectsList.get(i));
        }

        return announcementsList;
    }

    public Announcement createAnnouncement(User user, String title, String description, Address address, String race, int age, String size) throws JsonProcessingException, IllegalAccessException {
        if(user.getPermission().equals("Guardian")){
            return Guardian.createNewAnnouncement(title, description, address, race, age, size);
        }

        throw new IllegalAccessException("User doesn't have permission to add new announcement");
    }
}
