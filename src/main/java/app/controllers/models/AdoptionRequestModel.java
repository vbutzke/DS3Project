package app.controllers.models;

import java.util.Date;

public class AdoptionRequestModel extends Model{

    public String userId;
    public String phone;
    public String city;
    public Date   dateForContact;
    public String shiftForContact;
    public AnnouncementModel announcement;

}
