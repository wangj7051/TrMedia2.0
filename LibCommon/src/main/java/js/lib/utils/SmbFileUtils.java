package js.lib.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileInputStream;
import jcifs.smb.SmbFileOutputStream;
import js.lib.android.utils.EmptyUtil;
import js.lib.android.utils.JsFileUtils;
import js.lib.android.utils.Logs;

/**
 * SmbFile operate methods
 * 
 * @author Jun.wang
 */
public class SmbFileUtils extends JsFileUtils {
	// TAG
	private static final String TAG = "FileUtils -> ";

	/**
	 * New create Folder
	 * 
	 * @return 0 :Success
	 *         <p>
	 *         -1 What to create is not a folder.
	 *         <p>
	 *         -2 exist folder with same folder name.
	 * 
	 */
	public static int createSmbFolder(String folderPath) {
		int resNew = 0;
		try {
			resNew = createSmbFolder(new SmbFile(folderPath));
		} catch (Exception e) {
			Logs.printStackTrace(TAG + "createSmbFolder()", e);
		}

		return resNew;
	}

	/**
	 * New create Folder
	 * 
	 * @return 0 :Success
	 *         <p>
	 *         -1 What to create is not a folder.
	 *         <p>
	 *         -2 exist folder with same folder name.
	 * 
	 */
	public static int createSmbFolder(SmbFile smbF) {
		try {
			if (smbF.isFile()) {
				return -1;
			}

			if (smbF.exists()) {
				return -2;
			}

			createSmbPath(smbF);

		} catch (Exception e) {
			Logs.printStackTrace(TAG + "createSmbFolder()", e);
		}

		return 0;
	}

	/**
	 * Create SMB folder path
	 */
	protected static void createSmbPath(SmbFile smbF) {
		try {
			if (smbF == null) {
				return;
			}
			if (!smbF.exists()) {
				smbF.mkdirs();
			}
		} catch (Exception e) {
			Logs.printStackTrace(TAG + "createSmbPath()", e);
		}
	}

	/**
	 * 获取目标文件
	 */
	public static SmbFile getTargetSmbFile(String targetPath, String fileName) {
		//
		SmbFile targetFile = null;

		try {
			//
			targetFile = new SmbFile(targetPath + File.separator + fileName);
			if (targetPath.endsWith(File.separator)) {
				targetFile = new SmbFile(targetPath + fileName);
			}

			if (targetFile.exists()) {
				String startName = "";
				String endName = "";

				//
				if (targetFile.isFile()) {
					int idxSeperate = fileName.lastIndexOf(".");
					if (idxSeperate == -1) {
						startName = fileName;
						endName = "";
					} else {
						startName = fileName.substring(0, idxSeperate);
						endName = fileName.substring(idxSeperate);
					}

					//
				} else {
					if (fileName.endsWith(File.separator)) {
						startName = fileName.substring(0, fileName.length() - 1);
						endName = File.separator;
					} else {
						startName = fileName;
						endName = "";
					}
				}

				//
				int loopNum = 1;
				boolean isLoop = true;
				while (isLoop) {
					for (int idx = loopNum; idx <= loopNum * 100; idx++) {
						fileName = startName + "(" + idx + ")" + endName;
						targetFile = new SmbFile(targetPath + "/" + fileName);
						if (!targetFile.exists()) {
							isLoop = false;
							break;
						}
					}
				}
			}
		} catch (Exception e) {
			Logs.printStackTrace(TAG + "getTargetSmbFile()", e);
		}

		return targetFile;
	}

	/**
	 * Delete File
	 * <p>
	 * if file is folder,delete it and its children
	 */
	public static void deleteSmbFiles(SmbFile file) {
		try {
			//
			if (file == null || !file.exists()) {
				return;
			}

			// Delete File
			if (file.isFile()) {
				file.delete();
				return;
			}

			// Delete folder
			if (file.isDirectory()) {
				SmbFile[] childFile = file.listFiles();
				if (childFile == null || childFile.length == 0) {
					file.delete();
					return;
				}
				for (SmbFile f : childFile) {
					deleteSmbFiles(f);
				}
				file.delete();
			}
		} catch (Exception e) {
			Logs.printStackTrace(TAG + "deleteSmbFiles()", e);
		}
	}

	/**
	 * @param path
	 *            文件目录
	 * @param oldname
	 *            原来的文件名
	 * @param newname
	 *            新文件名
	 * 
	 * @return 0 重命名成功
	 *         <p>
	 *         -1 重命名文件不存在
	 *         <p>
	 *         -2 新文件名和原文件名相同
	 *         <p>
	 *         -3 已经存在同名文件
	 *         <p>
	 *         -4 重命名信息输入非法
	 */
	public static int renameSmbFile(String path, String oldname, String newname) {
		//
		if (EmptyUtil.isEmpty(oldname) || EmptyUtil.isEmpty(newname)) {
			return -4;
		}

		//
		if (oldname.equals(newname)) {
			return -2;
		}

		//
		try {
			SmbFile oldfile = new SmbFile(path + File.separator + oldname);
			if (path.endsWith(File.separator)) {
				oldfile = new SmbFile(path + oldname);
			}

			if (!oldfile.exists()) {
				return -1;
			}

			//
			SmbFile newfile = new SmbFile(path + File.separator + newname);
			if (path.endsWith(File.separator)) {
				newfile = new SmbFile(path + newname);
			}

			if (oldname.endsWith(File.separator)) {
				if (!newname.endsWith(File.separator)) {
					newfile = new SmbFile(path + newname + File.separator);
				}
			}

			if (newfile.exists()) {
				return -3;
			}

			//
			oldfile.renameTo(newfile);
		} catch (Exception e) {
			Logs.printStackTrace(TAG + "renameSmbFile()", e);
		}

		return 0;
	}

	/**
	 * Copy file from SMB to SMB
	 * 
	 * @param srcPath
	 *            : Source path
	 * @param targetPath
	 *            : Target path
	 */
	public static void copySmbFileByStream(SmbFile srcFile, SmbFile targetFile) {
		SmbFileInputStream fis = null;
		SmbFileOutputStream fos = null;

		try {
			fis = new SmbFileInputStream(srcFile);
			fos = new SmbFileOutputStream(targetFile);

			byte[] buf = new byte[1024 * 2];
			int i;
			while ((i = fis.read(buf)) != -1) {
				fos.write(buf, 0, i);
			}

		} catch (Exception e) {
			Logs.printStackTrace(TAG + "copySmbFileByStream()1", e);
		} finally {
			try {
				fis.close();
				fos.close();
			} catch (Exception e2) {
				Logs.printStackTrace(TAG + "copySmbFileByStream()2", e2);
			}
		}
	}

	/**
	 * Copy folder from SMB to SMB
	 * 
	 * @param srcPath
	 *            : Source path
	 * @param targetPath
	 *            : Target path
	 */
	public static void copySmbFolder(String srcPath, String targetPath) {
		try {
			//
			SmbFile targetF = new SmbFile(targetPath);
			if (!targetF.exists()) {
				targetF.mkdirs();
			}

			//
			SmbFile srcFolder = new SmbFile(srcPath);
			String[] strFNames = srcFolder.list();

			//
			SmbFile temp = null;
			for (int idx = 0; idx < strFNames.length; idx++) {
				temp = new SmbFile(getPath("", srcPath) + strFNames[idx]);

				// Copy child files
				if (temp.isFile()) {
					copySmbFileByStream(temp, new SmbFile(getPath("", targetPath) + strFNames[idx]));

					// Loop copy child folders
				} else if (temp.isDirectory()) {
					copySmbFolder(getPath("", srcPath) + strFNames[idx], getPath("", targetPath) + strFNames[idx]);
				}
			}
		} catch (Exception e) {
			Logs.printStackTrace(TAG + "copySmbFolder()", e);
		}
	}

	/**
	 * Copy file from Local to SMB
	 * 
	 * @param srcPath
	 *            : Source path
	 * @param targetPath
	 *            : Target path
	 */
	public static void copyFileLocalToSmb(File srcFile, SmbFile targetFile) {
		InputStream fis = null;
		SmbFileOutputStream fos = null;

		try {
			fis = new BufferedInputStream(new FileInputStream(srcFile));
			fos = new SmbFileOutputStream(targetFile);

			byte[] buf = new byte[1024 * 2];
			int i;
			while ((i = fis.read(buf)) != -1) {
				fos.write(buf, 0, i);
			}

		} catch (Exception e) {
			Logs.printStackTrace(TAG + "copyFileLocalToSmb()", e);
		} finally {
			try {
				fis.close();
				fos.close();
			} catch (Exception e2) {
				Logs.printStackTrace(TAG + "copyFileLocalToSmb()2", e2);
			}
		}
	}

	/**
	 * Copy folder from Local to SMB
	 * 
	 * @param srcPath
	 *            : Source path
	 * @param targetPath
	 *            : Target path
	 */
	public static void copyFolderLocalToSmb(String srcPath, String targetPath) {
		try {
			//
			SmbFile targetF = new SmbFile(targetPath);
			if (!targetF.exists()) {
				targetF.mkdirs();
			}

			//
			SmbFile srcFolder = new SmbFile(srcPath);
			String[] strFNames = srcFolder.list();

			//
			File temp = null;
			for (int idx = 0; idx < strFNames.length; idx++) {
				temp = new File(getPath("", srcPath) + strFNames[idx]);

				// Copy child files
				if (temp.isFile()) {
					copyFileLocalToSmb(temp, new SmbFile(getPath("", targetPath) + strFNames[idx]));

					// Loop copy child folders
				} else if (temp.isDirectory()) {
					copyFolderLocalToSmb(getPath("", srcPath) + strFNames[idx], getPath("", targetPath) + strFNames[idx]);
				}
			}
		} catch (Exception e) {
			Logs.printStackTrace(TAG + "copyFolderLocalToSmb()", e);
		}
	}

	/**
	 * Copy file from SMB to Local
	 * 
	 * @param srcPath
	 *            : Source path
	 * @param targetPath
	 *            : Target path
	 */
	public static void copyFileSmbToLocal(SmbFile srcFile, File targetFile) {
		SmbFileInputStream fis = null;
		OutputStream fos = null;

		try {
			fis = new SmbFileInputStream(srcFile);
			fos = new BufferedOutputStream(new FileOutputStream(targetFile));

			byte[] buf = new byte[1024 * 2];
			int i;
			while ((i = fis.read(buf)) != -1) {
				fos.write(buf, 0, i);
			}

		} catch (Exception e) {
			Logs.printStackTrace(TAG + "copyFileSmbToLocal()", e);
		} finally {
			try {
				fis.close();
				fos.close();
			} catch (Exception e2) {
				Logs.printStackTrace(TAG + "copyFileSmbToLocal()2", e2);
			}
		}
	}

	/**
	 * Copy folder from SMB to Local
	 * 
	 * @param srcPath
	 *            : Source path
	 * @param targetPath
	 *            : Target path
	 */
	public static void copyFolderSmbToLocal(String srcPath, String targetPath) {
		try {
			//
			File targetF = new File(targetPath);
			if (!targetF.exists()) {
				targetF.mkdirs();
			}

			//
			SmbFile srcFolder = new SmbFile(srcPath);
			String[] strFNames = srcFolder.list();

			//
			SmbFile temp = null;
			for (int idx = 0; idx < strFNames.length; idx++) {
				temp = new SmbFile(getPath("", srcPath) + strFNames[idx]);

				// Copy child files
				if (temp.isFile()) {
					copyFileSmbToLocal(temp, new File(getPath("", targetPath) + strFNames[idx]));

					// Loop copy child folders
				} else if (temp.isDirectory()) {
					copyFolderLocalToSmb(getPath("", srcPath) + strFNames[idx], getPath("", targetPath) + strFNames[idx]);
				}
			}
		} catch (Exception e) {
			Logs.printStackTrace(TAG + "copyFolderSmbToLocal()", e);
		}
	}
}
