package pt.utl.ist.airdesk.airdesk.datastructures;

import java.io.Serializable;

/**
 * Created by pedro on 13-05-2015.
 */
public class FileLockResponse implements Serializable {

    String lock;
    Boolean state;

    public FileLockResponse(String lock, Boolean state) {
        this.lock = lock;
        this.state = state;
    }

    public String getLock() {
        return lock;
    }

    public void setLock(String lock) {
        this.lock = lock;
    }

    public Boolean getState() {
        return state;
    }

    public void setState(Boolean state) {
        this.state = state;
    }
}

