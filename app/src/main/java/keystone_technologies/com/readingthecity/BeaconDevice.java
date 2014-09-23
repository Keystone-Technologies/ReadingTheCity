package keystone_technologies.com.readingthecity;

public class BeaconDevice {

    private String UUID;
    private int response;
    private String major;
    private String minor;
    private String name;
    private String parent;

    public String getUUID() {
        return UUID;
    }

    public void setUUID(String UUID) {
        this.UUID = UUID;
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
}
