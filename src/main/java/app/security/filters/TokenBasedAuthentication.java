package app.security.filters;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;

import app.entities.User;


class TokenBasedAuthentication extends AbstractAuthenticationToken {

    private String token;
    private UserDetails principle;
    private User currentUser;

    public TokenBasedAuthentication( UserDetails principle, User user ) {
        super( principle.getAuthorities() );
        this.principle = principle;
        this.currentUser = user;
    }

    public String getToken() {
        return token;
    }

    public void setToken( String token ) {
        this.token = token;
    }

    @Override
    public boolean isAuthenticated() {
        return true;
    }

    @Override
    public Object getCredentials() {
        return token;
    }

    @Override
    public UserDetails getPrincipal() {
        return principle;
    }
    
    @Override
    public Object getDetails() {
    	return currentUser;
    }
}