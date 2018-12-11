package app.entities;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

public class Comment {

    private String userId;
    private String comment;
    private String timestamp;

    public Comment(String userId, String comment) {
        this.userId = userId;
        this.comment = comment;
        this.timestamp = new SimpleDateFormat("dd/MM/yyyy").format(new Timestamp(System.currentTimeMillis()).getTime());
    }

    public String getUserId() {
        return userId;
    }

    public String getComment() {
        return comment;
    }

    public String getTimestamp() {
        return timestamp;
    }
}
