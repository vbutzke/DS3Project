package app.entities;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import app.singletons.AnnouncementStatus;
import app.database.DatabaseController;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import app.utils.MongoDbDateDeserializer;
import app.utils.MongoDbId;
import org.springframework.data.annotation.Transient;

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
	private ArrayList<AnnouncementParams> params;
	private AnnouncementStatus status;
	private String adopter;
	private boolean adopted;
	private String    threadId;
	private final String collection = "announcements";

  @Transient
	private boolean favorite;
  
	@JsonDeserialize(using = MongoDbDateDeserializer.class)
	private Date createdAt;

    public Announcement() {}

    public Announcement(String title, String description, Address address, String race, String age, String size) throws IOException {
        this.title       = title;
        this.description = description;
        this.address     = address;
        this.race        = race;
        this.age         = age;
        this.size        = size;
        this.status 	  = AnnouncementStatus.AVAILABLE;
        this.adopter     = "";
        this.adopted 	  = false;
        Thread t = new Thread(get_id());
		    this.threadId    = DatabaseController.INSTANCE.getRecord(t, t.getCollection(), Thread.class).get_id();
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

	public boolean getFavorite() {
		return favorite;
	}

	public void setFavorite(boolean favorite) {
		this.favorite = favorite;
	}
	
  public String getAdopter() {
		return adopter;
  }
  
  public boolean isAdopted() {
		return adopted;
	}

	public void setAdopter(String adopter) {
		this.adopter = adopter;
	}

	public void requestAdoption(String adopterId){
    	if(this.status == AnnouncementStatus.AVAILABLE) {
			this.status = AnnouncementStatus.PENDING_APPROVAL;
			setAdopter(adopterId);
		} else {
    		throw new UnsupportedOperationException("There is an adoption pending for this announcement.");
		}
	}

	public void approveAdoption(String adopterId){
    	if(this.status == AnnouncementStatus.PENDING_APPROVAL) {
			this.status = AnnouncementStatus.ADOPTED;
			setAdopter(adopterId);
		} else {
			throw new UnsupportedOperationException("There is an adoption pending for this announcement.");
		}
	}

	public void declineAdoption(){
		if(this.status == AnnouncementStatus.PENDING_APPROVAL) {
			this.status = AnnouncementStatus.AVAILABLE;
			setAdopter("");
		} else {
			throw new UnsupportedOperationException("There is an adoption pending for this announcement.");
		}
	}

  public Thread addComment(Comment comment) throws IOException {
    	Thread thread = getThreadFromDB();
    	if(thread == null){
    		thread = new Thread(get_id(), comment);
		} else {
    		thread.getComments().add(comment);
		}
		DatabaseController.INSTANCE.updateObject(thread, thread.getCollection());
		return thread;
	}

	public Thread removeComment(int commentPos) throws IOException {
    	Thread thread = getThreadFromDB();
    	thread.getComments().remove(commentPos);
		DatabaseController.INSTANCE.updateObject(thread, thread.getCollection());
    	return thread;
	}

	public Photo getPhoto() {
		return photo;
	}

	public void save() throws JsonProcessingException {
		DatabaseController.INSTANCE.updateObject(this, collection);
	}

	public String getCollection() {
		return collection;
	}

	public String getThreadId() {
		return threadId;
	}

	@JsonIgnore
	public Thread getThreadFromDB() throws IOException {

		return (Thread) DatabaseController.INSTANCE.getRecordBy(threadId, "threads", Thread.class);
	}
}
