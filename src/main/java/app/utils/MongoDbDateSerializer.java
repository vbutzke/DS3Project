package app.utils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

class MongoDbDateSerializer extends JsonSerializer<Date> {
    private static final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    
    @Override
    public void serialize(Date date, JsonGenerator jgen, SerializerProvider provider) throws IOException {
    	String dateValue = formatter.format(date);
    	
        String text = "{ \"$date\" : \""+   dateValue   +"\"}";
        
        jgen.writeRawValue(text);
    }
}