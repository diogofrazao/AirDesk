package pt.utl.ist.airdesk.airdesk;

import java.io.Serializable;

/**
 * Created by duarte on 5/9/15.
 */
public class toBePassed implements Serializable {

    String id;

    public void setId(String id) {
        this.id = id;
    }

    public String getId(){
        return id;
    }
}
