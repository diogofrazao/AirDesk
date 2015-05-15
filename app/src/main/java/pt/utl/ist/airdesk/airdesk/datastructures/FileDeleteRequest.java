package pt.utl.ist.airdesk.airdesk.datastructures;

import java.io.File;
import java.io.Serializable;

/**
 * Created by pedro on 14-05-2015.
 */
public class FileDeleteRequest implements Serializable {

    String login;
    File file;
    String workspace;

    public String getWorkspace() {
        return workspace;
    }

    public void setWorkspace(String workspace) {
        this.workspace = workspace;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public FileDeleteRequest(File file,String login, String workspace) {
        this.file = file;
        this.login = login;
        this.workspace = workspace;
    }
}
