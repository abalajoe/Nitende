package app.facebook.android.com.nitende.datasource;

/**
 * Created by abala on 11/2/16.
 */
public class User {
    private int id;
    private String firstname;
    private String email;
    private String password;

    public User(int id, String firstname, String email) {
        this.id = id;
        this.firstname = firstname;
        this.email = email;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
