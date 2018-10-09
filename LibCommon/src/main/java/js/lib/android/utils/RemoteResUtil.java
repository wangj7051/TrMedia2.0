package js.lib.android.utils;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;

/**
 * Remote Resources Methods
 * <p>
 * 该类用来获取其他APK的资源
 * 
 * @author Jun.Wang
 */
public class RemoteResUtil {

	private String mRemoteAppPackage = "";
	private Context mRemoteCxt;
	private Resources mRemoResources;

	public void init(Context cxt, String remoteAppPackage) {
		try {
			mRemoteAppPackage = remoteAppPackage;
			mRemoteCxt = cxt.createPackageContext(mRemoteAppPackage, Context.CONTEXT_IGNORE_SECURITY);
			mRemoResources = mRemoteCxt.getResources();
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			mRemoteAppPackage = "";
			mRemoteCxt = null;
			mRemoResources = null;
		}
	}

	private int getResID(Resources resources, String resName, String resType, String appPackage) {
		return resources.getIdentifier(resName, resType, appPackage);
	}

	public Drawable getDrawable(String resName) {
		return mRemoResources.getDrawable(getResID(mRemoResources, resName, "drawable", mRemoteAppPackage));
	}

	public String getString(String resName) {
		return mRemoResources.getString(getResID(mRemoResources, resName, "string", mRemoteAppPackage));
	}

	public int getColor(String resName) {
		return mRemoResources.getColor(getResID(mRemoResources, resName, "color", mRemoteAppPackage));
	}
}
