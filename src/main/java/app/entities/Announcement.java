package app.entities;

import app.database.DatabaseController;
import com.fasterxml.jackson.core.JsonProcessingException;

public class Announcement {

    private String title;

    public Announcement(String title) throws JsonProcessingException {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}
