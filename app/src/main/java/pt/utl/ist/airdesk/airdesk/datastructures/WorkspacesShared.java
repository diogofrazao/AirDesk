package pt.utl.ist.airdesk.airdesk.datastructures;

import java.io.Serializable;
import java.util.List;

/**
 * Created by duarte on 5/9/15.
 */
public class WorkspacesShared implements Serializable{

    List<WorkspaceRepToBeSent> ws;
    String _networkName;

    public String get_login() {
        return _login;
    }

    public void set_login(String _login) {
        this._login = _login;
    }

    String _login;

    public String get_networkName() {
        return _networkName;
    }

    public void set_networkName(String _networkName) {
        this._networkName = _networkName;
    }


    public WorkspaceRepToBeSent getFilesByName(String wsname){

        for(WorkspaceRepToBeSent a : ws){
            if(a.get_name().equals(wsname)){return a;}

        }
        return null;
    }

    public WorkspaceRepToBeSent getOwnerByName(String wsname){

        for(WorkspaceRepToBeSent a : ws){
            if(a.get_sentFrom().equals(wsname)){return a;}

        }
        return null;
    }

    public WorkspacesShared(List<WorkspaceRepToBeSent> ws,String networkName,String login) {
        this.ws = ws;
        _networkName = networkName;
        _login = login;
    }

    public List<WorkspaceRepToBeSent> getWs() {
        return ws;
    }

    public void setWs(List<WorkspaceRepToBeSent> ws) {
        this.ws = ws;
    }
}
