package app.entities;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import app.database.DatabaseController;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import app.utils.MongoDbDateDeserializer;
import app.utils.MongoDbId;

public class Announcement {
	
	@JsonSerialize(using=ToStringSerializer.class)
	private MongoDbId _id;
	private String    title;
	private String    description;
    private Address   address;
    private String    race;
    private String    age;
    private String    size;
	private String    user;
	private Photo     photo;
	private Thread    thread;
	private ArrayList<AnnouncementParams> params;
	private final String collection = "announcements";

	@JsonDeserialize(using = MongoDbDateDeserializer.class)
	private Date createdAt;

    public Announcement() {}

    public Announcement(String title, String description, Address address, String race, String age, String size) {
        this.title       = title;
        this.description = description;
        this.address     = address;
        this.race        = race;
        this.age         = age;
        this.size        = size;
    }

	public String get_id() {
		if(_id == null)
			return null;
		
		return _id.get$oid();
	}

	public void set_id(MongoDbId _id) {
		this._id = _id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	public String getRace() {
		return race;
	}

	public void setRace(String race) {
		this.race = race;
	}

	public String getAge() {
		return age;
	}

	public void setAge(String age) {
		this.age = age;
	}

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public Date getCreatedAt() {
		return createdAt;
	}
	
	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public void setAvatar(Photo p) {
		this.photo = p;
	}
	
	public Photo getAvatar() {
		return this.photo;
	}
	
	public ArrayList<AnnouncementParams> getParams() {
		return params;
	}

	public void setParams(ArrayList<AnnouncementParams> params) {
		this.params = params;
	}

	public Thread addComment(Comment comment) throws IOException {
    	thread = getThread();
    	if(thread == null){
    		thread = new Thread(_id, comment);
		} else {
    		thread.getComments().add(comment);
		}
		DatabaseController.INSTANCE.updateObject(thread, thread.getCollection());
		return thread;
	}

	public Thread removeComment(int commentPos) throws IOException {
    	thread = getThread();
    	thread.getComments().remove(commentPos);
		DatabaseController.INSTANCE.updateObject(thread, thread.getCollection());
    	return thread;
	}

	public Photo getPhoto() {
		return photo;
	}

	public Thread getThread() throws IOException {
		return thread = (Thread) DatabaseController.INSTANCE.getRecordBy(get_id(), thread.getCollection(), Thread.class);
	}

	public void save() throws JsonProcessingException {
		DatabaseController.INSTANCE.updateObject(this, collection);
	}

	public String getCollection() {
		return collection;
	}
}
