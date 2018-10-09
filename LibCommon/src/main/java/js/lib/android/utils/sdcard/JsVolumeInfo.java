package js.lib.android.utils.sdcard;

import android.content.Context;
import android.os.StatFs;
import android.os.storage.StorageManager;
import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Refer to {@link android.os.storage.VolumeInfo} from Android6.0
 *
 * @author Jun.Wang
 */
public class JsVolumeInfo {
    //TAG
    private static final String TAG = "JsVolumeInfo";

    /**
     * {@link android.os.storage.VolumeInfo#ACTION_VOLUME_STATE_CHANGED}
     */
    public static final String ACTION_VOLUME_STATE_CHANGED = "android.os.storage.action.VOLUME_STATE_CHANGED";

    /**
     * {@link android.os.storage.VolumeInfo#EXTRA_VOLUME_STATE}
     */
    public static final String EXTRA_VOLUME_STATE = "android.os.storage.extra.VOLUME_STATE";

    /**
     * {@link android.os.storage.VolumeInfo#STATE_UNMOUNTED}
     */
    public static final int STATE_UNMOUNTED = 0;
    /**
     * {@link android.os.storage.VolumeInfo#STATE_CHECKING}
     */
    public static final int STATE_CHECKING = 1;
    /**
     * {@link android.os.storage.VolumeInfo#STATE_MOUNTED}
     */
    public static final int STATE_MOUNTED = 2;
    /**
     * {@link android.os.storage.VolumeInfo#STATE_MOUNTED_READ_ONLY}
     */
    public static final int STATE_MOUNTED_READ_ONLY = 3;
    /**
     * {@link android.os.storage.VolumeInfo#STATE_FORMATTING}
     */
    public static final int STATE_FORMATTING = 4;
    /**
     * {@link android.os.storage.VolumeInfo#STATE_EJECTING}
     */
    public static final int STATE_EJECTING = 5;
    /**
     * {@link android.os.storage.VolumeInfo#STATE_UNMOUNTABLE}
     */
    public static final int STATE_UNMOUNTABLE = 6;
    /**
     * {@link android.os.storage.VolumeInfo#STATE_REMOVED}
     */
    public static final int STATE_REMOVED = 7;
    /**
     * {@link android.os.storage.VolumeInfo#STATE_BAD_REMOVAL}
     */
    public static final int STATE_BAD_REMOVAL = 8;

    // // Use Method
    // private BroadcastReceiver receiver = new BroadcastReceiver() {
    //
    // @Override
    // public void onReceive(Context context, Intent intent) {
    // String action = intent.getAction();
    // if (ACTION_VOLUME_STATE_CHANGED.equals(action)) {
    // int state = intent.getIntExtra(EXTRA_VOLUME_STATE, STATE_UNMOUNTED);
    // // UDisk insert: STATE_UNMOUNTED → STATE_MOUNTED
    // // UDisk remove: STATE_EJECTING → STATE_UNMOUNTED → STATE_BAD_REMOVAL
    // }
    // }
    // };
    //
    // public void register(Context cxt) {
    // try {
    // IntentFilter ifSD = new IntentFilter(ACTION_VOLUME_STATE_CHANGED);
    // cxt.registerReceiver(receiver, ifSD);
    // } catch (Exception e) {
    // e.printStackTrace();
    // }
    // }

    public static List<?> getVolumeInfos(Context context) {
        try {
            StorageManager storageManager = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
            Class clsStoreManager = Class.forName("android.os.storage.StorageManager");
            Method getVolumesMethod = clsStoreManager.getMethod("getVolumes");
            return (List<?>) getVolumesMethod.invoke(clsStoreManager);//获取到了VolumeInfo的列表
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<SDCardInfo> getSDCardInfos(Context context) {
        List<SDCardInfo> infos = new ArrayList<SDCardInfo>();
        try {
            //
            List<?> volumeInfos = getVolumeInfos(context);

            //
            Class volumeInfoClazz = Class.forName("android.os.storage.VolumeInfo");
            Method getTypeMethod = volumeInfoClazz.getMethod("getType");
            Method getFsUuidMethod = volumeInfoClazz.getMethod("getFsUuid");
            Field fsTypeField = volumeInfoClazz.getDeclaredField("fsType");
            Field fsLabelField = volumeInfoClazz.getDeclaredField("fsLabel");
            Field pathField = volumeInfoClazz.getDeclaredField("path");
            Field internalPath = volumeInfoClazz.getDeclaredField("internalPath");

            //
            if (volumeInfos != null) {
                for (Object volumeInfo : volumeInfos) {
                    String uuid = (String) volumeInfoClazz.getMethod("getFsUuid").invoke(volumeInfo);
                    if (uuid != null) {
                        SDCardInfo info = new SDCardInfo();
                        String fsTypeString = (String) fsTypeField.get(volumeInfo);//U盘类型
                        info.label = (String) fsLabelField.get(volumeInfo);//U盘名称
                        info.root = (String) pathField.get(volumeInfo);//U盘路径
                        String internalPathString = (String) internalPath.get(volumeInfo);//U盘路径
                        infos.add(info);
                        Log.i(TAG, fsTypeString + " " + info.label + " " + info.root + " " + internalPathString);
//                        StatFs statFs = new StatFs(internalPathString);
//                        long avaibleSize = statFs.getAvailableBytes();
//                        long totalSize = statFs.getTotalBytes();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return infos;
    }
}
