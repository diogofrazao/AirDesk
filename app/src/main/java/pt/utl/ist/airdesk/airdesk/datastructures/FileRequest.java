package pt.utl.ist.airdesk.airdesk.datastructures;

/**
 * Created by duarte on 5/10/15.
 */
public class FileRequest {

    // states if it is a request or response
    boolean request;
    String workspace;
    String fileName;

    String typeRequest;

    public boolean isRequest() {
        return request;
    }

    public void setRequest(boolean request) {
        this.request = request;
    }


}
