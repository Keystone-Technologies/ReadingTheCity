package keystone_technologies.com.readingthecity;

import java.util.Date;

public class Device {

    private int major;
    private int minor;
    private Date date;
    private String id;

    public Device() {
    }

    public Device(int major, int minor, Date date, String id) {
        this.major = major;
        this.minor = minor;
        this.date = date;
        this.id = id;
    }

    public void setMajor(int major) {
        this.major = major;
    }

    public int getMajor() {
        return major;
    }

    public void setMinor(int minor) {
        this.minor = minor;
    }

    public int getMinor() {
        return minor;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Date getDate() {
        return date;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
