package pt.utl.ist.airdesk.airdesk.datastructures;

import java.util.List;

/**
 * Created by duarte on 5/9/15.
 */
public class WorkspacesShared {

    List<WorkspaceRepToBeSent> ws;

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    String from;

    public WorkspacesShared(List<WorkspaceRepToBeSent> ws) {
        this.ws = ws;
    }

    public List<WorkspaceRepToBeSent> getWs() {
        return ws;
    }

    public void setWs(List<WorkspaceRepToBeSent> ws) {
        this.ws = ws;
    }
}
