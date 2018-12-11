package app.controllers.models;

import app.entities.Comment;
import app.utils.MongoDbId;

import java.util.LinkedList;

public class ThreadModel {

    public MongoDbId announcementId;
    public LinkedList<Comment> comments;

}
