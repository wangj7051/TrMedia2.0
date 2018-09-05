package js.lib.android.utils;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * WIFI Common Methods
 * <p>
 * 需要权限:
 * <p>
 * &ltuses-permission android:name="android.permission.INTERNET" /&gt
 * </p>
 * <p>
 * &ltuses-permission android:name="android.permission.ACCESS_FINE_LOCATION" /&gt
 * </p>
 * <p>
 * &ltuses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/&gt
 * </p>
 * <p>
 * &ltuses-permission android:name="android.permission.ACCESS_WIFI_STATE" /&gt
 * </p>
 *
 * @author Jun.Wang
 */
public class WifiUtil {

    private Context mContext;
    private WifiManager mWifiManager;

    public WifiUtil(Context context) {
        try {
            mContext = context.getApplicationContext();
            mWifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        } catch (Exception e) {
            mWifiManager = null;
        }
    }

    /**
     * 判断 WIFI 是否是 5G 频段.
     */
    public boolean isWifi5G() {
        int freq = 0;
        WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
        // Android SDK > Android 5.0.1(-21-)
        if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.LOLLIPOP) {
            freq = wifiInfo.getFrequency();
        } else {
            String ssid = wifiInfo.getSSID();
            if (ssid != null && ssid.length() > 2) {
                String ssidTemp = ssid.substring(1, ssid.length() - 1);
                List<ScanResult> scanResults = mWifiManager.getScanResults();
                for (ScanResult scanResult : scanResults) {
                    if (scanResult.SSID.equals(ssidTemp)) {
                        freq = scanResult.frequency;
                        break;
                    }
                }
            }
        }
        return freq > 4900 && freq < 5900;
    }

    public boolean isWifiApEnabled() {
        try {
            Method method = mWifiManager.getClass().getMethod("isWifiApEnabled");
            method.setAccessible(true);
            return (Boolean) method.invoke(mWifiManager);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 设置热点名称及密码，并创建热点
     */
    public void createHotspot(String ssid, String pwd) {
        //Create hotspot configuration
        WifiConfiguration netConfig = new WifiConfiguration();
        netConfig.SSID = ssid;
        netConfig.preSharedKey = pwd;
        netConfig.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
        netConfig.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
        netConfig.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
        netConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
        netConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
        netConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
        netConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
        netConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);

        //通过反射机制打开热点
        createHotspot(netConfig);
    }

    /**
     * Create hotspot
     *
     * @param wifiConfig {@link WifiConfiguration}
     */
    public void createHotspot(WifiConfiguration wifiConfig) {
        try {
            //通过反射机制打开热点
            Method method1 = mWifiManager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class);
            method1.invoke(mWifiManager, wifiConfig, true);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 关闭WiFi热点
     */
    public void closeWifiAp() {
        try {
            Method method = mWifiManager.getClass().getMethod("getWifiApConfiguration");
            method.setAccessible(true);
            WifiConfiguration config = (WifiConfiguration) method.invoke(mWifiManager);
            Method method2 = mWifiManager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class);
            method2.invoke(mWifiManager, config, false);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
