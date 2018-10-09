package js.lib.android.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.List;

/**
 * {@link android.support.v4.view.ViewPager} adapter - {@link FragmentStatePagerAdapter}
 *
 * @author Jun.Wang
 */
public class VPFragStateAdapter<T extends Fragment> extends FragmentStatePagerAdapter {

    /**
     * Fragment List
     */
    private List<T> mListFms;

    /**
     * Refresh Flag
     */
    private boolean mIsRefreshFlag = false;

    public VPFragStateAdapter(FragmentManager fm) {
        super(fm);
    }

    public void setListFrags(List<T> listFms) {
        this.mListFms = listFms;
    }

    public void setRfreshFlag(boolean isRefresh) {
        this.mIsRefreshFlag = isRefresh;
    }

    public void refreshPages(boolean isRefresh) {
        setRfreshFlag(isRefresh);
        notifyDataSetChanged();
    }

    public void refresh(List<T> listFms) {
        setListFrags(listFms);
        notifyDataSetChanged();
    }

    public void refresh(List<T> listFms, boolean isRefresh) {
        setRfreshFlag(isRefresh);
        setListFrags(listFms);
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
