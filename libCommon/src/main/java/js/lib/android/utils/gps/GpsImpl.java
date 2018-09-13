package js.lib.android.utils.gps;

import android.content.Context;
import android.location.Criteria;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.util.Log;

import java.util.Iterator;

public class GpsImpl {
    //TAG
    private static final String TAG = "GpsImpl";

    private Context mContext;
    private LocationManager mLocationManager;

    private GpsStatus.Listener mGpsStausListener;

    /**
     * {@link GpsImplListener} object
     */
    private GpsImplListener mGpsImplListener;

    public interface GpsImplListener {
        void onGotSpeed(double speed_mPerS, double speed_kmPerH);
    }

    public GpsImpl(Context context) {
        mContext = context;
        mLocationManager = GpsUtil.getLocationManager(context);
        init();
    }

    public void setGpsImplListener(GpsImplListener l) {
        mGpsImplListener = l;
    }

    private void init() {
        if (mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            String bestProvider = mLocationManager.getBestProvider(getLocationCriteria(), true);
            // 获取位置信息
            // 如果不设置查询要求，getLastKnownLocation方法传人的参数为LocationManager.GPS_PROVIDER
            Location location = mLocationManager.getLastKnownLocation(bestProvider);
            // 监听状态
            mLocationManager.addGpsStatusListener((mGpsStausListener = new GpsStatusOnChange()));
            // 绑定监听，有4个参数
            // 参数1，设备：有GPS_PROVIDER和NETWORK_PROVIDER两种
            // 参数2，位置信息更新周期，单位毫秒
            // 参数3，位置变化最小距离：当位置距离变化超过此值时，将更新位置信息
            // 参数4，监听
            // 备注：参数2和3，如果参数3不为0，则以参数3为准；参数3为0，则通过时间来定时更新；两者为0，则随时刷新

            // 1秒更新一次，或最小位移变化超过1米更新一次；
            // 注意：此处更新准确度非常低，推荐在service里面启动一个Thread，在run中sleep(10000);然后执行handler.sendMessage(),更新位置
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, new LocationOnChange());
        } else {
            GpsUtil.openGPSSet(mContext);
        }
    }

    /**
     * 返回查询条件
     *
     * @return @{@link Criteria}
     */
    private Criteria getLocationCriteria() {
        Criteria criteria = new Criteria();
        // 设置定位精确度 Criteria.ACCURACY_COARSE比较粗略，Criteria.ACCURACY_FINE则比较精细
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setSpeedRequired(true); // 设置是否要求速度
        criteria.setCostAllowed(false); // 设置是否允许运营商收费
        criteria.setBearingRequired(false); // 设置是否需要方位信息
        criteria.setAltitudeRequired(false); // 设置是否需要海拔信息
        criteria.setPowerRequirement(Criteria.POWER_LOW); // 设置对电源的需求
        return criteria;
    }

    private class GpsStatusOnChange implements GpsStatus.Listener {
        @Override
        public void onGpsStatusChanged(int event) {
            Log.i(TAG, "event: " + event);
            switch (event) {
                case GpsStatus.GPS_EVENT_FIRST_FIX: // 第一次定位
                    Log.i(TAG, "GPS_EVENT_FIRST_FIX");
                    break;

                case GpsStatus.GPS_EVENT_SATELLITE_STATUS: // 卫星状态改变
                    GpsStatus gpsStatus = mLocationManager.getGpsStatus(null); // 获取当前状态
                    int maxSatellites = gpsStatus.getMaxSatellites(); // 获取卫星颗数的默认最大值
                    Iterator<GpsSatellite> iters = gpsStatus.getSatellites().iterator(); // 创建一个迭代器保存所有卫星
                    int count = 0;
                    while (iters.hasNext() && count <= maxSatellites) {
                        GpsSatellite s = iters.next();
                        count++;
                    }
                    Log.i(TAG, "Satellite Number:" + count);
                    break;

                case GpsStatus.GPS_EVENT_STARTED: // 定位启动
                    Log.i(TAG, "GPS_EVENT_STARTED");
                    break;

                case GpsStatus.GPS_EVENT_STOPPED: // 定位结束
                    Log.i(TAG, "GPS_EVENT_STOPPED");
                    break;
            }
        }
    }

    // 位置监听
    private class LocationOnChange implements LocationListener {
        /**
         * 位置信息变化时触发
         */
        @Override
        public void onLocationChanged(Location location) {
            Log.i(TAG, "onLocationChanged(Location)");
            // location.getAltitude(); -- 海拔
            updateSpeedByLocation(location);
        }

        /**
         * GPS状态变化时触发
         */
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.i(TAG, "onStatusChanged(provider," + status + ",Bundle)");
            switch (status) {
                case LocationProvider.AVAILABLE: // GPS状态为可见时
                    Log.i(TAG, "当前GPS状态为可见状态");
                    break;

                case LocationProvider.OUT_OF_SERVICE: // GPS状态为服务区外时
                    Log.i(TAG, "当前GPS状态为服务区外状态");
                    break;

                case LocationProvider.TEMPORARILY_UNAVAILABLE: // GPS状态为暂停服务时
                    Log.i(TAG, "当前GPS状态为暂停服务状态");
                    break;
            }
        }

        /**
         * GPS开启时触发
         */
        public void onProviderEnabled(String provider) {
            Location location = mLocationManager.getLastKnownLocation(provider);
            updateSpeedByLocation(location);
        }

        /**
         * GPS禁用时触发
         */
        public void onProviderDisabled(String provider) {
            // updateView(null);
        }

        private void updateSpeedByLocation(Location location) {
            double speedMPerS = location.getSpeed();
            double speedKmPerH = speedMPerS * 3.6;
            if (mGpsImplListener != null) {
                mGpsImplListener.onGotSpeed(speedMPerS, speedKmPerH);
            }
        }
    }

    public void destroy() {
        if (mLocationManager != null) {
            mLocationManager.removeGpsStatusListener(mGpsStausListener);
            mGpsImplListener = null;
        }
    }
}
