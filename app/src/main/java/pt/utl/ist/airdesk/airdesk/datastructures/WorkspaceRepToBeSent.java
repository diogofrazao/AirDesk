package pt.utl.ist.airdesk.airdesk.datastructures;

/**
 * Created by duarte on 5/9/15.
 */
public class WorkspaceRepToBeSent {

    String _name;

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

    List<String> _files;

    public WorkspaceRepToBeSent(string name, List<String> files){
        _name= name;
        _files=files;
    }



}
