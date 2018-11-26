package app.security.filters;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.filter.OncePerRequestFilter;

import app.entities.User;
import app.security.services.JwtService;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class TokenAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    JwtService jwtService;

    @Autowired
    UserDetailsService userDetailServiceImpl;

    private String getToken( HttpServletRequest request ) {

        String authHeader = request.getHeader(JwtService.HEADER_STRING);
        
        if ( authHeader != null && authHeader.startsWith(JwtService.TOKEN_PREFIX)) {
            return authHeader.substring(7);
        }

        return null;
    }

    private boolean isValid(String token) {
    	token = token == null ? "" : token.toLowerCase().trim();
    	return !(token.isEmpty() || token.equals("undefined") || token.equals("bearer undefined") || token.equals("bearer"));
    }

    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {

        String error = "";
        String authToken = getToken( request );

        if (isValid(authToken)) {

            // Get username from token
            User user = jwtService.getUser( authToken );
            if ( user != null ) {

                // Get user
                UserDetails userDetails = userDetailServiceImpl.loadUserByUsername( user.getEmail() );

                // Create authentication
                TokenBasedAuthentication authentication = new TokenBasedAuthentication( userDetails, user );
                authentication.setToken( authToken );
                SecurityContextHolder.getContext().setAuthentication( authentication );
            } else {
                error = "Username from token can't be found in DB.";
            }
        } else {
            error = "Authentication failed - no Bearer token provided.";
        }
        if( ! error.equals("")){
            System.out.println(error);
            SecurityContextHolder.getContext().setAuthentication( null );//prevent show login form...
        }

        chain.doFilter(request, response);
    }

}