package app.controllers;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.LinkedList;
import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;

import app.entities.*;

import org.apache.commons.math3.exception.NoDataException;
import org.bson.types.ObjectId;
import org.springframework.boot.actuate.trace.http.HttpTrace.Principal;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mongodb.BasicDBObject;

import app.controllers.models.AnnouncementModel;
import app.controllers.models.RegisterModel;
import app.database.*;
import app.exceptions.DuplicateEntityException;

@RestController
public class AppController {

	private User u;
	
	@PostConstruct
    public void postConstruct() {
        DatabaseController.INSTANCE.startDB();
    }
	
	@RequestMapping("/register")
	public User registerUser(HttpServletResponse response, @RequestBody RegisterModel model) {
		try {
			u = new User(model.email, model.firstName, model.lastName, model.password, model.passwordConf, null);
		} catch(InvalidParameterException | JsonProcessingException | DuplicateEntityException e) {
			sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
		}

		response.setStatus(HttpServletResponse.SC_OK);
		return u;
	}

	@RequestMapping(method = RequestMethod.POST, value = "/create-announcement")
	public Announcement createAnnouncement(HttpServletResponse response, Authentication authentication, @RequestBody AnnouncementModel model) {
		try {
			User user = (User)authentication.getDetails();
			Announcement a = FeedController.INSTANCE.createAnnouncement(user, model.title, model.description, model.address, model.race, model.age, model.size, model.params);
			response.setStatus(HttpServletResponse.SC_OK);
			return a;
		} catch (JsonProcessingException e) {
			sendError(response, HttpServletResponse.SC_PRECONDITION_FAILED, e.getMessage());
		}

		return null;
	}
	
	@RequestMapping(method = RequestMethod.POST, value = "/update-announcement/{announcementId}")
	public Announcement updateAnnouncement(HttpServletResponse response, Authentication authentication, @RequestBody AnnouncementModel model, @PathVariable String announcementId) {
		try {
			User user = (User)authentication.getDetails();
			Announcement a = FeedController.INSTANCE.getAnnouncementById(announcementId);
			
			if(!a.getUser().equals(user.get_id())) {
	        	throw new BadCredentialsException("Not authorized."); 
	        }
			
			FeedController.INSTANCE.updateAnnouncement(a, model.title, model.description, model.address, model.race, model.age, model.size, model.params);
			response.setStatus(HttpServletResponse.SC_OK);
			return a;
		} catch (NoDataException | IOException e) {
			sendError(response, HttpServletResponse.SC_PRECONDITION_FAILED, e.getMessage());
		}

		return null;
	}

	@RequestMapping("/feed")
	private LinkedList<Announcement> feed(HttpServletResponse response, Authentication authentication){
		User user = (User)authentication.getDetails();
		LinkedList<Announcement> announcementsList = new LinkedList<>();
		try{
			announcementsList = FeedController.INSTANCE.getAllAnnouncements();
		} catch (IOException e) {
			sendError(response, HttpServletResponse.SC_PRECONDITION_FAILED, e.getMessage());
		}
		return announcementsList;
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/get-announcement/{announcementId}")
	private Announcement getAnnouncement(HttpServletResponse response, Authentication authentication, @PathVariable String announcementId) {
		Announcement announcement = null; 
		try{
			announcement = FeedController.INSTANCE.getAnnouncementById(announcementId);
			announcement = FeedController.INSTANCE.getOnePic(announcement);
		} catch (IOException e) {
			sendError(response, HttpServletResponse.SC_PRECONDITION_FAILED, e.getMessage());
		}
		return announcement;
	}
  
	@RequestMapping("/my-announcements")
	private LinkedList<Announcement> myAnnouncements(HttpServletResponse response, Authentication authentication){
		User  user = (User)authentication.getDetails();
		LinkedList<Announcement> announcementsList = new LinkedList<>();
		try{
			announcementsList = FeedController.INSTANCE.getMyAnnouncements(user);
		} catch (IOException e) {
			sendError(response, HttpServletResponse.SC_PRECONDITION_FAILED, e.getMessage());
		}
		return announcementsList;
	}

	@RequestMapping("/logout")
	private void logout(HttpServletResponse response) {
		DatabaseController.INSTANCE.closeDB();
	}
	
	@RequestMapping(method = RequestMethod.POST, value = "/authenticate")
    public User authenticate(HttpServletResponse response, @RequestBody Credentials credentials) throws IllegalAccessException, NoDataException, IOException {
		User  user = AccountController.INSTANCE.authenticate(credentials);
		
		String token = AccountController.INSTANCE.generateToken(user);
        
        if(response!=null)
            response.addHeader("Set-Authorization", token);
        
        return user;
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/get-photos/{announcementId}")
	private LinkedList<Photo> getPhotos(HttpServletResponse response, Authentication authentication, @PathVariable String announcementId) {
		LinkedList<Photo> photos = null;
		
		try {	        
			photos = GalleryController.INSTANCE.getPhotos(announcementId);
		} catch (IOException e) {
			e.printStackTrace();
			sendError(response, HttpServletResponse.SC_PRECONDITION_FAILED, e.getMessage());
		}
		
		return photos;
	}
	
	@PostMapping("/upload-image") // //new annotation since 4.3
    public Photo uploadImage(HttpServletResponse response, @RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes, Authentication authentication, String announcementId) {
		
		Photo photo = null;
		
		try {
			User user = (User)authentication.getDetails();
			Announcement announcement = FeedController.INSTANCE.getAnnouncementById(announcementId);
        
	        if (file.isEmpty()) {
	            throw new Exception("Please select a file to upload");
	        }
	        
	        if(!announcement.getUser().equals(user.get_id())) {
	        	throw new BadCredentialsException("Not authorized."); 
	        }
	        
	        return GalleryController.INSTANCE.uploadImage(file, user, announcement);
		}
		catch(Exception e) {
			e.printStackTrace();
			sendError(response, HttpServletResponse.SC_PRECONDITION_FAILED, e.getMessage());
		}
		
		return photo;
	}
	
	@PostMapping("/remove-image")
    public Boolean removeFile(HttpServletResponse response, RedirectAttributes redirectAttributes, Authentication authentication, String fileId) {
		try {
			User user = (User)authentication.getDetails();

			GalleryController.INSTANCE.removeFile(user, fileId);
		} catch (NoDataException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			sendError(response, HttpServletResponse.SC_PRECONDITION_FAILED, e.getMessage());
		}
		
		return true;
	}

	@RequestMapping(method = RequestMethod.POST, value = "/get-announcement/{announcementId}/addAsFavorite")
	public void addAsFavorite(HttpServletResponse response, Authentication authentication, @PathVariable String announcementId){
		try{
			User user = (User)authentication.getDetails();
			FeedController.INSTANCE.addAsFavorite(announcementId, user);
			response.setStatus(HttpServletResponse.SC_OK);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
		}
	}

	@RequestMapping(method = RequestMethod.POST, value = "/get-announcement/{announcementId}/removeFromFavorites")
	public void removeFromFavorites(HttpServletResponse response, Authentication authentication, @PathVariable String announcementId){
		try{
			User user = (User)authentication.getDetails();
			FeedController.INSTANCE.removeFromFavorites(announcementId, user);
			response.setStatus(HttpServletResponse.SC_OK);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
		}
	}

	@RequestMapping(method = RequestMethod.GET, value = "/get-announcement/getFavorites")
	public LinkedList<Announcement> getFavorites(HttpServletResponse response, Authentication authentication){
		LinkedList<Announcement> favorites = new LinkedList<>();
		try{
			User user = (User)authentication.getDetails();
			favorites = FeedController.INSTANCE.getFavorites(user);
			response.setStatus(HttpServletResponse.SC_OK);
		} catch (IOException e) {
			e.printStackTrace();
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
		return favorites;
	}


	private HttpServletResponse sendError(HttpServletResponse response, int sc, String message) {
		response.setStatus(sc);
		try {
			response.sendError(sc, message);		
		} catch(IOException i) {
			i.printStackTrace();
		}
		
		return response;
	}
	
}
