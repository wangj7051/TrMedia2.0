package js.lib.android_media_scan.scan_controller;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MediaScanControllerBase {

    /**
     * 支持遍历的路径
     */
    static List<String> mListSupportPaths = new ArrayList<>();

    /**
     * 黑名单列表，在此黑名单列表中的路径不应当被扫描。
     */
    static List<String> mListBlacklistPaths = new ArrayList<>();

    public static void setSupportPaths(List<String> listPaths) {
        if (listPaths == null) {
            mListSupportPaths = listPaths;
        } else {
            mListSupportPaths = new ArrayList<>();
        }
    }

    public static void setBlacklistPaths(List<String> listPaths) {
        if (listPaths == null) {
            mListBlacklistPaths = listPaths;
        } else {
            mListBlacklistPaths = new ArrayList<>();
        }
    }

    protected File renameFileWithSpecialName(File file) {
        if (file != null) {
            String fName = file.getName();
            if (fName.contains("'")) {
                fName = fName.replace("'", "`");
                String fPath = file.getParent() + "/" + fName;
                File targetFile = new File(fPath);
                boolean isRenamed = file.renameTo(targetFile);
                if (isRenamed) {
                    return targetFile;
                }
            }
        }
        return file;
    }

    public void destroy() {
        mListSupportPaths.clear();
        mListBlacklistPaths.clear();
    }
}
