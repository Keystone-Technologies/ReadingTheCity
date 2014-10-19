package keystone_technologies.com.readingthecity;

import java.io.Serializable;
import java.util.Date;

public class BeaconDevice {

    private int response;
    private int major;
    private int minor;
    private String name;
    private String parent;
    private String id;
    private Date date;
    private String url;
    private String description;
    private boolean hasParent;

    public BeaconDevice() {
    }

    public BeaconDevice(String name, String id) {
        this.id = id;
        this.name = name;
    }

    public BeaconDevice(int major, int minor, String name, String parent, String id,
                        String url, String description) {
        this.major = major;
        this.minor = minor;
        this.name = name;
        this.parent = parent;
        this.url = url;
        this.description = description;
        this.id = id;
    }

    public boolean hasParent() {
        if (getParent() != null) {
            return true;
        }
        return false;
    }

    public int getResponse() {
        return response;
    }

    public void setResponse(int response) {
        this.response = response;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }
}
