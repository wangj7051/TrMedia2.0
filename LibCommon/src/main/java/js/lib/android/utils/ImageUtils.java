package js.lib.android.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import org.apache.http.protocol.HTTP;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.util.Log;


/**
 * 图片共同方法
 *
 * @author Jun.Wang
 */
public class ImageUtils {
    // TAG
    private static final String TAG = "ImageUtils";

    /**
     * 响应 - 成功
     */
    public static final int RESP_SUCCESS = 0;
    /**
     * 响应 - 异常 - 未知异常
     */
    public static final int RESP_OTHER_ERROR = -1;
    /**
     * 响应 - 异常 - 传入的网络图片路径为空
     */
    public static final int RESP_NONE_URI = 1;
    /**
     * 响应 - 异常 - 同步异常
     */
    public static final int RESP_SYNC_ERROR = 2;

    /**
     * 线程句柄
     */
    private static Handler mHandler = new Handler();

    /**
     * 同步网络图片异步线程
     */
    private static SyncWebImgTask mSyncWebImgTask;

    /**
     * 同步网络图片监听
     */
    public interface SyncImgListener {
        /**
         * 已存在
         */
        void hasExist(String localImgPath);

        /**
         * 同步成功
         */
        void afterSync(String localImgPath);

        /**
         * 同步状态
         * <p>
         * {@link #RESP_SUCCESS}
         * </p>
         * <p>
         * {@link #RESP_OTHER_ERROR}
         * </p>
         * <p>
         * {@link #RESP_NONE_URI}
         * </p>
         * <p>
         * {@link #RESP_SYNC_ERROR}
         * </p>
         */
        public void respStatus(int status);
    }

    /**
     * 取消网络图片同步
     */
    public static void cancelSync() {
        mHandler.removeCallbacksAndMessages(null);
        if (mSyncWebImgTask != null) {
            if (!mSyncWebImgTask.isCancelled()) {
                mSyncWebImgTask.cancel(true);
            }
            mSyncWebImgTask = null;
        }
    }

    /**
     * 同步网络图片到本地
     *
     * @param imgWebUrl      : 网络图片链接
     * @param localStorePath : 本地存储路径
     * @param l              : {@link SyncImgListener}
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static void syncWebImg(String imgWebUrl, String localStorePath, SyncImgListener l) {
        cancelSync();
        mSyncWebImgTask = new SyncWebImgTask(imgWebUrl, localStorePath, l);
        mSyncWebImgTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private static class SyncWebImgTask extends AsyncTask<String, String, Integer> {
        private String mmImgWebUrl = "", mmLocalStorePath = "";
        private SyncImgListener mmListener;

        public SyncWebImgTask(String imgWebUrl, String localStorePath, SyncImgListener l) {
            mmImgWebUrl = imgWebUrl;
            mmLocalStorePath = localStorePath;
            mmListener = l;
        }

        @Override
        protected Integer doInBackground(String... params) {
            // 未传入图片网络地址
            if (EmptyUtil.isEmpty(mmImgWebUrl)) {
                return RESP_NONE_URI;
            }

            // 同步网络图片到本地
            File imgFile = null;
            try {
                // 图片名称
                String imgName = URLEncoder.encode(mmImgWebUrl, HTTP.UTF_8);
                // 文件路径
                final String imgFilePath = mmLocalStorePath + "/" + imgName;

                // 图片文件
                imgFile = new File(imgFilePath);

                // 如果已下载
                if (imgFile.exists()) {
                    postExist(imgFilePath);
                    // 如果文件还未下载
                } else {
                    storeImageToLocal(mmImgWebUrl, imgFilePath);
                    // 下载结束，返回文件路径
                    postSyncResult(imgFilePath);
                }
                return RESP_SUCCESS;
            } catch (Exception e) {
                if (e != null) {
                    Log.i(TAG, "SyncWebImgTask > doInBackground> e: " + e.getMessage());
                }

                // 下载出错后，删除已下载文件
                if (imgFile != null && imgFile.exists()) {
                    imgFile.delete();
                }
            }
            // 未知异常
            return RESP_OTHER_ERROR;
        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            postRespStatus(result);
        }

        private void postExist(final String localImgPath) {
            mHandler.post(new Runnable() {

                @Override
                public void run() {
                    mmListener.hasExist(localImgPath);
                }
            });
        }

        private void postSyncResult(final String localImgPath) {
            mHandler.post(new Runnable() {

                @Override
                public void run() {
                    mmListener.afterSync(localImgPath);
                }
            });
        }

        private void postRespStatus(final int status) {
            if (mmListener != null) {
                mmListener.respStatus(status);
            }
        }
    }

    /**
     * 将获取到的图片存放到本地
     *
     * @param imgUrl   :图片URL
     * @param localUrl : 存储路径 + 图片名
     */
    public static void storeImageToLocal(final String imgUrl, final String localStorePath) throws Exception {
        // new一个URL对象
        URL url = new URL(imgUrl);
        // 打开链接
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        // 设置请求方式为"GET"
        conn.setRequestMethod("GET");
        // 超时响应时间为20秒
        conn.setConnectTimeout(20 * 1000);
        // 通过输入流获取图片数据
        InputStream inStream = conn.getInputStream();
        // 得到图片的二进制数据，以二进制封装得到数据，具有通用性
        byte[] data = readInputStream(inStream);
        // new一个文件对象用来保存图片，默认保存当前工程根目录
        File imageFile = new File(localStorePath);
        if (!imageFile.exists()) {
            imageFile.createNewFile();
        }
        // 创建输出流
        FileOutputStream outStream = new FileOutputStream(imageFile);
        // 写入数据
        outStream.write(data);
        // 关闭输出流
        outStream.close();
    }

    /**
     * 将获取到的图片对象,读取为字节流
     *
     * @param inStream : 文件流
     */
    public static byte[] readInputStream(InputStream inStream) throws Exception {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        // 创建一个Buffer字符串
        byte[] buffer = new byte[1024];
        // 每次读取的字符串长度，如果为-1，代表全部读取完毕
        int len = 0;
        // 使用一个输入流从buffer里把数据读取出来
        while ((len = inStream.read(buffer)) != -1) {
            // 用输出流往buffer里写入数据，中间参数代表从哪个位置开始读，len代表读取的长度
            outStream.write(buffer, 0, len);
        }
        // 关闭输入流
        inStream.close();
        // 把outStream里的数据写入内存
        return outStream.toByteArray();
    }

    /**
     * 保存Bitmap到SD卡
     *
     * @param filePath  ： 文件路径 ，格式为“.../../example.png”
     * @param bmToStore ： 要执行保存的Bitmap
     */
    public static void storeBitmap(String filePath, Bitmap bmToStore) {
        // "/sdcard/" + bitName + ".png"
        try {
            //
            File f = new File(filePath);
            if (f.exists()) {
                f.delete();
            }

            if (f.isDirectory()) {
                return;
            }

            f.createNewFile();

            //
            FileOutputStream fOut = new FileOutputStream(f);

            //
            bmToStore.compress(Bitmap.CompressFormat.PNG, 100, fOut);

            // 关闭流操作
            fOut.flush();
            fOut.close();

        } catch (Throwable e) {
            if (e != null) {
                Log.i(TAG, "storeBitmap > e: " + e.getMessage());
            }
        }
    }

    /**
     * 将{@link Drawable}转化为 {@link Bitmap}
     */
    public static Bitmap drawable2Bitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        } else if (drawable instanceof NinePatchDrawable) {
            Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(),
                    drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            drawable.draw(canvas);
            return bitmap;
        } else {
            return null;
        }
    }
}
