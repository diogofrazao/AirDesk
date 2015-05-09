package pt.utl.ist.airdesk.airdesk.Sqlite;

/**
 * Created by duarte on 5/9/15.
 */

public class WSUsersPermission {

    String workspaceName;
    String user;
    String rights;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    long id;

    public String getRights() {
        return rights;
    }

    public void setRights(String rights) {
        this.rights = rights;
    }

    public String getWorkspaceName() {
        return workspaceName;
    }

    public void setWorkspaceName(String workspaceName) {
        this.workspaceName = workspaceName;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

}
