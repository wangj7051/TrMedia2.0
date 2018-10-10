package js.lib.android.media.bean;

import java.util.Comparator;

/**
 * PinYin Comparator
 *
 * @author Jun.Wang
 */
public class PinyinComparator implements Comparator<String> {

    @Override
    public int compare(String lhsChar, String rhsChar) {
        if ("@".equals(lhsChar) || "#".equals(rhsChar)) {
            return -1;
        } else if ("#".equals(lhsChar) || "@".equals(rhsChar)) {
            return 1;
        } else {
            return lhsChar.compareTo(rhsChar);
        }
    }
}
