package pt.utl.ist.airdesk.airdesk.datastructures;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by duarte on 5/9/15.
 */
public class WorkspaceRepToBeSent implements Serializable {

    String _name;

    List<String> _files;

    String _sentFrom;

    public String get_sentFrom() {
        return _sentFrom;
    }

    public void set_sentFrom(String _sentFrom) {
        this._sentFrom = _sentFrom;
    }



    public List<String> get_files() {
        return _files;
    }

    public void set_files(List<String> _files) {
        this._files = _files;
    }

    public String get_name() {
        return _name;
    }



    public void set_name(String _name) {
        this._name = _name;
    }

    public WorkspaceRepToBeSent(String name, String from){
        _name= name;
        _sentFrom = from;
        _files= new ArrayList<String>();
    }
}
