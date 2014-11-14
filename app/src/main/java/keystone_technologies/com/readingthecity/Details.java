package keystone_technologies.com.readingthecity;

public class Details {

    private String detail;
    private String id;

    public Details() {

    }

    public Details(String detail) {
        this.detail = detail;
    }

    public Details(String id, String detail) {
        this.id = id;
        this.detail = detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getDetail() {
        return detail;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
