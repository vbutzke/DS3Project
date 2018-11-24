package tests;

import app.controllers.AccountController;
import app.database.DatabaseController;
import app.database.DatabaseFilter;
import app.entities.Credentials;
import app.entities.User;
import app.exceptions.DuplicateEntityException;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import singletons.UserRecord;

import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.LinkedList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class AccountUT  extends AbstractUT {

    private User adopter;
    @Before
    public void start() {
        startDB();

        try {
            adopter = new User(UserRecord.ADOPTER.getEmail(), UserRecord.ADOPTER.getFirstName(), UserRecord.ADOPTER.getLastName(), UserRecord.ADOPTER.getPassword(), UserRecord.ADOPTER.getPasswordConf(), null);
        } catch (JsonProcessingException | DuplicateEntityException e) {
            e.printStackTrace();
            try {
                DatabaseController.INSTANCE.removeObject(adopter, "user");
            } catch (JsonProcessingException e1) {
                e1.printStackTrace();
            }
        }
    }

    @After
    public void end() {
        try {
            DatabaseController.INSTANCE.removeObject(adopter, "user");
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        
        closeDB();
    }

    @Test
    public void authenticate(){
        
        User user = null;
        
        try {
        	DatabaseFilter model = new DatabaseFilter();
            
            model.add("email", UserRecord.ADOPTER.getEmail());
            model.add("password", UserRecord.ADOPTER.getPassword());
            
            user = (User)DatabaseController.INSTANCE.filter(model, "user", User.class);
        } catch (InvalidParameterException | IOException e) {
            e.printStackTrace();
        }

        assertNotNull(user);
    }

}
