package app.controllers;

import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.Base64;
import java.util.LinkedList;
import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.mail.MessagingException;
import javax.naming.AuthenticationException;
import javax.servlet.http.HttpServletResponse;
import app.controllers.models.AdoptionRequestModel;
import app.entities.*;
import app.singletons.EmailType;
import app.utils.EmailService;
import app.controllers.models.CommentModel;
import app.entities.Thread;
import org.apache.commons.math3.exception.NoDataException;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.trace.http.HttpTrace.Principal;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import com.fasterxml.jackson.core.JsonProcessingException;
import app.controllers.models.AnnouncementModel;
import app.controllers.models.RegisterModel;
import app.database.*;
import app.exceptions.DuplicateEntityException;
import app.utils.FileStorageService;
import app.utils.MongoDbId;

@RestController
public class AppController {

	private User u;
	
	@PostConstruct
    public void postConstruct() {
        DatabaseController.INSTANCE.startDB();
    }
	
	@RequestMapping("/register")
	public User registerUser(HttpServletResponse response, @RequestBody RegisterModel model) {
		try {
			u = new User(model.email, model.firstName, model.lastName, model.password, model.passwordConf);
		} catch(InvalidParameterException | JsonProcessingException | DuplicateEntityException e) {
			sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
		}

		response.setStatus(HttpServletResponse.SC_OK);
		return u;
	}

	@RequestMapping(method = RequestMethod.POST, value = "/create-announcement")
	public Announcement createAnnouncement(HttpServletResponse response, Authentication authentication, @RequestBody AnnouncementModel model) {
		try {
			User user = (User)authentication.getDetails();
			Announcement a = FeedController.INSTANCE.createAnnouncement(user, model.title, model.description, model.address, model.race, model.age, model.size, model.params);
			response.setStatus(HttpServletResponse.SC_OK);
			return a;
		} catch (IOException e) {
			sendError(response, HttpServletResponse.SC_PRECONDITION_FAILED, e.getMessage());
		}

		return null;
	}
	
	@RequestMapping(method = RequestMethod.POST, value = "/update-announcement/{announcementId}")
	public Announcement updateAnnouncement(HttpServletResponse response, Authentication authentication, @RequestBody AnnouncementModel model, @PathVariable String announcementId) {
		try {
			User user = (User)authentication.getDetails();
			Announcement a = FeedController.INSTANCE.getAnnouncementById(announcementId);
			
			if(!a.getUser().equals(user.get_id())) {
	        	throw new BadCredentialsException("Not authorized."); 
	        }
			
			FeedController.INSTANCE.updateAnnouncement(a, model.title, model.description, model.address, model.race, model.age, model.size, model.params);
			a.save();
			response.setStatus(HttpServletResponse.SC_OK);
			return a;
		} catch (NoDataException | IOException e) {
			sendError(response, HttpServletResponse.SC_PRECONDITION_FAILED, e.getMessage());
		}

		return null;
	}

	@RequestMapping("/feed")
	private LinkedList<Announcement> feed(HttpServletResponse response, Authentication authentication){
		User user = (User)authentication.getDetails();
		LinkedList<Announcement> announcementsList = new LinkedList<>();
		try{
			announcementsList = FeedController.INSTANCE.getAllAnnouncements();
		} catch (IOException e) {
			sendError(response, HttpServletResponse.SC_PRECONDITION_FAILED, e.getMessage());
		}
		return announcementsList;
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/get-announcement/{announcementId}")
	private Announcement getAnnouncement(HttpServletResponse response, Authentication authentication, @PathVariable String announcementId) {
		Announcement announcement = null; 
		try{
			User user = (User) authentication.getDetails();
			
			announcement = FeedController.INSTANCE.getAnnouncementById(announcementId);
			announcement = FeedController.INSTANCE.getOnePic(announcement);
			announcement = FeedController.INSTANCE.getFavorite(user, announcement);
    } catch (IOException e) {
			sendError(response, HttpServletResponse.SC_PRECONDITION_FAILED, e.getMessage());
		}
		return announcement;
	}

	@RequestMapping(method = RequestMethod.GET, value = "/get-announcement/{announcementId}/getThread")
	private Thread getThread(HttpServletResponse response, Authentication authentication, @PathVariable String announcementId) {
		try{
			return FeedController.INSTANCE.getAnnouncementById(announcementId).getThreadFromDB();
		} catch (IOException e) {
			sendError(response, HttpServletResponse.SC_PRECONDITION_FAILED, e.getMessage());
		}
		return null;
	}

	@RequestMapping(method = RequestMethod.POST, value = "/get-announcement/{announcementId}/deleteComment")
	private Thread addComment(HttpServletResponse response, Authentication authentication, @PathVariable String announcementId, int commentPos) {
		Thread thread = null;
		try{
			Announcement a = FeedController.INSTANCE.getAnnouncementById(announcementId);
			thread = a.removeComment(commentPos);
			a.save();

		} catch (IOException e) {
			sendError(response, HttpServletResponse.SC_PRECONDITION_FAILED, e.getMessage());
		}
		return thread;
	}

	@PostMapping("/get-announcement/{announcementId}/addComment")
	private Thread deleteComment(HttpServletResponse response, Authentication authentication, @RequestBody CommentModel model, @PathVariable String announcementId) {
		Thread thread = null;
		try{
			Announcement a = FeedController.INSTANCE.getAnnouncementById(announcementId);
			thread = a.addComment(new Comment(model.userId, model.comment));
			a.save();
		} catch (IOException e) {
			sendError(response, HttpServletResponse.SC_NOT_MODIFIED, e.getMessage());
		}
		return thread;
	}

	@RequestMapping("/my-announcements")
	private LinkedList<Announcement> myAnnouncements(HttpServletResponse response, Authentication authentication){
		User  user = (User)authentication.getDetails();
		LinkedList<Announcement> announcementsList = new LinkedList<>();
		try{
			announcementsList = FeedController.INSTANCE.getMyAnnouncements(user);
		} catch (IOException e) {
			sendError(response, HttpServletResponse.SC_PRECONDITION_FAILED, e.getMessage());
		}
		return announcementsList;
	}

	@RequestMapping("/logout")
	private void logout(HttpServletResponse response) {
		DatabaseController.INSTANCE.closeDB();
	}
	
	@RequestMapping(method = RequestMethod.POST, value = "/authenticate")
    public User authenticate(HttpServletResponse response, @RequestBody Credentials credentials) throws NoDataException, IOException {
		User  user = AccountController.INSTANCE.authenticate(credentials);
		try {
			String token = AccountController.INSTANCE.generateToken(user);

			if (response != null)
				response.addHeader("Set-Authorization", token);
		} catch(IOException e){
			e.printStackTrace();
		}
        return user;
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/get-photos/{announcementId}")
	public LinkedList<Photo> getPhotos(HttpServletResponse response, Authentication authentication, @PathVariable String announcementId) {
		LinkedList<Photo> photos = null;
		
		try {	        
			photos = GalleryController.INSTANCE.getPhotos(announcementId);
		} catch (IOException e) {
			e.printStackTrace();
			sendError(response, HttpServletResponse.SC_PRECONDITION_FAILED, e.getMessage());
		}
		
		return photos;
	}
	
//	@PostMapping("/upload-image") // //new annotation since 4.3
//    public Photo uploadImage(HttpServletResponse response, @RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes, Authentication authentication, String announcementId) {
//		
//		Photo photo = null;
//		
//		try {
//			User user = (User)authentication.getDetails();
//			Announcement announcement = FeedController.INSTANCE.getAnnouncementById(announcementId);
//        
//	        if (file.isEmpty()) {
//	            throw new Exception("Please select a file to upload");
//	        }
//	        
//	        if(!announcement.getUser().equals(user.get_id())) {
//	        	throw new BadCredentialsException("Not authorized."); 
//	        }
//	        
//	        return GalleryController.INSTANCE.uploadImage(file, user, announcement);
//		}
//		catch(Exception e) {
//			e.printStackTrace();
//			sendError(response, HttpServletResponse.SC_PRECONDITION_FAILED, e.getMessage());
//		}
//		
//		return photo;
//	}
	
	@PostMapping("/remove-image")
    public Boolean removeFile(HttpServletResponse response, RedirectAttributes redirectAttributes, Authentication authentication, String fileId) {
		try {
			User user = (User)authentication.getDetails();

			GalleryController.INSTANCE.removeFile(user, fileId);
		} catch (NoDataException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			sendError(response, HttpServletResponse.SC_PRECONDITION_FAILED, e.getMessage());
		}
		
    return true;
	}
	
	@Autowired
  private FileStorageService fileStorageService;
    
  @PostMapping("/upload-image")
  public Photo uploadFile(HttpServletResponse response, @RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes, Authentication authentication, String announcementId) throws Exception {
        Photo photo =  new Photo();
		
		try {
			User user = (User)authentication.getDetails();
			Announcement announcement = FeedController.INSTANCE.getAnnouncementById(announcementId);
        
	        if (file.isEmpty()) {
	            throw new Exception("Please select a file to upload");
	        }
	        
	        if(!announcement.getUser().equals(user.get_id())) {
	        	throw new BadCredentialsException("Not authorized."); 
	        }
      
	        photo.setAnnouncementId(announcement.get_id());
	        //photo.setUri(fileDownloadUri);
	        photo.setContentType(file.getContentType());
	        
	        photo.set_id((MongoDbId)DatabaseController.INSTANCE.addObject(photo, "photos"));
	        
	        String fileName = fileStorageService.storeFile(file, photo.get_id());
	        String fileDownloadUri = ServletUriComponentsBuilder.fromPath("/photo/")
	                .path(photo.get_id())
	                .toUriString();
	        
	        photo.setUri(fileDownloadUri);
	        
	        DatabaseController.INSTANCE.updateObject(photo, "photos");
	        Photo p = GalleryController.INSTANCE.uploadImage(file, user, announcement);
	        announcement.save();
	        return p;
    }
		catch(Exception e) {
			e.printStackTrace();
			sendError(response, HttpServletResponse.SC_PRECONDITION_FAILED, e.getMessage());
		}
        return photo;
    }

    @GetMapping("/photo/{fileName:.+}")
    public ResponseEntity<Resource> downloadFile(HttpServletResponse response, @PathVariable String fileName, HttpServletRequest request) throws Exception {
        // Load file as Resource
        Resource resource = fileStorageService.loadFileAsResource(fileName);

        // Try to determine file's content type
        String contentType = null;
        Photo photo = null;
        try {
        	photo = GalleryController.INSTANCE.getPhoto(fileName);
            contentType = photo.getContentType();
        } catch (IOException e) {
        	e.printStackTrace();
			sendError(response, HttpServletResponse.SC_PRECONDITION_FAILED, e.getMessage());
        }

        // Fallback to the default content type if type could not be determined
        if(contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
		
	@SuppressWarnings("SameReturnValue")
	@PostMapping("/remove-image")
    public Boolean removeFile(HttpServletResponse response, RedirectAttributes redirectAttributes, Authentication authentication, String fileId) {
		try {
			User user = (User)authentication.getDetails();
			GalleryController.INSTANCE.removeFile(user, fileId);
		} catch (NoDataException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			sendError(response, HttpServletResponse.SC_PRECONDITION_FAILED, e.getMessage());
		}
		
		return true;
	}

	@RequestMapping(method = RequestMethod.POST, value = "/get-announcement/{announcementId}/addAsFavorite")
	public void addAsFavorite(HttpServletResponse response, Authentication authentication, @PathVariable String announcementId){
		try{
			User user = (User)authentication.getDetails();
			FeedController.INSTANCE.addAsFavorite(announcementId, user);
			response.setStatus(HttpServletResponse.SC_OK);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
		}
	}

	@RequestMapping(method = RequestMethod.POST, value = "/get-announcement/{announcementId}/removeFromFavorites")
	public void removeFromFavorites(HttpServletResponse response, Authentication authentication, @PathVariable String announcementId){
		try{
			User user = (User)authentication.getDetails();
			FeedController.INSTANCE.removeFromFavorites(announcementId, user);
			response.setStatus(HttpServletResponse.SC_OK);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
		}
	}

	@RequestMapping(method = RequestMethod.GET, value = "/get-announcement/getFavorites")
	public LinkedList<Announcement> getFavorites(HttpServletResponse response, Authentication authentication){
		LinkedList<Announcement> favorites = new LinkedList<>();
		try{
			User user = (User)authentication.getDetails();
			favorites = FeedController.INSTANCE.getFavorites(user);
			response.setStatus(HttpServletResponse.SC_OK);
		} catch (IOException e) {
			e.printStackTrace();
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
		return favorites;
	}

	@RequestMapping(method = RequestMethod.POST, value = "/get-announcement/adopt/{announcementId}")
	public void adoptDog(HttpServletResponse response, Authentication authentication, @PathVariable String announcementId, @RequestBody AdoptionRequestModel model){
		Announcement announcement = null;
		try{
			announcement = FeedController.INSTANCE.getAnnouncementById(announcementId);
			User guardian = (User)DatabaseController.INSTANCE.filter(new BasicDBObject().append("_id", new ObjectId(announcement.getUser())), "user", User.class);
			User adopter = (User)authentication.getDetails();
			FeedController.INSTANCE.requestAdoption(announcement, adopter.get_id());
			EmailService.INSTANCE.send(guardian.getEmail(), "[SOLICITAÇÃO DE ADOÇÃO] - "+announcement.getTitle(), EmailService.INSTANCE.buildBody(model, adopter, EmailType.ADOPTION_REQ));
			response.setStatus(HttpServletResponse.SC_OK);
		} catch (IOException | MessagingException e) {
			sendError(response, HttpServletResponse.SC_PRECONDITION_FAILED, e.getMessage());
		}

	}

	@RequestMapping(method = RequestMethod.POST, value = "/forgot-password") //TODO: conferir se é realmente um POST
	public void forgotPassword(HttpServletResponse response, String email){

		BasicDBObject filter = new BasicDBObject();
		filter.append("email", new ObjectId(email));
		try {
			User user = (User)DatabaseController.INSTANCE.filter(filter, "user", User.class);
			if(user != null){
				EmailService.INSTANCE.send(email, "Reset password", "To reset your password, click on the following link: "); //TODO: adicionar link de password reset aqui
			}
		} catch (IOException | MessagingException e) {
			e.printStackTrace();
		}
		response.setStatus(HttpServletResponse.SC_OK);
	}

	@RequestMapping(method = RequestMethod.POST, value = "/reset-password")
	public boolean resetPassword(HttpServletResponse response, String email, String password, String passwordConf){
		boolean isReset = false;
		BasicDBObject filter = new BasicDBObject();
		filter.append("email", new ObjectId(email));
		try {
			User user = (User)DatabaseController.INSTANCE.filter(filter, "user", User.class);
			if(user != null){
				isReset = user.setPassword(password, passwordConf);
				response.setStatus(HttpServletResponse.SC_OK);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		response.setStatus(HttpServletResponse.SC_PRECONDITION_FAILED);
		return isReset;
	}

  @RequestMapping(method = RequestMethod.POST, value ="/get-announcement/approve/{announcementId}")
	public void approveAdoption(HttpServletResponse response, Authentication authentication, @PathVariable String announcementId, @RequestBody AdoptionRequestModel model){
		Announcement announcement = null;
		BasicDBObject filter = new BasicDBObject();
		try{
			announcement = FeedController.INSTANCE.getAnnouncementById(announcementId);
			filter.append("_id", new ObjectId(announcement.getAdopter()));
			User adopter = (User) DatabaseController.INSTANCE.filter(filter, "user", User.class);
			User user = (User)authentication.getDetails();
			if(FeedController.INSTANCE.approveAdoption(user, adopter, announcement)){
				EmailService.INSTANCE.send(adopter.getEmail(), "[SOLICITAÇÃO DE ADOÇÃO] - "+announcement.getTitle(), EmailService.INSTANCE.buildBody(model, adopter, EmailType.APPROVE_ADOPTION));
			}
			response.setStatus(HttpServletResponse.SC_OK);
		} catch (IOException e) {
			sendError(response, HttpServletResponse.SC_PRECONDITION_FAILED, e.getMessage());
		} catch (AuthenticationException | MessagingException e) {
			e.printStackTrace();
			sendError(response, HttpServletResponse.SC_PRECONDITION_FAILED, e.getMessage());
		}
	}

	@RequestMapping(method = RequestMethod.POST, value ="/get-announcement/decline/{announcementId}")
	public void declineAdoption(HttpServletResponse response, Authentication authentication, @PathVariable String announcementId, @RequestBody AdoptionRequestModel model){
		Announcement announcement = null;
		BasicDBObject filter = new BasicDBObject();
		try{
			announcement = FeedController.INSTANCE.getAnnouncementById(announcementId);
			filter.append("_id", new ObjectId(announcement.getAdopter()));
			User adopter = (User) DatabaseController.INSTANCE.filter(filter, "user", User.class);
			User user = (User)authentication.getDetails();
			if(FeedController.INSTANCE.declineAdoption(user, announcement)){
				EmailService.INSTANCE.send(adopter.getEmail(), "[SOLICITAÇÃO DE ADOÇÃO] - "+announcement.getTitle(), EmailService.INSTANCE.buildBody(model, adopter, EmailType.DECLINE_ADOPTION));
			}
			response.setStatus(HttpServletResponse.SC_OK);
		} catch (IOException e) {
			sendError(response, HttpServletResponse.SC_PRECONDITION_FAILED, e.getMessage());
		} catch (AuthenticationException | MessagingException e) {
			e.printStackTrace();
			sendError(response, HttpServletResponse.SC_PRECONDITION_FAILED, e.getMessage());
		}
	}

	private void sendError(HttpServletResponse response, int sc, String message) {
		response.setStatus(sc);
		try {
			response.sendError(sc, message);		
		} catch(IOException i) {
			i.printStackTrace();
		}

	}
	
}
