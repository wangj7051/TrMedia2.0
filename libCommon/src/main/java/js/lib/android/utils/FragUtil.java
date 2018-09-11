package js.lib.android.utils;

public class FragUtil {

    /**
     * Load V4 Fragment
     */
    public static void loadV4Fragment(int replaceId, android.support.v4.app.Fragment frag,
                                      android.support.v4.app.FragmentManager fm) {
        if (fm != null) {
            android.support.v4.app.FragmentTransaction ft = fm.beginTransaction();
            ft.add(replaceId, frag);
            ft.commit();
        }
    }

    public static void loadV4ChildFragment(int replaceId,
                                           android.support.v4.app.Fragment frag, android.support.v4.app.FragmentManager fm) {
        android.support.v4.app.FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(replaceId, frag);
        transaction.commit();
    }

    /**
     * Load V4 Fragment
     */
    public static void removeV4Fragment(android.support.v4.app.Fragment frag,
                                        android.support.v4.app.FragmentManager fm) {
        if (fm != null) {
            android.support.v4.app.FragmentTransaction ft = fm.beginTransaction();
            ft.remove(frag);
            ft.commit();
        }
    }
}
