package pt.utl.ist.airdesk.airdesk.datastructures;

import java.io.Serializable;

/**
 * Created by diogofrazao on 12/05/15.
 */
public class FileResponse implements Serializable {

    String _file;

    public FileResponse(String _file) {
        this._file = _file;
    }

    public String getFile() {
        return _file;
    }

    public void setFile(String file) {
        this._file = file;
    }

}
