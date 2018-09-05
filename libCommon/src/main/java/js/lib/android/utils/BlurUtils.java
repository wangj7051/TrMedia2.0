package js.lib.android.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.renderscript.Allocation;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.view.View;

/**
 * 高斯模糊
 * 
 * @author Jun.Wang
 */
public class BlurUtils {
	// TAG
	private static final String TAG = "BlurUtils";

	/**
	 * Context
	 */
	private static Context mContext;

	/**
	 * Handler
	 */
	private static Handler mHandler;

	/**
	 * 糊度
	 */
	private static int mRadius = 20;

	/**
	 * 初始化相关参数
	 */
	public static void init(Context cxt) {
		mContext = cxt;
		mHandler = new Handler();
	}

	/**
	 * 设置糊度
	 * <p>
	 * 建议范围[1,25]
	 */
	public void setRadius(int radius) {
		if (radius <= 0) {
			mRadius = 1;
		} else if (radius >= 25) {
			mRadius = 25;
		} else {
			mRadius = radius;
		}
	}

	/**
	 * 高斯模糊类型
	 */
	public interface IBlurType {
		/**
		 * Render Script Blur, 如果糊度较大，耗时很长，且无法得到糊度很高的图片
		 */
		public final int RENDER_SCRIPT = 1;

		/**
		 * Fast Blur ，该类型速度较慢，建议使用 Advance Fast Blur
		 */
		public final int FAST = 2;

		/**
		 * Advance Fast Blur ， 该方案在速度上优于前两种，建议采用该方案
		 */
		public final int ADVANCED_FAST = 3;
	}

	/**
	 * 执行糊化
	 */
	public static void doBlur(View view, Bitmap srcBitmap, int blurType) {
		// Advance Fast Blur
		if (blurType == IBlurType.ADVANCED_FAST) {
			doAdvancedFastBlur(view, srcBitmap, false, "");

			// Fast Blur
		} else if (blurType == IBlurType.FAST) {
			doFastBlur(view, srcBitmap, false, "");

			// Render Script Blur
		} else if (blurType == IBlurType.RENDER_SCRIPT) {
			doRenderScriptBlur(view, srcBitmap, false, "");
		}
	}

	/**
	 * Advance Fast Blur
	 */
	private static void doAdvancedFastBlur(final View view, final Bitmap srcBitmap, final boolean isStoreToSD,
			final String storeFilePath) {

		new Thread() {
			public void run() {
				try {

					//
					// long startMs = System.currentTimeMillis();

					if (srcBitmap == null) {
						return;
					}

					float scaleFactor = 8;

					//
					int bmW = srcBitmap.getWidth(), bmH = srcBitmap.getHeight();

					Bitmap overlay = Bitmap.createBitmap((int) (bmW / scaleFactor), (int) (bmH / scaleFactor),
							Bitmap.Config.ARGB_8888);

					Paint paint = new Paint();
					paint.setFlags(Paint.FILTER_BITMAP_FLAG);

					Canvas canvas = new Canvas(overlay);
					canvas.translate(-view.getLeft() / scaleFactor, -view.getTop() / scaleFactor);
					canvas.scale(1 / scaleFactor, 1 / scaleFactor);
					canvas.drawBitmap(srcBitmap, 0, 0, paint);

					overlay = applyFastBlur(overlay, mRadius, true);

					//
					setBlurImage(new BitmapDrawable(mContext.getResources(), overlay), view);
				} catch (Exception e) {
					Logs.printStackTrace(TAG + "doAdvancedFastBlur()", e);
				} catch (OutOfMemoryError e) {
					Logs.printStackTrace(TAG + "doAdvancedFastBlur()", e);
				}
			};
		}.start();
	}

	/**
	 * Fast Blur
	 */
	private static void doFastBlur(final View view, final Bitmap srcBitmap, final boolean isStoreToSD, final String storeFilePath) {

		new Thread() {
			public void run() {
				try {
					//
					if (srcBitmap == null) {
						return;
					}

					//
					// long startMs = System.currentTimeMillis();

					//
					int bmW = srcBitmap.getWidth(), bmH = srcBitmap.getHeight();

					Bitmap overlay = Bitmap.createBitmap(bmW, bmH, Bitmap.Config.ARGB_8888);
					Canvas canvas = new Canvas(overlay);
					canvas.translate(-view.getLeft(), -view.getTop());
					canvas.drawBitmap(srcBitmap, 0, 0, null);
					overlay = applyFastBlur(overlay, mRadius, true);

					//
					setBlurImage(new BitmapDrawable(mContext.getResources(), overlay), view);
				} catch (Exception e) {
					Logs.printStackTrace(TAG + "doFastBlur()", e);
				} catch (OutOfMemoryError e) {
					Logs.printStackTrace(TAG + "doFastBlur()", e);
				}
			};
		}.start();
	}

	/**
	 * Render Script Blur
	 */
	private static void doRenderScriptBlur(final View view, final Bitmap srcBitmap, final boolean isStoreToSD,
			final String storeFilePath) {
		Logs.i(TAG, "doRenderScriptBlur -> SDK VERSION CODE : " + android.os.Build.VERSION.SDK_INT);
		// SDK 必须大于16才可以使用Android自带的糊化工具
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
			new Thread() {
				public void run() {
					try {
						if (srcBitmap == null) {
							return;
						}

						//
						// long startMs = System.currentTimeMillis();

						//
						int bmW = srcBitmap.getWidth(), bmH = srcBitmap.getHeight();

						Bitmap overlay = Bitmap.createBitmap(bmW, bmH, Bitmap.Config.ARGB_8888);
						Canvas canvas = new Canvas(overlay);
						canvas.translate(-view.getLeft(), -view.getTop());
						canvas.drawBitmap(srcBitmap, 0, 0, null);

						RenderScript rs = RenderScript.create(mContext);
						Allocation overlayAlloc = Allocation.createFromBitmap(rs, overlay);
						ScriptIntrinsicBlur blur = ScriptIntrinsicBlur.create(rs, overlayAlloc.getElement());
						blur.setInput(overlayAlloc);
						blur.setRadius(mRadius);
						blur.forEach(overlayAlloc);
						overlayAlloc.copyTo(overlay);

						//
						setBlurImage(new BitmapDrawable(mContext.getResources(), overlay), view);

						//
						rs.destroy();
					} catch (Exception e) {
						Logs.printStackTrace(TAG + "doRenderScriptBlur()", e);
					} catch (OutOfMemoryError e) {
						Logs.printStackTrace(TAG + "doRenderScriptBlur()", e);
					}
				};
			}.start();

		} else {
			doAdvancedFastBlur(view, srcBitmap, isStoreToSD, storeFilePath);
		}
	}

	/**
	 * 设置糊化图片
	 */
	@SuppressWarnings("deprecation")
	private static void setBlurImage(final BitmapDrawable resultImg, final View view) {
		mHandler.post(new Runnable() {

			@Override
			public void run() {
				view.setBackgroundDrawable(resultImg);
			}
		});
	}

	/**
	 * @param sentBitmap
	 * @param radius
	 * @param canReuseInBitmap
	 * @return
	 */
	private static Bitmap applyFastBlur(Bitmap sentBitmap, int radius, boolean canReuseInBitmap) {
		Bitmap bitmap;
		if (canReuseInBitmap) {
			bitmap = sentBitmap;
		} else {
			bitmap = sentBitmap.copy(sentBitmap.getConfig(), true);
		}

		if (radius < 1) {
			return (null);
		}

		int w = bitmap.getWidth();
		int h = bitmap.getHeight();

		int[] pix = new int[w * h];
		bitmap.getPixels(pix, 0, w, 0, 0, w, h);

		int wm = w - 1;
		int hm = h - 1;
		int wh = w * h;
		int div = radius + radius + 1;

		int r[] = new int[wh];
		int g[] = new int[wh];
		int b[] = new int[wh];
		int rsum, gsum, bsum, x, y, i, p, yp, yi, yw;
		int vmin[] = new int[Math.max(w, h)];

		int divsum = (div + 1) >> 1;
		divsum *= divsum;
		int dv[] = new int[256 * divsum];
		for (i = 0; i < 256 * divsum; i++) {
			dv[i] = (i / divsum);
		}

		yw = yi = 0;

		int[][] stack = new int[div][3];
		int stackpointer;
		int stackstart;
		int[] sir;
		int rbs;
		int r1 = radius + 1;
		int routsum, goutsum, boutsum;
		int rinsum, ginsum, binsum;

		for (y = 0; y < h; y++) {
			rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
			for (i = -radius; i <= radius; i++) {
				p = pix[yi + Math.min(wm, Math.max(i, 0))];
				sir = stack[i + radius];
				sir[0] = (p & 0xff0000) >> 16;
				sir[1] = (p & 0x00ff00) >> 8;
				sir[2] = (p & 0x0000ff);
				rbs = r1 - Math.abs(i);
				rsum += sir[0] * rbs;
				gsum += sir[1] * rbs;
				bsum += sir[2] * rbs;
				if (i > 0) {
					rinsum += sir[0];
					ginsum += sir[1];
					binsum += sir[2];
				} else {
					routsum += sir[0];
					goutsum += sir[1];
					boutsum += sir[2];
				}
			}
			stackpointer = radius;

			for (x = 0; x < w; x++) {

				r[yi] = dv[rsum];
				g[yi] = dv[gsum];
				b[yi] = dv[bsum];

				rsum -= routsum;
				gsum -= goutsum;
				bsum -= boutsum;

				stackstart = stackpointer - radius + div;
				sir = stack[stackstart % div];

				routsum -= sir[0];
				goutsum -= sir[1];
				boutsum -= sir[2];

				if (y == 0) {
					vmin[x] = Math.min(x + radius + 1, wm);
				}
				p = pix[yw + vmin[x]];

				sir[0] = (p & 0xff0000) >> 16;
				sir[1] = (p & 0x00ff00) >> 8;
				sir[2] = (p & 0x0000ff);

				rinsum += sir[0];
				ginsum += sir[1];
				binsum += sir[2];

				rsum += rinsum;
				gsum += ginsum;
				bsum += binsum;

				stackpointer = (stackpointer + 1) % div;
				sir = stack[(stackpointer) % div];

				routsum += sir[0];
				goutsum += sir[1];
				boutsum += sir[2];

				rinsum -= sir[0];
				ginsum -= sir[1];
				binsum -= sir[2];

				yi++;
			}
			yw += w;
		}
		for (x = 0; x < w; x++) {
			rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
			yp = -radius * w;
			for (i = -radius; i <= radius; i++) {
				yi = Math.max(0, yp) + x;

				sir = stack[i + radius];

				sir[0] = r[yi];
				sir[1] = g[yi];
				sir[2] = b[yi];

				rbs = r1 - Math.abs(i);

				rsum += r[yi] * rbs;
				gsum += g[yi] * rbs;
				bsum += b[yi] * rbs;

				if (i > 0) {
					rinsum += sir[0];
					ginsum += sir[1];
					binsum += sir[2];
				} else {
					routsum += sir[0];
					goutsum += sir[1];
					boutsum += sir[2];
				}

				if (i < hm) {
					yp += w;
				}
			}
			yi = x;
			stackpointer = radius;
			for (y = 0; y < h; y++) {
				// Preserve alpha channel: ( 0xff000000 & pix[yi] )
				pix[yi] = (0xff000000 & pix[yi]) | (dv[rsum] << 16) | (dv[gsum] << 8) | dv[bsum];

				rsum -= routsum;
				gsum -= goutsum;
				bsum -= boutsum;

				stackstart = stackpointer - radius + div;
				sir = stack[stackstart % div];

				routsum -= sir[0];
				goutsum -= sir[1];
				boutsum -= sir[2];

				if (x == 0) {
					vmin[y] = Math.min(y + r1, hm) * w;
				}
				p = x + vmin[y];

				sir[0] = r[p];
				sir[1] = g[p];
				sir[2] = b[p];

				rinsum += sir[0];
				ginsum += sir[1];
				binsum += sir[2];

				rsum += rinsum;
				gsum += ginsum;
				bsum += binsum;

				stackpointer = (stackpointer + 1) % div;
				sir = stack[stackpointer];

				routsum += sir[0];
				goutsum += sir[1];
				boutsum += sir[2];

				rinsum -= sir[0];
				ginsum -= sir[1];
				binsum -= sir[2];

				yi += w;
			}
		}

		bitmap.setPixels(pix, 0, w, 0, 0, w, h);

		return (bitmap);
	}
}
