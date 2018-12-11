package singletons;

import app.entities.Address;

import java.io.IOException;

@SuppressWarnings("SameParameterValue")
public enum AnnouncementsRecord {

    BLACK_DOG("Cachorro preto - São Leopoldo", "Pequeno cachorro preto encontrado na saída de um estacionamento", "Brazil", "Rio Grande do Sul", "São Leopoldo", "Vira-lata", "2 meses", "Pequeno porte");

    private String  title;
    private String  description;
    private Address address;
    private String  race;
    private String     age;
    private String  size;

    AnnouncementsRecord(String title, String description, String country, String state, String city, String race, String age, String size){
        this.title       = title;
        this.description = description;
        try {
            this.address     = new Address(country, state, city);
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    public String getAge() {
        return age;
    }

    public String getSize() {
        return size;
    }
}
