package app.security.services;

import app.database.DatabaseController;
import app.entities.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import org.apache.commons.math3.exception.NoDataException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.mongodb.BasicDBObject;

import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import sun.misc.BASE64Decoder;

import java.security.Key;
import java.util.Collections;

import javax.annotation.PostConstruct;

import java.io.IOException;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class JwtService
{
    private long expireHours;
    private String plainSecret;
    private String encodedSecret;

    public static final String TOKEN_PREFIX = "Bearer ";
	public static final String HEADER_STRING = "Authorization";

    public JwtService() {
        this.expireHours = 24;
        this.plainSecret = "C53DF4B8225D51EAFB2FD09CE545F4EE22894679C32D44FF6656BD71D4A92E79";

        this.encodedSecret = generateEncodedSecret(this.plainSecret);
    }

    @SuppressWarnings("EmptyMethod")
    @PostConstruct
    protected void init() {
    }

    private String generateEncodedSecret(String plainSecret)
    {
        if (StringUtils.isEmpty(plainSecret))
        {
            throw new IllegalArgumentException("JWT secret cannot be null or empty.");
        }
        return Base64
                .getEncoder()
                .encodeToString(this.plainSecret.getBytes());
    }

    private Date getExpirationTime()
    {
        Date now = new Date();
        long expireInMilis = TimeUnit.HOURS.toMillis(expireHours);
        return new Date(expireInMilis + now.getTime());
    }

    private User getUser(String encodedSecret, String token)
    {
		try {
	        Claims claims = Jwts.parser()
	                .setSigningKey(encodedSecret)
	                .parseClaimsJws(token)
	                .getBody();
	
	        User securityUser = null;
        
			String userName = claims.getSubject();
			BasicDBObject filter = new BasicDBObject();
	        filter.append("email", userName);        
	        
			return (User)DatabaseController.INSTANCE.filter(filter, "user", User.class);
			
		} catch (NoDataException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        return null;
    }

    public User getUser(String token)
    {
        return getUser(this.encodedSecret, token);
    }

    private String getToken(String encodedSecret, User user) throws IOException {
        Date now = new Date();
        byte[] encodedKey = new BASE64Decoder().decodeBuffer(encodedSecret);
        Key key = new SecretKeySpec(encodedKey,0, encodedKey.length, "DES");
        return Jwts.builder()
                .setId(UUID.randomUUID().toString())
                .setSubject(user.getEmail())
                .claim("name", user.getFirstName())
                .setIssuedAt(now)
                .setExpiration(getExpirationTime())
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    public String getToken(User user) throws IOException {
        return getToken(this.encodedSecret, user);
    }

    public void addAuthentication(HttpServletResponse response, String username) throws IOException {
        Date now = new Date();
        byte[] encodedKey = new BASE64Decoder().decodeBuffer(encodedSecret);
        Key key = new SecretKeySpec(encodedKey,0, encodedKey.length, "DES");
		String JWT = Jwts.builder()
				.setId(UUID.randomUUID().toString())
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(getExpirationTime())
                .signWith(key, SignatureAlgorithm.HS512)
				.compact();
		
		response.addHeader(HEADER_STRING, TOKEN_PREFIX + " " + JWT);
	}
	
	public Authentication getAuthentication(HttpServletRequest request) {
		String token = request.getHeader(HEADER_STRING);
		
		if (token != null) {
			// faz parse do token
			String user = Jwts.parser()
					.setSigningKey(this.encodedSecret)
					.parseClaimsJws(token.replace(TOKEN_PREFIX, ""))
					.getBody()
					.getSubject();
			
			if (user != null) {
				return new UsernamePasswordAuthenticationToken(user, null, Collections.emptyList());
			}
		}
		return null;
	}
}