package js.lib.android.adapter;

import java.util.List;

import js.lib.android.fragment.BaseAppV4Fragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * 页卡适配器
 * 
 * @author Jun.Wang
 */
public class VPFragPagerAdapter extends FragmentPagerAdapter {

	/**
	 * Fragment List
	 */
	private List<BaseAppV4Fragment> mListFms;

	public VPFragPagerAdapter(FragmentManager fm) {
		super(fm);
	}

	public void setListFrags(List<BaseAppV4Fragment> listFms) {
		this.mListFms = listFms;
	}

	@Override
	public Fragment getItem(int position) {
		if (mListFms == null || mListFms.size() == 0) {
			return null;
		}

		return mListFms.get(position);
	}

	@Override
	public int getCount() {
		if (mListFms == null) {
			return 0;
		}

		return mListFms.size();
	}
}
