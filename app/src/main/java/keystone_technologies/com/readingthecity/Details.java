package keystone_technologies.com.readingthecity;

import java.util.Date;

public class Details {

    private int response;
    private String id;
    private Date date;
    private String detail;

    public Details() {

    }

    public Details(String detail, Date date, String id) {
        this.detail = detail;
        this.date = date;
        this.id = id;
    }

    public int getResponse() {
        return response;
    }

    public void setResponse(int response) {
        this.response = response;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getDetail() {
        return detail;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Date getDate() {
        return date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
