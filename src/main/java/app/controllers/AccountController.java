package app.controllers;

import app.database.DatabaseController;
import app.database.DatabaseFilter;
import app.entities.User;
import app.entities.Credentials;
import app.security.services.JwtService;
import com.fasterxml.jackson.core.JsonProcessingException;

import org.apache.commons.math3.exception.NoDataException;
import org.bson.Document;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.LinkedList;

import javax.servlet.http.HttpServletResponse;

@RestController
public class AccountController {
    AccountController(){

    }

    @RequestMapping(method = RequestMethod.POST, value = "/authenticate")
    public User authenticate(HttpServletResponse response, @RequestBody Credentials credentials) throws IllegalAccessException, NoDataException, IOException {
            
        DatabaseFilter model = new DatabaseFilter();
        
        model.add("email", credentials.getUsername());
        model.add("password", credentials.getPassword());

        User user = (User)DatabaseController.INSTANCE.filter(model, "user", User.class);

        String token = this.generateToken(user);
        
        if(response!=null)
            response.addHeader("Set-Authorization", "Bearer " + token);
        
        return user;
    }

    private String generateToken(User user) {
        JwtService service = new JwtService();
        return service.getToken(user);
    }
}
