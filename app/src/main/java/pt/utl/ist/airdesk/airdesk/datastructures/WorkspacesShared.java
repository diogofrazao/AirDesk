package pt.utl.ist.airdesk.airdesk.datastructures;

import java.io.Serializable;
import java.util.List;

/**
 * Created by duarte on 5/9/15.
 */
public class WorkspacesShared implements Serializable{

    List<WorkspaceRepToBeSent> ws;
    String from;

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public WorkspaceRepToBeSent getFilesByName(String wsname){

        for(WorkspaceRepToBeSent a : ws){
            if(a.get_name().equals(wsname)){return a;}

        }
        return null;
    }

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
