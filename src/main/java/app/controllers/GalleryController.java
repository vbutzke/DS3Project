package app.controllers;

import java.io.IOException;
import java.util.Base64;
import java.util.LinkedList;

import org.apache.commons.math3.exception.NoDataException;
import org.bson.types.ObjectId;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.multipart.MultipartFile;

import com.mongodb.BasicDBObject;

import app.database.DatabaseController;
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

	public Photo uploadImage(MultipartFile file, User user, Announcement announcement) throws IOException {
        // Get the file and save it somewhere
        byte[] bytes = file.getBytes();
        String base64 = Base64.getEncoder().encodeToString(bytes);
        
        Photo photo = new  Photo();
        photo.setAnnouncementId(announcement.get_id());
        photo.setImage(base64);
        
        photo.set_id((MongoDbId)DatabaseController.INSTANCE.addObject(photo, "photos"));
        
        return photo;
    }
	
    @SuppressWarnings("SameReturnValue")
    public void removeFile(User user, String photoId) throws NoDataException, IOException {
		
		Photo photo = this.getPhotoById(photoId);
        Announcement announcement = FeedController.INSTANCE.getAnnouncementById(photo.getAnnouncementId());
        
        if(!announcement.getUser().equals(user.get_id())) {
        	throw new BadCredentialsException("Not authorized"); 
        }
        
        BasicDBObject filter = new BasicDBObject();
		filter.append("_id", new ObjectId(photoId));
		
		DatabaseController.INSTANCE.removeObject(filter, "photos");

    }
	
	private Photo createPhoto(String announcementId, MultipartFile file) throws IOException {
		byte[] bytes = file.getBytes();
        String base64 = Base64.getEncoder().encodeToString(bytes);
        
        Photo p = new Photo();
        
        p.setAnnouncementId(announcementId);
        p.setImage(base64);
            
		DatabaseController.INSTANCE.addObject(p, "photos");
		
		return p;
	}
	
	 public LinkedList<Photo> getPhotosByAnnouncements(Announcement announcement) throws IOException {
        LinkedList<Photo> photosList = new LinkedList<>();
        
        BasicDBObject filter = new BasicDBObject();
        
        filter.append("announcementId", announcement.get_id());
        
        LinkedList<Object> objectsList = DatabaseController.INSTANCE.getList(filter, "photos", Photo.class);

         for (Object anObjectsList : objectsList) {
             photosList.add((Photo) anObjectsList);
         }

        return photosList;
     }
	 
	 public Photo getPhotoById(String photoId) throws NoDataException, IOException {
		BasicDBObject filter = new BasicDBObject();
		filter.append("_id", new ObjectId(photoId));
		return (Photo)DatabaseController.INSTANCE.filter(filter, "photos", Photo.class);	        
	 }
}
