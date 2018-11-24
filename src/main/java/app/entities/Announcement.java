package app.entities;

import java.util.HashMap;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import app.utils.MongoDbId;

public class Announcement {
	
	@JsonSerialize(using=ToStringSerializer.class)
	private MongoDbId _id;
		
	public String get_id() {
		if(_id == null)
			return null;
		
		return _id.get$oid();
	}

	public void set_id(MongoDbId _id) {
		this._id = _id;
	}
	
	private String  title;
    private String  description;
    private Address address;
    private String  race;
    private int     age;
    private String  size;

	private String user;

    public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	//private HashMap<String, Photo> photos;
    public Announcement() {}
	public Announcement(String title, String description, Address address, String race, int age, String size) {
        this.title       = title;
        this.description = description;
        this.address     = address;
        this.race        = race;
        this.age         = age;
        this.size        = size;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Address getAddress() {
        return address;
    }

    public String getRace() {
        return race;
    }

    public int getAge() {
        return age;
    }

    public String getSize() {
        return size;
    }
    

}
