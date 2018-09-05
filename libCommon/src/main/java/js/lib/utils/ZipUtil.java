package js.lib.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import js.lib.android.utils.CloseUtil;


/**
 * 压缩 /解压缩 类
 * 
 * @author Jun.Wang
 */
public class ZipUtil {
	private ZipOutputStream zipOut; // 压缩Zip
	private ZipEntry zipEntry;
	// private int mBufSize; // size of bytes
	private byte[] buf;
	private int readedBytes;

	/**
	 * Constructor
	 */
	public ZipUtil() {
		this(512);
		File f = new File("");
		f.exists();
	}

	/**
	 * Constructor
	 * 
	 * @param bufSize
	 *            : default is 512
	 */
	public ZipUtil(int bufSize) {
		setBufSize(bufSize);
	}

	/**
	 * 设置缓冲区大小
	 * 
	 * @param bufSize
	 *            : default is 512
	 */
	public void setBufSize(final int bufSize) {
		// this.mBufSize = bufSize;
		this.buf = new byte[bufSize];
	}

	/**
	 * 压缩文件或文件夹
	 * 
	 * @param srcPath
	 *            : 要执行压缩的文件或文件夹路径<br/>
	 *            1. 如果压缩文件,则传入应该类似: <br/>
	 *            "F:\MyDoc\WorkDoc\PullToRefreshView\ReadMe.txt"
	 *            <p>
	 *            2. 如果压缩文件夹,则传入应该类似: <br/>
	 *            "F:\MyDoc\WorkDoc\PullToRefreshView"
	 */
	public void compress(String srcPath) {
		File src = new File(srcPath);
		if (src.exists()) {
			if (src.isFile()) {
				compressFile(src);
			} else if (src.isDirectory()) {
				compressFolder(src);
			}
		} else {
			// TODO Add fileNotExist LOG
		}
	}

	/**
	 * 压缩文件
	 * 
	 * @param srcFile
	 *            : 要执行压缩的文件
	 */
	private void compressFile(File srcFile) {
		try {
			// 创建压缩文件对象
			String fileName = srcFile.getPath();
			fileName = fileName.substring(0, fileName.lastIndexOf("."));
			// 父文件夹
			File targetZipFile = new File(fileName + ".zip");

			// 压缩
			this.zipOut = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(targetZipFile)));
			handlerFile("", srcFile);
			this.zipOut.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void handlerFile(String parentPath, File file) throws IOException {
		// 此方法存放在该项目目录下
		if ("".equals(parentPath)) {
			this.zipOut.putNextEntry(new ZipEntry(file.getName().toString()));
			// 生成的压缩包存放在原目录下
		} else {
			this.zipOut.putNextEntry(new ZipEntry(parentPath + "/" + file.getName().toString()));
		}

		// 写入压缩
		FileInputStream fileIn = new FileInputStream(file);
		while ((this.readedBytes = fileIn.read(this.buf)) > 0) {
			this.zipOut.write(this.buf, 0, this.readedBytes);
		}
		this.zipOut.closeEntry();
		fileIn.close();
	}

	/**
	 * 压缩文件夹
	 * 
	 * @param folder
	 *            : 要执行压缩的文件夹
	 */
	private void compressFolder(File srcFolder) {
		try {
			// 创建压缩文件对象
			File targetZipFile = new File(srcFolder.getPath() + ".zip");
			// 压缩
			this.zipOut = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(targetZipFile)));
			handlerFolder(srcFolder);
			this.zipOut.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 递归完成目录文件读取
	private void handlerFolder(File f) throws Exception {
		// 处理子目录
		File[] files = f.listFiles();
		// 如果目录为空,则单独创建之.
		if (files == null || files.length == 0) {
			// ZipEntry的isDirectory()方法中,目录以"/"结尾.
			this.zipOut.putNextEntry(new ZipEntry(f.toString() + "/"));
			this.zipOut.closeEntry();

			// 如果目录不为空,则分别处理目录和文件.
		} else {
			for (File childF : files) {
				if (childF.isDirectory()) {
					handlerFolder(childF);
				} else {
					handlerFile(f.getName(), childF);
				}
			}
		}
	}

	/**
	 * 解压缩指定文件
	 * 
	 * @param srcPath
	 *            :要执行解压的文件，如"F:\xxx.zip"
	 * @param targetDir
	 *            :目标路径,即要解压到哪个文件夹, 如 "F:\"
	 */
	public void decompress(String srcFilePath, String targetDir) {
		// Source File
		File srcFile = new File(srcFilePath);
		if (!srcFile.exists()) {
			return;
		}

		// Target Path Create
		if ("".equals(targetDir)) {
			File srcParentFile = srcFile.getParentFile();
			if (srcParentFile != null) {
				targetDir = srcParentFile.getPath();
			}
		} else {
			File targetFolder = new File(targetDir);
			if (!targetFolder.exists()) {
				targetFolder.mkdir();
			}
		}

		// UnZip
		BufferedInputStream bis = null;
		ZipInputStream zipIn = null;
		try {
			// Loop to UnZip Child
			bis = new BufferedInputStream(new FileInputStream(srcFilePath));
			zipIn = new ZipInputStream(bis);
			while (true) {
				try {
					// TODO 等待改善;路径中包含中文字符串会报异常
					zipEntry = zipIn.getNextEntry();
					if (zipEntry == null) {
						break;
					}

					// 解压缩子文件
					File zipChildF = new File(targetDir + "/" + this.zipEntry.getName());
					if (this.zipEntry.isDirectory()) {
						zipChildF.mkdirs();
					} else {
						// 如果指定文件的目录不存在,则创建之.
						File zipChildParentFile = zipChildF.getParentFile();
						if (!zipChildParentFile.exists()) {
							zipChildParentFile.mkdirs();
						}

						// WriteOut , 存放到指定目录下
						FileOutputStream fileOut = new FileOutputStream(zipChildF);
						while ((this.readedBytes = zipIn.read(this.buf)) > 0) {
							fileOut.write(this.buf, 0, this.readedBytes);
						}
						fileOut.close();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			CloseUtil.close(bis);
			CloseUtil.close(zipIn);
		}
	}
}
