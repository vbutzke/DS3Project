package app.controllers;

import app.database.DatabaseController;
import app.entities.User;
import app.entities.UserModel;
import app.entities.Credentials;
import app.security.services.JwtService;
import app.security.JwtUser;
import app.entities.Adopter;
import app.entities.Guardian;
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
            
        UserModel model = new UserModel();
        
        model.email = credentials.getUsername();
        model.password = credentials.getPassword();

        User user = DatabaseController.INSTANCE.getRecord(model, "user", Adopter.class);

        String token = this.generateToken(user);
        
        if(response!=null)
            response.addHeader("Set-Authorization", "Bearer " + token);
        
        return user;
    }

    private String generateToken(User user) {
        JwtUser jwtUser = new JwtUser();
        jwtUser.setUserName(user.getEmail());
        jwtUser.setRole("ADOPTER"); //Todo: Role hard coded

        JwtService service = new JwtService();
        return service.getToken(jwtUser);
    }
}
