package app.controllers;

import app.database.DatabaseController;
import app.database.DatabaseFilter;
import app.entities.Address;
import app.entities.Announcement;
import app.entities.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.mongodb.BasicDBObject;

import java.io.IOException;
import java.util.LinkedList;

import org.apache.commons.math3.exception.NoDataException;
import org.bson.types.ObjectId;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

public enum FeedController {
    INSTANCE();

    FeedController(){

    }
    
    public Announcement getAnnouncementById(String announcementId) throws NoDataException, IOException {
    	BasicDBObject filter = new BasicDBObject();
    	
        filter.append("_id", new ObjectId(announcementId));
        
        Announcement result = (Announcement)DatabaseController.INSTANCE.filter(filter, "announcements", Announcement.class);
        
        return result;
    }
    
    public LinkedList<Announcement> getMyAnnouncements(User user) throws IOException {
        LinkedList<Announcement> announcementsList = new LinkedList<>();
        
        BasicDBObject filter = new BasicDBObject();
        
        filter.append("user", user.get_id());
        
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
