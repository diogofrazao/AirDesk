package pt.utl.ist.airdesk.airdesk.datastructures;

import java.io.Serializable;

/**
 * Created by pedro on 13-05-2015.
 */
public class FileResponseAlteration implements Serializable{

    String _status;

    public FileResponseAlteration(String _status) {
        this._status = _status;
    }

    public String get_status() {
        return _status;
    }

    public void set_status(String _status) {
        this._status = _status;
    }
}
