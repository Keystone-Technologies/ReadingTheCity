package keystone_technologies.com.readingthecity;

import java.util.Date;

public class Device {

    private int major;
    private int minor;
    private Date date;

    public Device() {

    }

    public Device(int major, int minor, Date date) {
        this.major = major;
        this.minor = minor;
        this.date = date;
    }

    public int getMajor() {
        return major;
    }

    public void setMajor(int major) {
        this.major = major;
    }

    public int getMinor() {
        return minor;
    }

    public void setMinor(int minor) {
        this.minor = minor;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Date getDate() {
        return date;
    }
}
