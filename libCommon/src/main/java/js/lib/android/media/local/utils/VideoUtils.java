package js.lib.android.media.local.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import js.lib.android.utils.EmptyUtil;
import js.lib.android.utils.Logs;
import js.lib.utils.date.DateFormatUtil;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.provider.MediaStore;

/**
 * Video Common Methods
 * <p>
 * frameworks\base\media\java\android\media\MediaFile.java （1）addFileType("RMVB", FILE_TYPE_RMVB,
 * "video/x-pn-realvideo");//添加RMVB （2）isVideoFileType,条件中添加 fileType == FILE_TYPE_RMVB
 */
public class VideoUtils extends MediaUtils {
	// TAG
	private static final String TAG = "VideoUtils -> ";

	/**
	 * Context
	 */
	private static Context mContext;
	private static ContentResolver mContentResolver;

	/**
	 * You can only get medias at these paths
	 */
	private static List<String> mlistSupportPaths;

	/**
	 * You cannot get medias at these paths
	 */
	private static List<String> mListFilterPaths;

	public static void init(Context cxt, List<String> listSupportPaths, List<String> listFilterPaths) {
		mContext = cxt;
		mContentResolver = mContext.getContentResolver();
		mlistSupportPaths = listSupportPaths;
		mListFilterPaths = listFilterPaths;
	}

	/**
	 * Query Video Paths
	 */
	public static List<VideoInfo> queryListVideoInfos(List<String> listSelectedPaths) {
		//
		List<VideoInfo> listVideoInfos = new ArrayList<VideoInfo>();

		//
		Cursor cur = null;
		try {
			String[] projection = new String[] { VideoInfo._MIME_TYPE, VideoInfo._PATH, VideoInfo._DURATION,
					VideoInfo._DISPLAY_NAME };
			Uri videoUri = VideoInfo.QUERY_URI;
			String sortOrder = MediaStore.Video.Media.DEFAULT_SORT_ORDER;
			String selection = null;
			if (!EmptyUtil.isEmpty(listSelectedPaths)) {
				selection = VideoInfo._PATH + " in " + getSelectionArgs(listSelectedPaths);
			} else if (!EmptyUtil.isEmpty(mlistSupportPaths) || !EmptyUtil.isEmpty(mListFilterPaths)) {
				selection = getSelection();
			}

			Logs.i(TAG, "queryListVideoInfos(listSelectedPaths) -> selection:{" + selection + "}");
			cur = mContentResolver.query(videoUri, projection, selection, null, sortOrder);
			if (cur != null) {
				for (cur.moveToFirst(); !cur.isAfterLast(); cur.moveToNext()) {
					Logs.i(TAG, "queryListVideoInfos -> mimeType:" + cur.getString(cur.getColumnIndex(VideoInfo._MIME_TYPE)));
					VideoInfo video = getVideoByCursor(cur);
					if (video != null) {
						listVideoInfos.add(video);
					}
				}
			}
		} catch (Exception e) {
			Logs.printStackTrace(TAG + "queryListVideoInfos()", e);
		} finally {
			if (cur != null && !cur.isClosed()) {
				cur.close();
			}
		}

		return listVideoInfos;
	}

	/**
	 * Query Video Paths
	 */
	public static Map<String, VideoInfo> queryMapVideoInfos(List<String> listSelectedPaths) {
		//
		Map<String, VideoInfo> mapVideoInfos = new HashMap<String, VideoInfo>();

		//
		Cursor cur = null;
		try {
			String[] projection = new String[] { VideoInfo._MIME_TYPE, VideoInfo._PATH, VideoInfo._DURATION,
					VideoInfo._DISPLAY_NAME };
			Uri videoUri = VideoInfo.QUERY_URI;
			String sortOrder = MediaStore.Video.Media.DEFAULT_SORT_ORDER;
			String selection = null;
			if (!EmptyUtil.isEmpty(listSelectedPaths)) {
				selection = VideoInfo._PATH + " in " + getSelectionArgs(listSelectedPaths);
			} else if (!EmptyUtil.isEmpty(mlistSupportPaths) || !EmptyUtil.isEmpty(mListFilterPaths)) {
				selection = getSelection();
			}

			Logs.i(TAG, "queryMapVideoInfos(listSelectedPaths) -> selection:{" + selection + "}");
			cur = mContentResolver.query(videoUri, projection, selection, null, sortOrder);
			if (cur != null) {
				for (cur.moveToFirst(); !cur.isAfterLast(); cur.moveToNext()) {
					Logs.i(TAG, "queryMapVideoInfos -> mimeType:" + cur.getString(cur.getColumnIndex(VideoInfo._MIME_TYPE)));
					VideoInfo video = getVideoByCursor(cur);
					if (video != null) {
						mapVideoInfos.put(video.path, video);
					}
				}
			}
		} catch (Exception e) {
			Logs.printStackTrace(TAG + "queryMapVideoInfos()", e);
		} finally {
			if (cur != null && !cur.isClosed()) {
				cur.close();
			}
		}

		return mapVideoInfos;
	}

	private static VideoInfo getVideoByCursor(Cursor cur) {
		VideoInfo video = null;

		try {
			String displayName = cur.getString(cur.getColumnIndex(VideoInfo._DISPLAY_NAME));
			String path = cur.getString(cur.getColumnIndex(VideoInfo._PATH));
			if (VideoInfo.isSupport(displayName.substring(displayName.lastIndexOf("."))) && isExist(path)) {
				video = new VideoInfo();
				video.mimeType = cur.getString(cur.getColumnIndex(VideoInfo._MIME_TYPE));
				video.displayName = displayName;
				video.path = path;
				video.duration = DateFormatUtil.getIntSecondMsec(cur.getInt(cur.getColumnIndex(MediaStore.Video.Media.DURATION)));
			}
		} catch (Exception e) {
			Logs.printStackTrace(TAG + "getVideoByCursor()", e);
			video = null;
		}

		return video;
	}

	public static boolean isExist(String path) {
		boolean isExist = false;
		try {
			isExist = (new File(path)).exists();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return isExist;
	}

	/**
	 * Scan the paths.
	 */
	public static void scanVideo(Context cxt, String path, final MediaScannerConnection.OnScanCompletedListener l) {
		scanVideos(cxt, new String[] { path }, l);
	}

	/**
	 * Scan the paths.
	 */
	public static void scanVideos(Context cxt, String[] paths, final MediaScannerConnection.OnScanCompletedListener l) {
		try {
			MediaScannerConnection.scanFile(cxt, paths, VideoInfo.MIME, l);
		} catch (Exception e) {
			Logs.printStackTrace(TAG + "scanVideos()", e);
		}
	}
}
