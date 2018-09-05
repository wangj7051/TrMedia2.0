package js.lib.android.adapter;

import java.util.List;

import js.lib.android.fragment.BaseAppV4Fragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * 页卡适配器
 * 
 * @author Jun.Wang
 */
public class VPFragStateAdapter extends FragmentStatePagerAdapter {

	/**
	 * Fragment List
	 */
	private List<BaseAppV4Fragment> mListFms;

	/**
	 * Refresh Flag
	 */
	private boolean mIsRefreshFlag = false;

	public VPFragStateAdapter(FragmentManager fm) {
		super(fm);
	}

	public void setListFrags(List<BaseAppV4Fragment> listFms) {
		this.mListFms = listFms;
	}

	public void setRfreshFlag(boolean isRefresh) {
		this.mIsRefreshFlag = isRefresh;
	}

	public void refreshPages(boolean isRefresh) {
		setRfreshFlag(isRefresh);
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		if (mListFms == null) {
			return 0;
		}

		return mListFms.size();
	}

	@Override
	public Fragment getItem(int position) {
		if (mListFms == null || mListFms.size() == 0) {
			return null;
		}

		return mListFms.get(position);
	}

	@Override
	public int getItemPosition(Object object) {
		if (mIsRefreshFlag) {
			return POSITION_NONE;
		}

		return super.getItemPosition(object);
	}
}
