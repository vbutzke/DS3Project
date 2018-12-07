package app.entities;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import app.utils.MongoDbDateDeserializer;
import app.utils.MongoDbDateSerializer;
import app.utils.MongoDbId;

public class Announcement {
	
	@JsonSerialize(using=ToStringSerializer.class)
	private MongoDbId _id;
	private String  title;
	private String  description;
    private Address address;
    private String  race;
    private String     age;
    private String  size;
	private String user;
	
	private Photo photo;
	
	private ArrayList<AnnouncementParams> params;

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
}
