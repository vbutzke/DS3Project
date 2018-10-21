package app.controllers;
import java.io.IOException;
import java.security.InvalidParameterException;
import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;

import app.entities.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.fasterxml.jackson.core.JsonProcessingException;
import app.database.*;
import app.exceptions.DuplicateEntityException;

@RestController
public class AppController {

	User u;

	@PostConstruct
    public void postConstruct() {
        DatabaseController.INSTANCE.startDB();
    }
	
	@RequestMapping("/register")
	public User registerUser(String email, String firstName, String lastName, String password, String passwordConf, String code, HttpServletResponse response) {
		System.out.println("Code: "+code);
		
		try {
			if(code == null || code.isEmpty() || code.equals("\"\"")) {
				u = new Adopter(email, firstName, lastName, password, passwordConf);
			} else if(code.contains("ADMIN")) {
				u = new Administrator(email, firstName, lastName, password, passwordConf, new AccessCode(code));
			} else if (code.contains("GUARDIAN")) {
				u = new Guardian(email, firstName, lastName, password, passwordConf, new AccessCode(code));
			} else {
				sendError(response, HttpServletResponse.SC_PRECONDITION_FAILED, "Invalid access code.");
			}
		} catch(InvalidParameterException | JsonProcessingException | DuplicateEntityException e) {
			sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
		}

		response.setStatus(HttpServletResponse.SC_OK);
		return u;
	}

	@RequestMapping("/createAnnouncement")
	private String createAnnouncement(HttpServletResponse response){

		try {
			Announcement a = u.createAnnouncement();
			response.setStatus(HttpServletResponse.SC_OK);
			return "ok";
		} catch (JsonProcessingException | IllegalAccessException e) {
			sendError(response, HttpServletResponse.SC_PRECONDITION_FAILED, e.getMessage());
		}

		return "not ok";
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
