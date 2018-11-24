package app.controllers;

import java.io.IOException;
import java.util.Base64;
import java.util.LinkedList;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.math3.exception.NoDataException;
import org.bson.types.ObjectId;
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

@RestController
public class GalleryController {
	
	@RequestMapping(method = RequestMethod.GET, value = "/get-photos/{announcementId}")
	private LinkedList<Photo> getPhotos(HttpServletResponse response, Authentication authentication, @PathVariable String announcementId) {
		LinkedList<Photo> photos = null; 
		try{
			Announcement announcement = FeedController.INSTANCE.getAnnouncementById(announcementId);
			photos = this.getPhotosByAnnouncements(announcement);
		} catch (IOException e) {
			
		}
		return photos;
	}

	@PostMapping("/upload-image") // //new annotation since 4.3
    public Boolean uploadImage(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes, Authentication authentication, String announcementId) throws IOException {

		BasicDBObject filter = new BasicDBObject();
    	
        filter.append("_id", new ObjectId(announcementId));
        
        Announcement announcement = (Announcement)DatabaseController.INSTANCE.filter(filter, "announcements", Announcement.class);
        
        User user = (User)authentication.getDetails();
        
        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "Please select a file to upload");
            return false;
        }
        
        if(!announcement.getUser().equals(user.get_id())) {
        	redirectAttributes.addFlashAttribute("message", "Not authorized."); 
        	return false;
        }

        try {

            // Get the file and save it somewhere
            byte[] bytes = file.getBytes();
            String base64 = Base64.getEncoder().encodeToString(bytes);
            
            Photo photo = new  Photo();
            photo.setAnnouncementId(announcement.get_id());
            photo.setImage(base64);
            
            DatabaseController.INSTANCE.addObject(photo, "photos");

        } catch (IOException e) {
            e.printStackTrace();
        }

        return true;
    }
	
	@PostMapping("/remove") // //new annotation since 4.3
    public Boolean removeFile(RedirectAttributes redirectAttributes, Authentication authentication, String fileId) throws NoDataException, IOException {
		BasicDBObject filter = new BasicDBObject();
    	
        filter.append("_id", new ObjectId(fileId));
        
        Photo photo= (Photo)DatabaseController.INSTANCE.filter(filter, "photos", Photo.class);
        
        User user = (User)authentication.getDetails();
        
        BasicDBObject filterAnnouncement = new BasicDBObject();
        filterAnnouncement.append("_id", new ObjectId(photo.getAnnouncementId()));
        Announcement announcement = (Announcement)DatabaseController.INSTANCE.filter(filterAnnouncement, "announcements", Announcement.class);
        
        if(!announcement.getUser().equals(user.get_id())) {
        	redirectAttributes.addFlashAttribute("message", "Not authorized."); 
        	return false;
        }
		DatabaseController.INSTANCE.removeObject(filter, "photos");
		
        return true;
    }
	
	private Photo createPhoto(String announcementId, MultipartFile file) throws IOException {
		// Get the file and save it somewhere
		byte[] bytes = file.getBytes();
        String base64 = Base64.getEncoder().encodeToString(bytes);
        
        Photo p = new Photo();
        
        p.setAnnouncementId(announcementId);
        p.setImage(base64);
            
		DatabaseController.INSTANCE.addObject(p, "photos");
		
		return p;
	}
	
	 private LinkedList<Photo> getPhotosByAnnouncements(Announcement announcement) throws IOException {
	        LinkedList<Photo> photosList = new LinkedList<>();
	        
	        BasicDBObject filter = new BasicDBObject();
	        
	        filter.append("announcementId", announcement.get_id());
	        
	        LinkedList<Object> objectsList = DatabaseController.INSTANCE.getList(filter, "photos", Photo.class);

	        for(int i=0; i<objectsList.size(); i++){
	        	photosList.add((Photo) objectsList.get(i));
	        }

	        return photosList;
	    }
}
