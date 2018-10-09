package js.lib.android.utils;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import org.apache.http.util.EncodingUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;


/**
 * File Operate methods
 *
 * @author Jun.Wang
 */
public class JsFileUtils {
    // TAG
    private static final String TAG = "JsFileUtils";

    /**
     * Context
     */
    protected static Context mContext;

    /**
     * APP 文件存储根路径
     */
    protected static String mRootPath = "";

    /**
     * 创建 APP 文件存储根路径
     */
    public static void init(Context cxt) {
        mContext = cxt;
        try {
            // 当前存在外部存储器 且 可读可写
            if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
                mRootPath = mContext.getExternalFilesDir(null).getParentFile().getAbsolutePath();
                // ../Data/..
            } else {
                mRootPath = Environment.getDataDirectory().getPath() + "/data/" + cxt.getPackageName();
                createFolder(mRootPath);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // LOG
        Log.i(TAG, "init(Context cxt)-> [mRootPath:" + mRootPath + "]");
    }

    /**
     * 获取文件存储根路径
     */
    public static String getRootPath() {
        return mRootPath;
    }

    /**
     * New create Folder
     *
     * @return 0 :创建成功
     * <p>
     * -1 非文件夹
     * <p>
     * -2 文件夹已存在
     */
    public static int createFolder(String folderPath) {
        return createFolder(new File(folderPath));
    }

    /**
     * New create Folder
     *
     * @return 0 :创建成功
     * <p>
     * -1 文件夹已存在
     * <p>
     * -2 非文件夹
     * <p>
     * -3 创建失败
     */
    public static int createFolder(File f) {
        if (f.exists()) {
            return -1;
        }
        if (f.isFile()) {
            return -2;
        }
        return f.mkdirs() ? 0 : -3;
    }

    /**
     * 文件的文件夹路径
     * <p>
     * if("/storage/usbotg/music/mm.mp3"),will return "/storage/usbotg/music/"
     */
    public static String getParentPath(String path) {
        String parentPath = "";
        try {
            if (!TextUtils.isEmpty(path)) {
                File f = new File(path);
                parentPath = f.getParent();
                if (parentPath != null && !parentPath.endsWith("/")) {
                    parentPath += "/";
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return parentPath;
    }

    /**
     * 返回路径
     *
     * @param parentPath : 父路径
     * @param pathName   : 子文件或子文件夹名称
     * @return "/a/b.jpg" 或 "/a/b"
     */
    public static String getPath(String parentPath, String pathName) {
        try {
            if (TextUtils.isEmpty(parentPath)) {
                return pathName;
            } else if (parentPath.endsWith("/")) {
                return parentPath + pathName;
            } else {
                return parentPath + "/" + pathName;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * Delete File & its children
     */
    public static void deleteFiles(File file) {
        try {
            // File
            if (file.isFile()) {
                file.delete();
                // Folder
            } else if (file.isDirectory()) {
                File[] childFiles = file.listFiles();
                if (childFiles != null) {
                    for (File childF : childFiles) {
                        deleteFiles(childF);
                    }
                    file.delete();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param path    文件目录
     * @param oldname 原来的文件名
     * @param newname 新文件名
     * @return 0 重命名成功
     * <p>
     * -1 重命名文件不存在
     * <p>
     * -2 新文件名和原文件名相同
     * <p>
     * -3 已经存在同名文件
     * <p>
     * -4 重命名信息输入非法
     */
    public static int renameFile(String path, String oldname, String newname) {
        // 重命名信息输入非法
        if (EmptyUtil.isEmpty(oldname) || EmptyUtil.isEmpty(newname)) {
            return -4;
        }
        // 新文件名和原文件名相同
        if (oldname.equals(newname)) {
            return -2;
        }
        // 重命名文件不存在
        File oldfile = new File(path + "/" + oldname);
        if (!oldfile.exists()) {
            return -1;
        }
        // 已经存在同名文件
        File newfile = new File(path + "/" + newname);
        if (newfile.exists()) {
            return -3;
        }
        // Rename
        oldfile.renameTo(newfile);
        return 0;
    }

    /**
     * 获取拷贝文件或文件夹名称
     * <p>
     * <li>拷贝文件,如"/root/f.txt", 如果在路径"/root/"下已经存在f.txt,则应当返回"f(1).txt"</li>
     * <li>拷贝文件夹,如"/root/folder", 如果在路径"/root/"下已经存在folder,则应当返回"folder(1)"</li>
     * </p>
     * 依次类推
     *
     * @param targetPath : 目标文件夹路径
     * @param fileName   ：要执行拷贝的文件或文件夹名称
     * @return : 文件夹或文件名称,如 "/root/f(1).txt","/root/f(2).txt","/root/f(3).txt" ...<br/>
     * 或"/root/folder(1)","/root/folder(2)","/root/folder(3)" ...
     */
    public static File getTargetFile(String targetPath, String fileName) {
        //
        File targetFile = new File(targetPath + fileName);
        if (targetFile.exists()) {
            String startName = "";
            String endName = "";
            if (targetFile.isFile()) {
                int idxSeperate = fileName.lastIndexOf(".");
                if (idxSeperate == -1) {
                    startName = fileName;
                    endName = "";
                } else {
                    startName = fileName.substring(0, idxSeperate);
                    endName = fileName.substring(idxSeperate);
                }
            } else {
                startName = fileName;
                endName = "";
            }

            //
            int loopNum = 1;
            boolean isLoop = true;
            while (isLoop) {
                for (int idx = loopNum; idx <= loopNum * 100; idx++) {
                    fileName = startName + "(" + idx + ")" + endName;
                    targetFile = new File(targetPath + "/" + fileName);
                    if (!targetFile.exists()) {
                        isLoop = false;
                        break;
                    }
                }
            }
        }
        return targetFile;
    }

    /**
     * 使用文件通道的方式复制文件,执行效率比较高
     *
     * @param srcFile    源文件
     * @param targetFile 复制到的新文件
     */

    public static void copyFileByChannel(File srcFile, File targetFile) {
        // Input
        FileInputStream fis = null;
        FileChannel fcIn = null;
        // Output
        FileOutputStream fos = null;
        FileChannel fcOut = null;

        // Copy
        try {
            // Input
            fis = new FileInputStream(srcFile);
            fcIn = fis.getChannel();// 得到对应的文件通道
            // Output
            fos = new FileOutputStream(targetFile);
            fcOut = fos.getChannel();// 得到对应的文件通道
            // 连接两个通道，并且从"fcIn"通道读取，然后写入"fcOut"通道
            fcIn.transferTo(0, fcIn.size(), fcOut);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            CloseUtil.close(fis);
            CloseUtil.close(fcIn);
            CloseUtil.close(fos);
            CloseUtil.close(fcOut);
        }
    }

    /**
     * 拷贝文件 , 普通的缓冲输入输出流，执行效率比较低
     *
     * @param srcFile    源文件
     * @param targetFile 复制到的新文件
     */
    public static void copyFileByStream(File srcFile, File targetFile) {
        InputStream is = null;
        OutputStream os = null;
        try {
            is = new BufferedInputStream(new FileInputStream(srcFile));
            os = new BufferedOutputStream(new FileOutputStream(targetFile));

            byte[] buf = new byte[1024 * 2];
            int i;
            while ((i = is.read(buf)) != -1) {
                os.write(buf, 0, i);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            CloseUtil.close(is);
            CloseUtil.close(os);
        }
    }

    /**
     * 拷贝文件夹
     *
     * @param srcPath    : 源文件夹路径
     * @param targetPath : 目标路径
     */
    public static void copyFolder(String srcPath, String targetPath) {
        try {
            //
            createFolder(targetPath);

            //
            File srcFolder = new File(srcPath);
            String[] strFNames = srcFolder.list();

            //
            File temp = null;
            for (int idx = 0; idx < strFNames.length; idx++) {
                temp = new File(srcPath + strFNames[idx]);
                // Copy child files
                if (temp.isFile()) {
                    copyFileByChannel(temp, new File(targetPath + strFNames[idx]));
                    // Loop copy child folders
                } else if (temp.isDirectory()) {
                    copyFolder(srcPath + strFNames[idx], targetPath + strFNames[idx]);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 移动文件夹
     *
     * @param srcPath    : 源文件夹路径
     * @param targetPath : 目标路径
     */
    public static void moveFolder(String srcPath, String targetPath) {
        copyFolder(srcPath, targetPath);
        deleteFiles(new File(srcPath));
    }

    /**
     * Write String To File
     */
    public static void writeStrToFile(String strContent, String targetFilePath) {
        FileWriter fw = null;
        BufferedWriter bw = null;
        try {
            File file = new File(targetFilePath);
            if (!file.exists()) {
                fw = new FileWriter(file);
                bw = new BufferedWriter(fw);
                bw.write(strContent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            CloseUtil.close(bw);
            CloseUtil.close(fw);
        }
    }

    /**
     * Read file to String
     */
    public static String read(String filePath, String charset) {
        StringBuffer txtContent = new StringBuffer("");
        // Read File To String
        File f = new File(filePath);
        if (f.exists()) {
            // 每次读取的byte数
            byte[] b = new byte[8 * 1024];
            InputStream is = null;
            try {
                // 文件输入流
                is = new FileInputStream(f);
                while (is.read(b) != -1) {
                    // 字符串拼接
                    txtContent.append(EncodingUtils.getString(b, charset));
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                CloseUtil.close(is);
            }
        }
        return txtContent.toString();
    }

    /**
     * Check File Exist
     */
    public static boolean isFileExist(String path) {
        try {
            return (new File(path)).exists();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Check Path Equals
     *
     * @param path1 : String path1
     * @param path2 : String path2
     * @return boolean
     */
    public static boolean isPathEqual(String path1, String path2) {
        try {
            return (new File(path1).getPath()).equals(new File(path2).getPath());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Get File Name
     *
     * @param f            : target file
     * @param isWithSuffix : 是否携带后缀, 如:"file.txt"
     * @return String
     */
    public static String getFileName(File f, boolean isWithSuffix) {
        try {
            String fileName = f.getName();
            return isWithSuffix ? fileName : fileName.substring(0, fileName.lastIndexOf("."));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}
