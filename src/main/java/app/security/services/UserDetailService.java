package app.security.services;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import app.database.DatabaseController;
import app.entities.Adopter;
import app.entities.User;
import app.entities.UserModel;
import app.security.JwtUser;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

@Service
public class UserDetailService  implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user;
        try {
            Document d = new Document();
            d.append("email", username);
            d = DatabaseController.INSTANCE.getDM().getRecord(d, "user");
        
            if(d == null){
                throw new UsernameNotFoundException("No user found. Username tried: " + username);
            }
            
            Set<GrantedAuthority> grantedAuthorities = new HashSet<GrantedAuthority>();
            grantedAuthorities.add(new SimpleGrantedAuthority("admin"));

            return new org.springframework.security.core.userdetails.User(d.getString("email"), d.getString("password"), grantedAuthorities);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            
            throw new UsernameNotFoundException("No user found. Username tried: " + username);
        }

    }
}