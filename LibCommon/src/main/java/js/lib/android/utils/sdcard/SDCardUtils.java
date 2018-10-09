package js.lib.android.utils.sdcard;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.os.storage.StorageManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import js.lib.android.utils.EmptyUtil;

/**
 * SDCard Common Methods
 *
 * @author Jun.Wang
 */
public class SDCardUtils {
    public static String SDCARD_INTERNAL = "internal";
    public static String SDCARD_EXTERNAL = "external";
    public static String UDISK_EXTERNAL = "udisk";

    /**
     * Check SDCard Status
     */
    public static boolean isSDCardActive() {
        return Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
    }

    /**
     * Get Inner storage.
     */
    public static File getInnerStorage() {
        return Environment.getExternalStorageDirectory();
    }

    /**
     * Get SDCard Information set
     */
    @SuppressLint("ObsoleteSdkInt")
    public static HashMap<String, SDCardInfo> getSDCardInfos(Context cxt) {
        // SDK >= 14
        if (android.os.Build.VERSION.SDK_INT >= 14) {
            return getSDCardInfo_GreaterOrEqual14(cxt);
        }

        // SDK < 14
        return getSDCardInfo_Below14();
    }

    /**
     * API14以下通过读取Linux的vold.fstab文件来获取SDCard信息
     */
    private static HashMap<String, SDCardInfo> getSDCardInfo_Below14() {
        HashMap<String, SDCardInfo> sdCardInfos = new HashMap<String, SDCardInfo>();
        BufferedReader bufferedReader = null;
        List<String> dev_mountStrs = null;
        try {
            // API14以下通过读取Linux的vold.fstab文件来获取SDCard信息
            bufferedReader = new BufferedReader(new FileReader(Environment.getRootDirectory().getAbsoluteFile() + File.separator
                    + "etc" + File.separator + "vold.fstab"));
            dev_mountStrs = new ArrayList<String>();
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                if (line.startsWith("dev_mount")) {
                    dev_mountStrs.add(line);
                }
            }
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //
        String envAbsolutePath = Environment.getExternalStorageDirectory().getAbsolutePath();
        for (int i = 0; dev_mountStrs != null && i < dev_mountStrs.size(); i++) {
            SDCardInfo sdCardInfo = new SDCardInfo();
            String[] infoStr = dev_mountStrs.get(i).split(" ");
            sdCardInfo.label = infoStr[1];
            sdCardInfo.root = infoStr[2];
            if (sdCardInfo.root.equals(envAbsolutePath)) {
                sdCardInfo.isMounted = (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED));
                sdCardInfos.put(SDCARD_INTERNAL, sdCardInfo);
            } else if (sdCardInfo.root.startsWith("/mnt") && !sdCardInfo.root.equals(envAbsolutePath)) {
                File file = new File(sdCardInfo.root + File.separator + "temp");
                if (file.exists()) {
                    sdCardInfo.isMounted = true;
                } else {
                    if (file.mkdir()) {
                        file.delete();
                        sdCardInfo.isMounted = true;
                    } else {
                        sdCardInfo.isMounted = false;
                    }
                }
                sdCardInfos.put(SDCARD_EXTERNAL, sdCardInfo);
            }
        }
        return sdCardInfos;
    }

    /**
     * Get SDCard Informations that SDK Version >= 14
     */
    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    private static HashMap<String, SDCardInfo> getSDCardInfo_GreaterOrEqual14(Context context) {
        HashMap<String, SDCardInfo> sdCardInfos = new HashMap<String, SDCardInfo>();
        String[] storagePathList = null;
        try {
            StorageManager storageManager = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
            Method getVolumePaths = storageManager.getClass().getMethod("getVolumePaths");
            storagePathList = (String[]) getVolumePaths.invoke(storageManager);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (storagePathList != null) {
            int loop = storagePathList.length;
            for (int idx = 0; idx < loop; idx++) {
                // Inner SDCard
                if (idx == 0) {
                    String mSDCardPath = storagePathList[0];
                    SDCardInfo internalDevInfo = new SDCardInfo();
                    internalDevInfo.root = mSDCardPath;
                    internalDevInfo.isMounted = checkSDCardMount14(context, mSDCardPath);
                    sdCardInfos.put(SDCARD_INTERNAL, internalDevInfo);
                } else {
                    String externalDevPath = storagePathList[idx];
                    SDCardInfo externalDevInfo = new SDCardInfo();
                    externalDevInfo.root = externalDevPath;
                    externalDevInfo.isMounted = checkSDCardMount14(context, externalDevPath);
                    sdCardInfos.put(SDCARD_EXTERNAL + "_" + idx, externalDevInfo);
                }
            }
        }

        //Android 7.0 UDISK
//        List<SDCardInfo> listJsSDInfos = JsVolumeInfo.getSDCardInfos(context);
//        if (!EmptyUtil.isEmpty(listJsSDInfos)) {
//            final int loop = listJsSDInfos.size();
//            for (int idx = 0; idx < loop; idx++) {
//                SDCardInfo sdInfo = listJsSDInfos.get(idx);
//                sdCardInfos.put(UDISK_EXTERNAL + "_" + idx, sdInfo);
//            }
//        }

        List<String> listUdiskPaths = PlayerMp3Utils.getAllExterSdcardPath();
        if (!EmptyUtil.isEmpty(listUdiskPaths)) {
            final int loop = listUdiskPaths.size();
            for (int idx = 0; idx < loop; idx++) {
                SDCardInfo sdInfo = new SDCardInfo();
                sdInfo.root = listUdiskPaths.get(idx);
                sdInfo.isMounted = true;
                sdInfo.label = UDISK_EXTERNAL + "_" + idx;
                sdCardInfos.put(sdInfo.label, sdInfo);
            }
        }

        return sdCardInfos;
    }

    /**
     * 判断SDCard是否挂载上,返回值为true证明挂载上了，否则未挂载
     *
     * @param context    上下文
     * @param mountPoint 挂载点
     */
    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    private static boolean checkSDCardMount14(Context context, String mountPoint) {
        if (mountPoint == null) {
            return false;
        }
        StorageManager storageManager = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
        try {
            Method getVolumeState = storageManager.getClass().getMethod("getVolumeState", String.class);
            String state = (String) getVolumeState.invoke(storageManager, mountPoint);
            return Environment.MEDIA_MOUNTED.equals(state);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
