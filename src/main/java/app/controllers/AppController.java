package app.controllers;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.LinkedList;
import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;

import app.entities.*;

import org.springframework.boot.actuate.trace.http.HttpTrace.Principal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import com.fasterxml.jackson.core.JsonProcessingException;

import app.controllers.models.AnnouncementModel;
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
	public User registerUser(String email, String firstName, String lastName, String password, String passwordConf, String code, HttpServletResponse response) {
		System.out.println("Code: "+code);
		
		try {
			if(code == null || code.isEmpty() || code.equals("\"\"")) {
				u = new User(email, firstName, lastName, password, passwordConf, null);
			} else if(code.contains("ADMIN")) {
				u = new User(email, firstName, lastName, password, passwordConf, new AccessCode(code));
			} else if (code.contains("GUARDIAN")) {
				u = new User(email, firstName, lastName, password, passwordConf, new AccessCode(code));
			} else {
				sendError(response, HttpServletResponse.SC_PRECONDITION_FAILED, "Invalid access code.");
			}
		} catch(InvalidParameterException | JsonProcessingException | DuplicateEntityException e) {
			sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
		}

		response.setStatus(HttpServletResponse.SC_OK);
		return u;
	}

	@RequestMapping(method = RequestMethod.POST, value = "/createAnnouncement")
	public String createAnnouncement(HttpServletResponse response, Authentication authentication, @RequestBody AnnouncementModel model) {
		try {
			User user = (User)authentication.getDetails();
			Announcement a = FeedController.INSTANCE.createAnnouncement(user, model.title, model.description, model.address, model.race, model.age, model.size);
			response.setStatus(HttpServletResponse.SC_OK);
			return "ok";
		} catch (JsonProcessingException e) {
			sendError(response, HttpServletResponse.SC_PRECONDITION_FAILED, e.getMessage());
		}

		return "not ok";
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
