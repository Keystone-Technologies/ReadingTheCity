package keystone_technologies.com.readingthecity;

import java.io.Serializable;
import java.util.Date;

public class BeaconDevice implements Serializable {

    private int response;
    private String major;
    private String minor;
    private String name;
    private String parent;
    private Date date;
    private String url;
    private String description;

    public BeaconDevice(String major, String minor, Date date) {
        this.major = major;
        this.minor = minor;
        this.date = date;
    }

    public BeaconDevice(String major, String minor, String name, String parent, Date date, String url, String description, int response) {
        this.major = major;
        this.minor = minor;
        this.name = name;
        this.parent = parent;
        this.date = date;
        this.url = url;
        this.description = description;
        this.response = response;
    }

    public int getResponse() {
        return response;
    }

    public void setResponse(int response) {
        this.response = response;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public String getMajor() {
        return major;
    }

    public void setMinor(String minor) {
        this.minor = minor;
    }

    public String getMinor() {
        return minor;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public String getParent() {
        return parent;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Date getDate() {
        return date;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
