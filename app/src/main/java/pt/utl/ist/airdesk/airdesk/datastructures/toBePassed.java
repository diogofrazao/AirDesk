package pt.utl.ist.airdesk.airdesk.datastructures;

import java.io.Serializable;

/**
 * Created by duarte on 5/9/15.
 */
public class toBePassed implements Serializable {

    public toBePassed(String id) {
        this.id = id;
    }

    String id;

    public void setId(String id) {
        this.id = id;
    }

    public String getId(){
        return id;
    }
}
