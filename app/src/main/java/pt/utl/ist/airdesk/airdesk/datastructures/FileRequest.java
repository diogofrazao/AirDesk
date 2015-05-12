package pt.utl.ist.airdesk.airdesk.datastructures;

/**
 * Created by duarte on 5/10/15.
 */
public class FileRequest {

    // states if it is a request or response
    String workspace;
    String fileName;


    public FileRequest(String workspace, String fileName) {
        this.workspace = workspace;
        this.fileName = fileName;
    }


    public String getWorkspace() {
        return workspace;
    }

    public void setWorkspace(String workspace) {
        this.workspace = workspace;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
