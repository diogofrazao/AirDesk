package pt.utl.ist.airdesk.airdesk;

/**
 * Created by diogofrazao on 23/03/15.
 */
public class User {

    private String email;
    private String permission;


    public User(String email, String permission){
        this.email = email;
        this.permission = permission;

    }


    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }





}
