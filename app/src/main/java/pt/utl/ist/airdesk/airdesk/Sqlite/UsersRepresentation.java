package pt.utl.ist.airdesk.airdesk.Sqlite;

/**
 * Created by diogofrazao on 01/04/15.
 */
public class UsersRepresentation {

    private String name;
    private long id;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    @Override
    public String toString() {
        return name;
    }

}
