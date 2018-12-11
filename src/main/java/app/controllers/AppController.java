package app.controllers;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.LinkedList;
import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;

import app.controllers.models.CommentModel;
import app.entities.*;
import app.entities.Thread;
import org.apache.commons.math3.exception.NoDataException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
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
			u = new User(model.email, model.firstName, model.lastName, model.password, model.passwordConf);
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
		} catch (IOException e) {
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
			a.save();
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
			announcement = FeedController.INSTANCE.getOnePic(FeedController.INSTANCE.getAnnouncementById(announcementId));
		} catch (IOException e) {
			sendError(response, HttpServletResponse.SC_PRECONDITION_FAILED, e.getMessage());
		}
		return announcement;
	}

	@RequestMapping(method = RequestMethod.GET, value = "/get-announcement/{announcementId}/getThread")
	private Thread getThread(HttpServletResponse response, Authentication authentication, @PathVariable String announcementId) {
		try{
			return FeedController.INSTANCE.getAnnouncementById(announcementId).getThreadFromDB();
		} catch (IOException e) {
			sendError(response, HttpServletResponse.SC_PRECONDITION_FAILED, e.getMessage());
		}
		return null;
	}

	@RequestMapping(method = RequestMethod.POST, value = "/get-announcement/{announcementId}/deleteComment")
	private Thread addComment(HttpServletResponse response, Authentication authentication, @PathVariable String announcementId, int commentPos) {
		Thread thread = null;
		try{
			Announcement a = FeedController.INSTANCE.getAnnouncementById(announcementId);
			thread = a.removeComment(commentPos);
			a.save();

		} catch (IOException e) {
			sendError(response, HttpServletResponse.SC_PRECONDITION_FAILED, e.getMessage());
		}
		return thread;
	}

	@PostMapping("/get-announcement/{announcementId}/addComment")
	private Thread deleteComment(HttpServletResponse response, Authentication authentication, @RequestBody CommentModel model, @PathVariable String announcementId) {
		Thread thread = null;
		try{
			Announcement a = FeedController.INSTANCE.getAnnouncementById(announcementId);
			thread = a.addComment(new Comment(model.userId, model.comment));
			a.save();
		} catch (IOException e) {
			sendError(response, HttpServletResponse.SC_NOT_MODIFIED, e.getMessage());
		}
		return thread;
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
    public User authenticate(HttpServletResponse response, @RequestBody Credentials credentials) throws NoDataException, IOException {
		User  user = AccountController.INSTANCE.authenticate(credentials);
		try {
			String token = AccountController.INSTANCE.generateToken(user);

			if (response != null)
				response.addHeader("Set-Authorization", token);
		} catch(IOException e){
			e.printStackTrace();
		}
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
		
		try {
			User user = (User)authentication.getDetails();
			Announcement announcement = FeedController.INSTANCE.getAnnouncementById(announcementId);
        
	        if (file.isEmpty()) {
	            throw new Exception("Please select a file to upload");
	        }
	        
	        if(!announcement.getUser().equals(user.get_id())) {
	        	throw new BadCredentialsException("Not authorized."); 
	        }
	        Photo p = GalleryController.INSTANCE.uploadImage(file, user, announcement);
	        announcement.save();
	        return p;
		}
		catch(Exception e) {
			e.printStackTrace();
			sendError(response, HttpServletResponse.SC_PRECONDITION_FAILED, e.getMessage());
		}
		
		return null;
	}
	
	@SuppressWarnings("SameReturnValue")
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
	
	private void sendError(HttpServletResponse response, int sc, String message) {
		response.setStatus(sc);
		try {
			response.sendError(sc, message);		
		} catch(IOException i) {
			i.printStackTrace();
		}

	}
	
}
