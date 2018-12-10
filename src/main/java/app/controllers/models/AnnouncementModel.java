package app.controllers.models;

import java.util.ArrayList;
import java.util.Map;

import app.entities.Address;
import app.entities.AnnouncementParams;
import app.entities.User;
import app.singletons.AnnouncementStatus;

public class AnnouncementModel extends Model{
	public String  _id;
	public String  title;
	public String  description;
	public Address address;
	public String  race;
	public String     age;
	public String  size;
	public User user;
	public ArrayList<AnnouncementParams> params; 
	public AnnouncementStatus status;

    public AnnouncementModel() {}
}
