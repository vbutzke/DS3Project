package app.security.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.BasicDBObject;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import app.database.DatabaseController;
import app.database.DatabaseFilter;
import app.entities.User;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

@Service
public class UserDetailService  implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user;
        try {
        	BasicDBObject d = new BasicDBObject();
        	
            d.append("email", username);
            user = (User)DatabaseController.INSTANCE.filter(d, "user", User.class);
        
            if(user == null){
                throw new UsernameNotFoundException("No user found. Username tried: " + username);
            }
            
            Set<GrantedAuthority> grantedAuthorities = new HashSet<GrantedAuthority>();
            grantedAuthorities.add(new SimpleGrantedAuthority("admin"));

            return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), grantedAuthorities);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            
            throw new UsernameNotFoundException("No user found. Username tried: " + username);
        }

    }
}