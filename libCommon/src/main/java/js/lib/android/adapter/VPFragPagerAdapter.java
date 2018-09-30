package js.lib.android.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * {@link android.support.v4.view.ViewPager} adapter - {@link FragmentPagerAdapter}
 *
 * @author Jun.Wang
 */
public class VPFragPagerAdapter<T extends Fragment> extends FragmentPagerAdapter {

    /**
     * Fragment List
     */
    private List<T> mListFms;

    public VPFragPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public void setListFrags(List<T> listFms) {
        this.mListFms = listFms;
    }

    public void refresh(List<T> listFms) {
        setListFrags(listFms);
        notifyDataSetChanged();
    }

    @Override
    public T getItem(int position) {
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
