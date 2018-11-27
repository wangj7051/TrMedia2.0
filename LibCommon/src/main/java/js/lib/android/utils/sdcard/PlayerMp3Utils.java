package js.lib.android.utils.sdcard;

import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * add read U disk music
 *
 * @author yangbofeng
 */
public class PlayerMp3Utils {

    public static String getUdiskPath() {
        String upath = "";
        try {
            Runtime runtime = Runtime.getRuntime();
            // 运行mount命令，获取命令的输出，得到系统中挂载的所有目录
            Process proc = runtime.exec("mount");
            InputStream is = proc.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            String line;
            BufferedReader br = new BufferedReader(isr);
            while ((line = br.readLine()) != null) {
                Log.d("", line);
                // 将常见的linux分区过滤掉
                // SdList.add(line);
                if (line.contains("secure"))
                    continue;
                if (line.contains("asec"))
                    continue;
                // 下面这些分区是我们需要的
                if (line.contains("vfat") || line.contains("fuse") || line.contains("fat") || (line.contains("ntfs"))) {
                    // 将mount命令获取的列表分割，items[0]为设备名，items[1]为挂载路径
                    String items[] = line.split(" ");
                    if (items != null && items.length > 1) {
                        String path = items[2].toLowerCase(Locale.getDefault());
                        // 添加一些判断，确保是sd卡，如果是otg等挂载方式，可以具体分析并添加判断条件
                        if (path != null && path.contains("media_rw")) {
                            upath = path;
                        }

                    }
                }
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return upath;
    }

    /**
     * 获取sd卡和U盘路径
     *
     * @return
     */
    public static List<String> getAllExterSdcardPath(boolean isFilter) {
        List<String> SdList = new ArrayList<>();
        try {
            Runtime runtime = Runtime.getRuntime();
            // 运行mount命令，获取命令的输出，得到系统中挂载的所有目录
            Process proc = runtime.exec("mount");
            InputStream is = proc.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            String line;
            BufferedReader br = new BufferedReader(isr);
            while ((line = br.readLine()) != null) {
                Log.d("", line);
                // 将常见的linux分区过滤掉
                // SdList.add(line);
                if (line.contains("secure"))
                    continue;
                if (line.contains("asec"))
                    continue;
                // 下面这些分区是我们需要的
                if (line.contains("vfat") || line.contains("fuse") || line.contains("fat") || (line.contains("ntfs"))) {
                    // 将mount命令获取的列表分割，items[0]为设备名，items[1]为挂载路径
                    String items[] = line.split(" ");
                    if (items.length > 1) {
                        String path = items[2].toLowerCase(Locale.getDefault());
                        // 添加一些判断，确保是sd卡，如果是otg等挂载方式，可以具体分析并添加判断条件
                        if (isFilter) {
                            if (!SdList.contains(path) && path.contains("media_rw")) {
                                SdList.add(items[2]);
                            }
                        } else if (!SdList.contains(path) && path.contains("media_rw")) {
                            SdList.add(items[2]);
                        }

                    }
                }
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return SdList;
    }

    /**
     * 根据路径获取.mp3
     *
     * @param paths
     */
    public static void getmp3(String paths) {
        List list = new ArrayList<Object>();
        StringBuilder sbb = new StringBuilder();
        File files = new File(paths);
        if (files.isDirectory()) {
            for (File file : files.listFiles()) {
                String path = file.getAbsolutePath();
                if (path.endsWith(".mp3")) {
                    Log.e("TAG", path);
                    list.add(path);
                    sbb.append(path);
                    sbb.append("\n");
                    Log.e("TAG", sbb.toString());
                }
            }
        }
    }
}
