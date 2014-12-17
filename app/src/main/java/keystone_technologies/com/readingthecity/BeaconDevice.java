package keystone_technologies.com.readingthecity;

import java.util.Date;

public class BeaconDevice {

    private int notificationId;
    private String id;
    private int major;
    private int minor;
    private Date fetching;
    private Date age;
    private String parent;

    public int getNotificationId() {
        return notificationId;
    }

    public String getId() {
        return id;
    }

    public int getMajor() {
        return major;
    }

    public int getMinor() {
        return minor;
    }

    public Date getFetching() {
        return fetching;
    }

    public Date getAge() {
        return age;
    }

    public String getParent() {
        return parent;
    }

    public void setNotificationId(int notificationId) {
        this.notificationId = notificationId;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setMajor(int major) {
        this.major = major;
    }

    public void setMinor(int minor) {
        this.minor = minor;
    }

    public void setFetching(Date fetching) {
        this.fetching = fetching;
    }

    public void setAge(Date age) {
        this.age = age;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }
}
