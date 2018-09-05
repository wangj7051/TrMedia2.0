package js.lib.android.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import android.content.Context;

/**
 * Assets Operate Methods
 * 
 * @author Jun.Wang
 */
public class AssetsUtil {
	/**
	 * 复制asset文件到指定目录
	 * 
	 * @param oldPath
	 *            asset下的路径<br/>
	 *            比如"assets/theme",则应该 oldPath="theme"
	 * 
	 * @param newPath
	 *            SD卡下保存路径
	 */
	public static void copyAssets(Context context, String oldPath, String newPath) {
		try {
			// Create Target Path
			JsFileUtils.createFolder(newPath);

			// 获取assets目录下的所有文件及目录名
			String fileNames[] = context.getAssets().list(oldPath);
			// 如果是目录
			if (fileNames.length > 0) {
				JsFileUtils.createFolder(oldPath);
				for (String fileName : fileNames) {
					String tmpAssetPath = JsFileUtils.getPath(oldPath, fileName);
					String tmpNewPath = JsFileUtils.getPath(newPath, oldPath);
					copyAssets(context, tmpAssetPath, JsFileUtils.getPath(tmpNewPath, fileName));
				}
				// 如果是文件
			} else {
				// Read
				InputStream is = context.getAssets().open(oldPath);

				// Create Target File
				File targetFile = new File(newPath);
				if (targetFile.exists()) {
					targetFile.delete();
				}
				targetFile.createNewFile();

				// Write to Target File
				FileOutputStream fos = new FileOutputStream(targetFile);
				byte[] buffer = new byte[1024];
				int byteCount = 0;
				while ((byteCount = is.read(buffer)) != -1) {// 循环从输入流读取buffer字节
					fos.write(buffer, 0, byteCount);// 将读取的输入流写入到输出流
				}
				fos.flush();// 刷新缓冲区
				is.close();
				fos.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
