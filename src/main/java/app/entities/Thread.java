package app.entities;

import app.database.DatabaseController;
import app.utils.MongoDbId;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import java.util.LinkedList;

public class Thread {

    @JsonSerialize(using = ToStringSerializer.class)
    private MongoDbId _id;

    private MongoDbId announcementId;
    private LinkedList<Comment> comments;
    private final String collection = "threads";

    public Thread(MongoDbId announcementId, Comment comment) throws JsonProcessingException {
        this.announcementId = announcementId;
        this.comments = new LinkedList<>();
        this.comments.add(comment);
        DatabaseController.INSTANCE.addObject(this, collection);
    }

    public String get_id() {
        if(_id == null)
            return null;

        return _id.get$oid();
    }

    public void set_id(MongoDbId _id) {
        this._id = _id;
    }

    public MongoDbId getAnnouncementId() {
        return announcementId;
    }

    public LinkedList<Comment> getComments() {
        return comments;
    }

    public String getCollection() {
        return collection;
    }
}