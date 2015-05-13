package pt.utl.ist.airdesk.airdesk.datastructures;

import java.io.Serializable;

/**
 * Created by pedro on 13-05-2015.
 */
public class FileRequestAlteration implements Serializable {
    String _workspaceName;
    String _fileName;
    String _text;

    public FileRequestAlteration(String _text, String _workspaceName, String _fileName) {
        this._text = _text;
        this._workspaceName = _workspaceName;
        this._fileName = _fileName;
    }


    public String get_workspaceName() {
        return _workspaceName;
    }

    public void set_workspaceName(String _workspaceName) {
        this._workspaceName = _workspaceName;
    }

    public String get_fileName() {
        return _fileName;
    }

    public void set_fileName(String _fileName) {
        this._fileName = _fileName;
    }

    public String get_text() {
        return _text;
    }

    public void set_text(String _text) {
        this._text = _text;
    }
}
