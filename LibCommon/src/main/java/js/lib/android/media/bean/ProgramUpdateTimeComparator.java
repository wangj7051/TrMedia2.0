package js.lib.android.media.bean;

import java.util.Comparator;

/**
 * PinYin Comparator
 *
 * @author Jun.Wang
 */
public class ProgramUpdateTimeComparator implements Comparator<Program> {

    @Override
    public int compare(Program lhs, Program rhs) {
        return (int) (-lhs.updateTime + rhs.updateTime);
    }
}
