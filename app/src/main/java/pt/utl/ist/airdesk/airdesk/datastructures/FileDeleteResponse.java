package pt.utl.ist.airdesk.airdesk.datastructures;

import java.io.Serializable;

/**
 * Created by pedro on 14-05-2015.
 */
public class FileDeleteResponse implements Serializable {

    String status;

    String fileName;

    public FileDeleteResponse(String status,String fileName) {
        this.status = status;
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
