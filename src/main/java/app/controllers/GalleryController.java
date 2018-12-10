package app.controllers;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.LinkedList;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.math3.exception.NoDataException;
import org.bson.types.ObjectId;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mongodb.BasicDBObject;

import app.database.DatabaseController;
import app.database.DatabaseFilter;
import app.entities.Announcement;
import app.entities.Photo;
import app.entities.User;
import app.utils.MongoDbId;

public enum GalleryController {
	INSTANCE();
	
	public LinkedList<Photo> getPhotos(String announcementId) throws IOException {
		Announcement announcement = FeedController.INSTANCE.getAnnouncementById(announcementId);
		return this.getPhotosByAnnouncements(announcement);
	}
	
	public Photo getPhoto(String photoId) throws IOException {
		BasicDBObject filter = new BasicDBObject();
        
        filter.append("_id", new ObjectId(photoId));
        
        LinkedList<Object> objectsList = DatabaseController.INSTANCE.getList(filter, "photos", Photo.class);

        Photo photo = null;
        
        if(!objectsList.isEmpty()){
        	photo = (Photo)objectsList.getFirst();
        }

        return photo;
	}

//	public Photo uploadImage(MultipartFile file, User user, Announcement announcement) throws IOException {
//        // Get the file and save it somewhere
//        byte[] bytes = file.getBytes();
//        String base64 = Base64.getEncoder().encodeToString(bytes);
//        
//        Photo photo = new  Photo();
//        photo.setAnnouncementId(announcement.get_id());
//        photo.setImage(base64);
//        
//        photo.set_id((MongoDbId)DatabaseController.INSTANCE.addObject(photo, "photos"));
//        
//        return photo;
//    }
	
    public Boolean removeFile(User user, String photoId) throws NoDataException, IOException {
		
		Photo photo = this.getPhotoById(photoId);
        Announcement announcement = FeedController.INSTANCE.getAnnouncementById(photo.getAnnouncementId());
        
        if(!announcement.getUser().equals(user.get_id())) {
        	throw new BadCredentialsException("Not authorized"); 
        }
        
        BasicDBObject filter = new BasicDBObject();
		filter.append("_id", new ObjectId(photoId));
		
		DatabaseController.INSTANCE.removeObject(filter, "photos");
		
        return true;
    }

//	private Photo createPhoto(String announcementId, MultipartFile file) throws IOException {
//		byte[] bytes = file.getBytes();
//        String base64 = Base64.getEncoder().encodeToString(bytes);
//        
//        Photo p = new Photo();
//        
//        p.setAnnouncementId(announcementId);
//        p.setImage(base64);
//            
//		DatabaseController.INSTANCE.addObject(p, "photos");
//		
//		return p;
//	}
	
	 public LinkedList<Photo> getPhotosByAnnouncements(Announcement announcement) throws IOException {
        LinkedList<Photo> photosList = new LinkedList<>();
        
        BasicDBObject filter = new BasicDBObject();
        
        filter.append("announcementId", announcement.get_id());
        
        LinkedList<Object> objectsList = DatabaseController.INSTANCE.getList(filter, "photos", Photo.class);

        for(int i=0; i<objectsList.size(); i++){
        	photosList.add((Photo) objectsList.get(i));
        }

        return photosList;
     }
	 
	 public Photo getPhotoById(String photoId) throws NoDataException, IOException {
		BasicDBObject filter = new BasicDBObject();
		filter.append("_id", new ObjectId(photoId));
		return (Photo)DatabaseController.INSTANCE.filter(filter, "photos", Photo.class);	        
	 }
}
