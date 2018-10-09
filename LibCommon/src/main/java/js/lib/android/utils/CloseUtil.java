package js.lib.android.utils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.nio.channels.FileChannel;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.zip.ZipInputStream;

/**
 * Close Methods
 * 
 * @author Jun.Wang
 */
public class CloseUtil {
	/**
	 * Close {@link ZipInputStream}
	 * 
	 * @param zis
	 *            : {@link ZipInputStream}
	 */
	public static void close(ZipInputStream zis) {
		if (zis != null) {
			try {
				zis.closeEntry();
			} catch (Exception e) {
			} finally {
				zis = null;
			}
		}
	}

	/**
	 * Close {@link BufferedInputStream}
	 * 
	 * @param bis
	 *            : {@link BufferedInputStream}
	 */
	public static void close(BufferedInputStream bis) {
		if (bis != null) {
			try {
				bis.close();
			} catch (Exception e) {
			} finally {
				bis = null;
			}
		}
	}

	/**
	 * Close {@link InputStream}
	 * 
	 * @param is
	 *            : {@link InputStream}
	 */
	public static void close(InputStream is) {
		if (is != null) {
			try {
				is.close();
			} catch (Throwable e) {
			} finally {
				is = null;
			}
		}
	}

	/**
	 * Close {@link OutputStream}
	 * 
	 * @param os
	 *            : {@link OutputStream}
	 */
	public static void close(OutputStream os) {
		try {
			os.close();
		} catch (Throwable e) {
		} finally {
			os = null;
		}
	}

	/**
	 * Close {@link FileChannel}
	 * 
	 * @param fc
	 *            : {@link FileChannel}
	 */
	public static void close(FileChannel fc) {
		try {
			fc.close();
		} catch (Throwable e) {
		} finally {
			fc = null;
		}
	}

	/**
	 * Close {@link BufferedReader}
	 * 
	 * @param br
	 *            : {@link BufferedReader}
	 */
	public static void close(BufferedReader br) {
		if (br != null) {
			try {
				br.close();
			} catch (Exception e) {
			} finally {
				br = null;
			}
		}
	}

	/**
	 * Close {@link Reader}
	 * 
	 * @param reader
	 *            : {@link Reader}
	 */
	public static void close(Reader reader) {
		try {
			reader.close();
		} catch (Throwable e) {
		} finally {
			reader = null;
		}
	}

	/**
	 * Close {@link Writer}
	 * 
	 * @param writer
	 *            : {@link Writer}
	 */
	public static void close(Writer writer) {
		try {
			writer.close();
		} catch (Throwable e) {
		} finally {
			writer = null;
		}
	}

	/**
	 * Shutdown ScheduledThreadPoolExecutor
	 * 
	 * @param stpExecutor
	 *            : {@link ScheduledThreadPoolExecutor}
	 * @param task
	 *            : {@link Runnable}
	 */
	public static void shutdown(ScheduledThreadPoolExecutor stpExecutor, Runnable task) {
		if (stpExecutor != null) {
			try {
				if (task != null) {
					stpExecutor.remove(task);
				}
				stpExecutor.shutdown();
			} catch (Exception e) {
			} finally {
				stpExecutor = null;
			}
		}
	}
}
