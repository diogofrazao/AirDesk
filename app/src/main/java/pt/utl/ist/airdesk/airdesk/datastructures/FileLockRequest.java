package pt.utl.ist.airdesk.airdesk.datastructures;

import java.io.Serializable;

/**
 * Created by pedro on 13-05-2015.
 */
public class FileLockRequest implements Serializable {

    String file;
    String login;
    String workpace;

    public FileLockRequest(String login, String file,String workpace) {
        this.login = login;
        this.file = file;
        this.workpace = workpace;
    }

    public String getWorkpace() {
        return workpace;
    }

    public void setWorkpace(String workpace) {
        this.workpace = workpace;
    }



    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }
}
