package app.entities;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import app.utils.MongoDbId;

public class Photo {
	
	@JsonSerialize(using=ToStringSerializer.class)
	private MongoDbId _id;

	private String announcementId;
	private String image;
		
	public String get_id() {
		if(_id == null)
			return null;
		
		return _id.get$oid();
	}

	public void set_id(MongoDbId _id) {
		this._id = _id;
	}
		
	public String getAnnouncementId() {
		return announcementId;
	}
	public void setAnnouncementId(String announcementId) {
		this.announcementId = announcementId;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
}
