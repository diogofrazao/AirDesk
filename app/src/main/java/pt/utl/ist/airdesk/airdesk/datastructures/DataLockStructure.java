package pt.utl.ist.airdesk.airdesk.datastructures;

/**
 * Created by pedro on 13-05-2015.
 */
public class DataLockStructure {

    String fileName;
    String userLogin;

    public DataLockStructure(String userLogin, String fileName) {
        this.userLogin = userLogin;
        this.fileName = fileName;
    }

    public String getUserLogin() {
        return userLogin;
    }

    public void setUserLogin(String userLogin) {
        this.userLogin = userLogin;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
