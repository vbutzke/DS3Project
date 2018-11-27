package app.utils;

import java.io.IOException;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MongoDbId {
	  private String $oid;
	
	  public MongoDbId(){ }
	  
	  public MongoDbId(String id){
		  this.$oid = id;
	  }
	 
	  public String get$oid() {
	    return $oid;
	  }
	 
	  public void set$oid(String $oid) {
	    this.$oid = $oid;
	  }
	 
	  @JsonCreator
	  public static String fromJSON(String val) 
	      throws JsonParseException, JsonMappingException, IOException {
	    ObjectMapper mapper = new ObjectMapper();
	    MongoDbId a = mapper.readValue(val, MongoDbId.class);
	    return a.get$oid();
	  }
		
}
