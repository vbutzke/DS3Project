package app.controllers;
import java.io.IOException;
import java.security.InvalidParameterException;

import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;

import app.entities.Administrator;
import app.entities.Adopter;
import app.entities.EntityExample;
import app.entities.Guardian;
import app.entities.User;

@RestController
public class AppController {
	
	@RequestMapping("/")
	public String works(HttpServletResponse response) {
		EntityExample e = new EntityExample();
		if(e.someMethod()) {
			response.setStatus( HttpServletResponse.SC_OK  );
		}
		return "Funcionou! \n" + response;
	}
	
	@RequestMapping("/register")
	public User registerUser(String email, String firstName, String lastName, String password, String passwordConf, String code, HttpServletResponse response) {
		User u = null;
		try {
			if(code.contains("admin")) {
				u = new Administrator(email, firstName, lastName, password, passwordConf, code);
			} else if (code.contains("guardian")) {
				u = new Guardian(email, firstName, lastName, password, passwordConf, code);
			} else if(code.isEmpty()) {
				u = new Adopter(email, firstName, lastName, password, passwordConf);
			} else {
				sendError(response, HttpServletResponse.SC_PRECONDITION_FAILED, "Invalid access code.");
			}
		} catch(InvalidParameterException | JsonProcessingException e) {
			sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
		}

		response.setStatus(HttpServletResponse.SC_OK);
		return u;
	}
	
	private HttpServletResponse sendError(HttpServletResponse response, int sc, String message) {
		response.setStatus( sc );
		try {
			response.sendError(sc, message);		
		} catch(IOException i) {
			i.printStackTrace();
		}
		
		return response;
	}
	
}
