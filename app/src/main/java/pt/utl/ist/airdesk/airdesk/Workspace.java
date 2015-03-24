package pt.utl.ist.airdesk.airdesk;

import java.util.ArrayList;

/**
 * Created by diogofrazao on 22/03/15.
 */
public class Workspace {

    private int quota;
    private String nameWorkspace;
    private String owner;
    ArrayList<User> users;
    private String path;

    public Workspace(int quota, String nameWorkspace, String owner){
        this.quota = quota;
        this.nameWorkspace = nameWorkspace;
        this.owner = owner;

    }

    public void addUser(String name, String permission) throws Exception {
        for (User u : users) {
            if (u.getEmail().equals(name)) {
                throw new Exception();
            }
            users.add(new User(name,permission));
        }
    }
    public int getQuota() {
        return quota;
    }

    public String getNameWorkspace() {
        return nameWorkspace;
    }

    public String getOwner() {
        return owner;
    }

    public void setQuota(int quota) {
        this.quota = quota;
    }

    public void setNameWorkspace(String nameWorkspace) {
        this.nameWorkspace = nameWorkspace;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }



}
