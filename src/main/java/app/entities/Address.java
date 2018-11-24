package app.entities;

import app.database.DatabaseController;
import java.io.IOException;
import java.security.InvalidParameterException;

public class Address {

    private String countryName;
    private String stateName;
    private String cityName;

    public Address() {}
    public Address(String countryName, String stateName, String cityName) throws IOException {

        if(((Country)DatabaseController.INSTANCE.getRecordBy(countryName, "locations",  Country.class)).getStates().get(stateName).getCities().get(cityName) != null){
            this.countryName = countryName;
            this.stateName   = stateName;
            this.cityName    = cityName;
        } else {
            throw new InvalidParameterException("The region informed does not exist in database");
        }

    }

    public String getCountryName() {
        return countryName;
    }

    public String getStateName() {
        return stateName;
    }

    public String getCityName() {
        return cityName;
    }
}
