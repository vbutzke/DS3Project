package app.controllers;

import app.database.DatabaseController;
import app.database.DatabaseFilter;
import app.entities.Address;
import app.entities.Announcement;
import app.entities.AnnouncementParams;
import app.entities.Photo;
import app.entities.User;
import app.utils.MongoDbId;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mongodb.BasicDBObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
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
        	Announcement a = (Announcement) objectsList.get(i);
        	a = getOnePic(a);
            announcementsList.add(a);
        }

        return announcementsList;
    }

    public LinkedList<Announcement> getAllAnnouncements() throws IOException {
        LinkedList<Announcement> announcementsList = new LinkedList<>();
        LinkedList<Object> objectsList             = DatabaseController.INSTANCE.getAllObjectsFromCollection("announcements", Announcement.class);

        for(int i=0; i<objectsList.size(); i++){
        	Announcement a = (Announcement) objectsList.get(i);
        	a = getOnePic(a);
            announcementsList.add(a);
        }

        return announcementsList;
    }

    public Announcement createAnnouncement(User user, String title,  String description, Address address, String race, String age, String size, ArrayList<AnnouncementParams> params) throws JsonProcessingException {
		Announcement a = new Announcement(title, description, address, race, age, size);
		
		a.setParams(params);
		a.setUser(user.get_id());
		a.setCreatedAt(new Date());
		
		a.set_id((MongoDbId)DatabaseController.INSTANCE.addObject(a, "announcements"));
		
		return a;
	}
    
    public Announcement updateAnnouncement(Announcement announcement, String title,  String description, Address address, String race, String age, String size, ArrayList<AnnouncementParams> params) throws JsonProcessingException {
    	announcement.setTitle(title);
    	announcement.setDescription(description);
    	announcement.setAddress(address);
    	announcement.setRace(race);
    	announcement.setAge(age);
    	announcement.setSize(size);
    	announcement.setParams(params);
    	
		DatabaseController.INSTANCE.updateObject(announcement, "announcements");

		return announcement;
	}
    
    public Announcement getOnePic(Announcement announcement) throws IOException {
    	LinkedList<Photo> photos = GalleryController.INSTANCE.getPhotosByAnnouncements(announcement);
    	
    	if(!photos.isEmpty()) {
    		Photo p = photos.getFirst();
    		announcement.setAvatar(p);
    	}
    	
    	return announcement;
    }

    public void addAsFavorite(String announcementId, User user) throws JsonProcessingException {
        user.getFavoriteAnnouncements().add(announcementId);
        user.setFavoriteAnnouncements(user.getFavoriteAnnouncements());
        DatabaseController.INSTANCE.updateObject(user, user.getCollection());
    }

    public void removeFromFavorites(String announcementId, User user) throws JsonProcessingException {
        user.getFavoriteAnnouncements().remove(announcementId);
        user.setFavoriteAnnouncements(user.getFavoriteAnnouncements());
        DatabaseController.INSTANCE.updateObject(user, user.getCollection());
    }

}
