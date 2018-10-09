package js.lib.android.media.bean;

import android.util.Log;

import java.util.Comparator;

/**
 * Program PinYin Comparator
 *
 * @author Jun.Wang
 */
public class ProgramPinyinComparator implements Comparator<Program> {

    @Override
    public int compare(Program lhs, Program rhs) {
        if (lhs.sortLetter.equals("@") || rhs.sortLetter.equals("#")) {
            return -1;
        } else if (lhs.sortLetter.equals("#") || rhs.sortLetter.equals("@")) {
            return 1;
        } else {
            int result = lhs.sortLetter.compareTo(rhs.sortLetter);
            Log.i("ProgramPinyinComparator", lhs.sortLetter + " compareTo " + rhs.sortLetter + " == result : " + result);
            return result;
        }
    }
}
