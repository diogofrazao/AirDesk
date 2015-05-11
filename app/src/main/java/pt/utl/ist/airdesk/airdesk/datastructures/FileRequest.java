package pt.utl.ist.airdesk.airdesk.datastructures;

/**
 * Created by duarte on 5/10/15.
 */
public class FileRequest {

    // states if it is a request or response
    boolean request;
    byte[] file;

    public boolean isRequest() {
        return request;
    }

    public void setRequest(boolean request) {
        this.request = request;
    }

    public byte[] getFile() {
        return file;
    }

    public void setFile(byte[] file) {
        this.file = file;
    }

}
