package keystone_technologies.com.readingthecity;

public class Posts {

    private String id;
    private String name;
    private int response;

    public Posts() {

    }

    public Posts(String id, String name, int response) {
        this.id = id;
        this.name = name;
        this.response = response;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setResponse(int response) {
        this.response = response;
    }

    public int getResponse() {
        return response;
    }
}
