package app.controllers;

import app.database.DatabaseController;
import app.entities.User;
import app.entities.Credentials;
import app.security.services.JwtService;
import com.mongodb.BasicDBObject;
import org.apache.commons.math3.exception.NoDataException;
import java.io.IOException;

public enum AccountController {
	INSTANCE();
	
    AccountController(){

    }

    public User authenticate(Credentials credentials) throws NoDataException, IOException {
    	BasicDBObject model = new BasicDBObject();
        
        model.append("email", credentials.getUsername());
        model.append("password", credentials.getPassword());

        return (User)DatabaseController.INSTANCE.filter(model, "user", User.class);
    }

    public String generateToken(User user) throws IOException {
        JwtService service = new JwtService();
        return service.getToken(user);
    }
}
