package app.singletons;

public enum EmailCredentials {
    INSTANCE();

    private String email;
    private String password;

    private EmailCredentials(){
        this.email    = "emaildummytestersender@gmail.com";
        this.password = "Obsec40dev";
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}