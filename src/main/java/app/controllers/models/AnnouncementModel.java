package app.controllers.models;

import app.entities.Address;
import app.entities.User;

public class AnnouncementModel {
	public String  _id;
	public String  title;
	public String  description;
	public Address address;
	public String  race;
	public int     age;
	public String  size;
	public User user;

    public AnnouncementModel() {}
}
