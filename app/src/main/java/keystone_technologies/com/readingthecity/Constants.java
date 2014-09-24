package keystone_technologies.com.readingthecity;

import com.estimote.sdk.Region;

public class Constants {
    public static final Integer REQUEST_ENABLE_BT = 1;
    public static final int NOTIFICATION_ID = 123;
    public static final String EXTRAS_BEACON = "extrasBeacon";
    public static final String TAG = MainActivity.class.getSimpleName();
    public static final Region ALL_ESTIMOTE_BEACONS_REGION = new Region("rid", null, null, null);
    public static final int YES = 1;
    public static final int NO = 0;
    public static final String BEACON_QUERY = "http://couchdb.dev.kit.cm/rtc/_design/lookup/_view/beacon";
    public static final String BEACON_DETAILS = "http://couchdb.dev.kit.cm/rtc/_design/lookup/_view/details";
}
