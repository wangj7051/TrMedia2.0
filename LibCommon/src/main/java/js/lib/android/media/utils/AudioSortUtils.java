package js.lib.android.media.utils;

import java.util.Collections;
import java.util.List;

import js.lib.android.media.bean.ProAudio;
import js.lib.android.media.bean.Program;
import js.lib.android.media.bean.ProgramPinyinComparator;
import js.lib.android.utils.EmptyUtil;
import js.lib.android.utils.Logs;

public class AudioSortUtils {
    //TAG
    private static final String TAG = "AudioSortUtils";

    public static void sortByTitle(List<? extends Program> listPrograms) {
        if (EmptyUtil.isEmpty(listPrograms)) {
            return;
        }

        ProgramPinyinComparator comparator = new ProgramPinyinComparator();
        for (Program program : listPrograms) {
            if (program instanceof ProAudio) {
                ProAudio media = (ProAudio) program;
                parseSortLetter(media, media.titlePinYin);
            }
        }
        Collections.sort(listPrograms, comparator);
    }

    public static void sortByAlbum(List<? extends Program> listPrograms) {
        if (EmptyUtil.isEmpty(listPrograms)) {
            return;
        }

        ProgramPinyinComparator comparator = new ProgramPinyinComparator();
        for (Program program : listPrograms) {
            if (program instanceof ProAudio) {
                ProAudio media = (ProAudio) program;
                parseSortLetter(media, media.albumPinYin);
            }
        }
        Collections.sort(listPrograms, comparator);
    }

    public static void sortByArtist(List<? extends Program> listPrograms) {
        if (EmptyUtil.isEmpty(listPrograms)) {
            return;
        }

        ProgramPinyinComparator comparator = new ProgramPinyinComparator();
        for (Program program : listPrograms) {
            if (program instanceof ProAudio) {
                ProAudio media = (ProAudio) program;
                parseSortLetter(media, media.artistPinYin);
            }
        }
        Collections.sort(listPrograms, comparator);
    }

    public static void sortByFolder(List<? extends Program> listPrograms) {
        if (EmptyUtil.isEmpty(listPrograms)) {
            return;
        }

        ProgramPinyinComparator comparator = new ProgramPinyinComparator();
        for (Program program : listPrograms) {
            if (program instanceof ProAudio) {
                ProAudio media = (ProAudio) program;
                parseSortLetter(media, media.mediaDirectoryPinYin);
            }
        }
        Collections.sort(listPrograms, comparator);
    }

    private static void parseSortLetter(ProAudio media, String sortBy) {
        try {
            String firstChar = sortBy.substring(0, 1).toUpperCase();
            if (firstChar.matches("[A-Z]")) {
                media.sortLetter = firstChar;
            } else {
                media.sortLetter = "#";
            }
        } catch (Exception e) {
            Logs.printStackTrace(TAG + "sortMediaList()", e);
        }
    }
}
