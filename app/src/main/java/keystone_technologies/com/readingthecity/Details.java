package keystone_technologies.com.readingthecity;

import org.json.JSONObject;

import java.util.Date;

public class Details {

    private String id;
    private Date fetching;
    private Date age;
    private String parent;
    private JSONObject detail;
    private int response;

    public Details() {

    }

    public Details(JSONObject detail) {
        this.detail = detail;
    }

    public void setDetail(JSONObject detail) {
        this.detail = detail;
    }

    public JSONObject getDetail() {
        return detail;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setFetching(Date fetching) {
        this.fetching = fetching;
    }

    public Date getFetching() {
        return fetching;
    }

    public void setResponse(int response) {
        this.response = response;
    }

    public int getResponse() {
        return response;
    }

    public Date getAge() {
        return age;
    }

    public String getParent() {
        return parent;
    }

    public void setAge(Date age) {
        this.age = age;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }
}
