package keystone_technologies.com.readingthecity;

import java.util.Date;

public class BeaconDevice {

    private int response;
    private String major;
    private String minor;
    private String name;
    private String parent;
    private Date date;

    public BeaconDevice(String major, String minor, String name, String parent, Date date) {
        this.major = major;
        this.minor = minor;
        this.name = name;
        this.parent = parent;
        this.date = date;
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
}
