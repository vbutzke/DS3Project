package app.entities;

public class Announcement {

    private String  title;
    private String  description;
    private Address address;
    private String  race;
    private int     age;
    private String  size;

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
