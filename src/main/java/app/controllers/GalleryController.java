package app.controllers;

import java.io.IOException;
import java.util.Base64;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import app.database.DatabaseController;
import app.entities.Announcement;
import app.entities.Photo;
import app.entities.User;

@RestController
public class GalleryController {

	@PostMapping("/upload-image") // //new annotation since 4.3
    public Boolean uploadImage(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes, Authentication authentication, String announcementId) throws IOException {

		String email = authentication.getName(); 
		Announcement announcement = (Announcement)DatabaseController.INSTANCE.getRecordBy(announcementId, "announcement", Announcement.class);
		
        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "Please select a file to upload");
            return false;
        }

        try {

            // Get the file and save it somewhere
            byte[] bytes = file.getBytes();
            String base64 = Base64.getEncoder().encodeToString(bytes);
            
            Photo photo = new  Photo();
            photo.setAnnouncementId(announcementId);
            photo.setImage(base64);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return true;
    }
	
	@PostMapping("/upload-video") // //new annotation since 4.3
    public Boolean uploadVideo(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes, Authentication authentication, String announceId) {

        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "Please select a file to upload");
            return false;
        }

        try {

            // Get the file and save it somewhere
            byte[] bytes = file.getBytes();
            String base64 = Base64.getEncoder().encodeToString(bytes);
            
            redirectAttributes.addFlashAttribute("message",
                    "You successfully uploaded '" + file.getOriginalFilename() + "'");

        } catch (IOException e) {
            e.printStackTrace();
        }

        return true;
    }
	
	@PostMapping("/remove") // //new annotation since 4.3
    public Boolean removeFile(String fileId) {

        return true;
    }
}
