package js.lib.android.utils;

import java.io.File;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.DisplayMetrics;
import android.widget.ImageView;

import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;

/**
 * Image load class
 * 
 * @author Jun.Wang
 */
public class ImageLoaderUtils {
	// TAG
	private static final String TAG = "ImageLoaderUtils -> ";

	/**
	 * Context
	 */
	private static Context mContext;

	/**
	 * 屏幕宽高
	 */
	private static int screenW, screenH;

	/**
	 * ImageLoader
	 */
	private static ImageLoader imageLoader;

	/**
	 * 网络图片 : "http://site.com/image.png";
	 * <p>
	 * SD卡图片: "file:///mnt/sdcard/image.png";
	 * <p>
	 * 媒体文件夹: "content://media/external/audio/albumart/13";
	 * <p>
	 * assets: "assets://image.png";
	 * <p>
	 * drawable文件: "drawable://" + R.drawable.image;
	 */
	public static void initLoader(Context cxt) {
		//
		mContext = cxt;

		//
		DisplayMetrics dm = cxt.getResources().getDisplayMetrics();
		screenW = dm.widthPixels;
		screenH = dm.heightPixels;
	}

	/**
	 * 初始化图片下载器
	 * 
	 * @param dirName
	 *            : 自定义文件缓存路径，如没有，可设置为""
	 */
	public static ImageLoader getImageLoader(String dirName) {
		//
		imageLoader = ImageLoader.getInstance();

		// 重新初始化ImageLoader时,需要释放资源.
		if (imageLoader.isInited()) {
			imageLoader.destroy();
		}

		//
		imageLoader.init(getConfig(dirName));
		return imageLoader;
	}

	/**
	 * 返回设置信息
	 */
	private static ImageLoaderConfiguration getConfig(String dirName) {
		ImageLoaderConfiguration.Builder configBuilder = new ImageLoaderConfiguration.Builder(mContext);
		// maxwidth, max height，即保存的每个缓存文件的最大长宽
		configBuilder.memoryCacheExtraOptions(screenW, screenH);
		// 线程池内加载的数量
		configBuilder.threadPoolSize(3);
		configBuilder.threadPriority(Thread.NORM_PRIORITY - 2);
		configBuilder.denyCacheImageMultipleSizesInMemory();
		// You can pass your own memory cache implementation/你可以通过自己的内存缓存实现
		configBuilder.memoryCache(new UsingFreqLimitedMemoryCache(2 * 1024 * 1024));
		configBuilder.memoryCacheSize(2 * 1024 * 1024);
		configBuilder.diskCacheSize(100 * 1024 * 1024);
		// 将保存的时候的URI名称用MD5 加密
		configBuilder.diskCacheFileNameGenerator(new Md5FileNameGenerator());
		configBuilder.tasksProcessingOrder(QueueProcessingType.LIFO);
		// 缓存的文件数量
		configBuilder.diskCacheFileCount(500);
		// 自定义缓存路径
		configBuilder.diskCache(new UnlimitedDiskCache(new File(dirName)));
		// connectTimeout (5 s), readTimeout (30 s)超时时间
		configBuilder.defaultDisplayImageOptions(DisplayImageOptions.createSimple());
		configBuilder.imageDownloader(new BaseImageDownloader(mContext, 15 * 1000, 30 * 1000));
		// Remove for releaseapp
		configBuilder.writeDebugLogs();
		return configBuilder.build();
	}

	/**
	 * 返回图像操作
	 */
	public static DisplayImageOptions getOptions() {
		DisplayImageOptions.Builder optionsBuilder = new DisplayImageOptions.Builder();
		// 加载图片时的图片
		// optionsBuilder.showImageOnLoading(R.drawable.ic_stub);
		// 没有图片资源时的默认图片
		// optionsBuilder.showImageForEmptyUri(R.drawable.ic_empty);
		// 加载失败时的图片
		// optionsBuilder.showImageOnFail(R.drawable.ic_error);
		// 启用内存缓存
		optionsBuilder.cacheInMemory(true);
		// 启用外存缓存
		optionsBuilder.cacheOnDisk(true);
		// 启用EXIF和JPEG图像格式
		optionsBuilder.considerExifParams(false);
		// 设置显示风格这里是圆角矩形
		// optionsBuilder.displayer(new RoundedBitmapDisplayer(20));

		//
		// optionsBuilder.resetViewBeforeLoading(false);
		// optionsBuilder.delayBeforeLoading(1000);

		// 设置图片的缩放方式
		// NONE:图片不会调整
		// EXACTLY :图像将完全按比例缩小的目标大小
		// EXACTLY_STRETCHED:图片会缩放到目标大小完全
		// IN_SAMPLE_INT:图像将被二次采样的整数倍
		// IN_SAMPLE_POWER_OF_2:图片将降低2倍，直到下一减少步骤，使图像更小的目标大小
		// optionsBuilder.imageScaleType(ImageScaleType.IN_SAMPLE_INT);

		// 默认是ARGB_8888，使用RGB_565会比使用ARGB_8888少消耗2倍的内
		optionsBuilder.bitmapConfig(Bitmap.Config.RGB_565);

		// 设置图片的显示方式
		// FadeInBitmapDisplayer（int durationMillis） 设置图片渐显的时间
		// new SimpleBitmapDisplayer() : 正常显示一张图片
		optionsBuilder.displayer(new SimpleBitmapDisplayer());

		return optionsBuilder.build();
	}

	/**
	 * 展示图片
	 */
	public static void displayImage(ImageLoader imgLoader, String url, ImageView ivShow) {
		if (imgLoader == null || EmptyUtil.isEmpty(url) || ivShow == null) {
			return;
		}

		try {
			imgLoader.displayImage(url, ivShow, getOptions());
		} catch (Exception e) {
			Logs.printStackTrace(TAG + "displayImage()", e);
		}
	}

	public static void clear() {
		try {
			imageLoader.clearMemoryCache();
			imageLoader.destroy();
		} catch (Exception e) {
			Logs.printStackTrace(TAG + "clear()", e);
		}
	}
}
