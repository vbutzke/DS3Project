package app.controllers;

import app.database.DatabaseController;
import app.database.DatabaseFilter;
import app.entities.Address;
import app.entities.Announcement;
import app.entities.User;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.IOException;
import java.util.LinkedList;

import org.bson.types.ObjectId;

public enum FeedController {
    INSTANCE();

    FeedController(){

    }
    
    public LinkedList<Announcement> getMyAnnouncements(User user) throws IOException {
        LinkedList<Announcement> announcementsList = new LinkedList<>();
        
        DatabaseFilter filter = new DatabaseFilter();
        
        filter.add("user", user.get_id());
        
        LinkedList<Object> objectsList = DatabaseController.INSTANCE.getList(filter, "announcements", Announcement.class);

        for(int i=0; i<objectsList.size(); i++){
            announcementsList.add((Announcement) objectsList.get(i));
        }

        return announcementsList;
    }

    public LinkedList<Announcement> getAllAnnouncements() throws IOException {
        LinkedList<Announcement> announcementsList = new LinkedList<>();
        LinkedList<Object> objectsList             = DatabaseController.INSTANCE.getAllObjectsFromCollection("announcements", Announcement.class);

        for(int i=0; i<objectsList.size(); i++){
            announcementsList.add((Announcement) objectsList.get(i));
        }

        return announcementsList;
    }

    public Announcement createAnnouncement(User user, String title,  String description, Address address, String race, int age, String size) throws JsonProcessingException {
		Announcement a = new Announcement(title, description, address, race, age, size);
		a.setUser(user.get_id());
		DatabaseController.INSTANCE.addObject(a, "announcements");
		return a;
	}
}
