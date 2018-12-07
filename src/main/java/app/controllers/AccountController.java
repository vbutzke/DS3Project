package app.controllers;

import app.database.DatabaseController;
import app.database.DatabaseFilter;
import app.entities.User;
import app.entities.Credentials;
import app.security.services.JwtService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.mongodb.BasicDBObject;

import org.apache.commons.math3.exception.NoDataException;
import org.bson.Document;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.LinkedList;

import javax.servlet.http.HttpServletResponse;

public enum AccountController {
	INSTANCE();
	
    AccountController(){

    }

    public User authenticate(Credentials credentials) throws IllegalAccessException, NoDataException, IOException {
    	BasicDBObject model = new BasicDBObject();
        
        model.append("email", credentials.getUsername());
        model.append("password", credentials.getPassword());

        return (User)DatabaseController.INSTANCE.filter(model, "user", User.class);
    }

    public String generateToken(User user) {
        JwtService service = new JwtService();
        return service.getToken(user);
    }
}
