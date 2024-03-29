package keystone_technologies.com.readingthecity;

import com.estimote.sdk.Region;

public class Constants {
    public static final Integer REQUEST_ENABLE_BT = 1;
    public static final String EXTRAS_BEACON = "extrasBeacon";
    public static final String TAG = MainActivity.class.getSimpleName();
    public static final Region ALL_ESTIMOTE_BEACONS_REGION = new Region("rid", null, null, null);
    public static final int YES = 1;
    public static final int NO = 0;
    public static final String BEACON_QUERY = "http://couchdb.dev.kit.cm/rtc/_design/lookup/_view/beacon?key=";
    public static final String BEACON_DETAILS = "http://couchdb.dev.kit.cm/rtc/_design/lookup/_view/details?key=";
    public static final int MILLIS_PER_DAY = 86400000;
    public static final int SERVICE_NOTIFICATION_ID = 112;
    public static final int PARENT_NOTIFICATION_ID = 311;
}
