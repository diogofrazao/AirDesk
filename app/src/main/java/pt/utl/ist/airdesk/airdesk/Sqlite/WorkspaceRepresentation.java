package pt.utl.ist.airdesk.airdesk.Sqlite;

/**
 * Created by diogofrazao on 23/03/15.
 */
public class WorkspaceRepresentation {

    private String nameWs;
    private String storage;
    private String path;
    private String owner;
    private long id;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getStorage() {
        return storage;
    }

    public void setStorage(String storage) {
        this.storage = storage;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getOwner() {
        return owner;
    }

    public String getNameWs() {
        return nameWs;
    }

    public void setNameWs(String nameWs) {
        this.nameWs = nameWs;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public String toString() {
        return nameWs;
    }
}
