package app.entities;

import app.database.DatabaseController;
import com.fasterxml.jackson.core.JsonProcessingException;

public class Announcement {

    private String title;

    public Announcement() throws JsonProcessingException {
        this.title = "First";
        DatabaseController.INSTANCE.addObject(this, "announcements");
    }

    public String getTitle() {
        return title;
    }
}
