package com.tricheer.player.version.cj.slc_lc2010_vdc.bean;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import js.lib.android.media.bean.ProAudio;
import js.lib.android.utils.EmptyUtil;
import js.lib.android.utils.Logs;

public class AudioFilter {
    public boolean isSelected = false;
    public String folderPath = "";
    public String folderPathPinYin = "";
    public String artist = "";
    public String artistPinYin = "";
    public String album = "";
    public String albumPinYin = "";
    public List<ProAudio> listMedias;
    public String sortLetter = "";

    public static void sortByAlbum(List<AudioFilter> listAfs) {
        if (EmptyUtil.isEmpty(listAfs)) {
            return;
        }
        PinyinComparator comparator = new PinyinComparator();
        for (AudioFilter af : listAfs) {
            parseSortLetter(af, af.albumPinYin);
        }
        Collections.sort(listAfs, comparator);
    }

    public static void sortByArtist(List<AudioFilter> listAfs) {
        if (EmptyUtil.isEmpty(listAfs)) {
            return;
        }
        PinyinComparator comparator = new PinyinComparator();
        for (AudioFilter af : listAfs) {
            parseSortLetter(af, af.artistPinYin);
        }
        Collections.sort(listAfs, comparator);
    }

    public static void sortByFolder(List<AudioFilter> listAfs) {
        if (EmptyUtil.isEmpty(listAfs)) {
            return;
        }
        PinyinComparator comparator = new PinyinComparator();
        for (AudioFilter af : listAfs) {
            parseSortLetter(af, af.folderPathPinYin);
        }
        Collections.sort(listAfs, comparator);
    }

    private static void parseSortLetter(AudioFilter af, String sortBy) {
        try {
            String firstChar = sortBy.substring(0, 1).toUpperCase();
            if (firstChar.matches("[A-Z]")) {
                af.sortLetter = firstChar;
            } else {
                af.sortLetter = "#";
            }
        } catch (Exception e) {
            Logs.printStackTrace("AudioFilter - sortMediaList()", e);
        }
    }

    static class PinyinComparator implements Comparator<AudioFilter> {

        @Override
        public int compare(AudioFilter lhs, AudioFilter rhs) {
            if (lhs.sortLetter.equals("@") || rhs.sortLetter.equals("#")) {
                return -1;
            } else if (lhs.sortLetter.equals("#") || rhs.sortLetter.equals("@")) {
                return 1;
            } else {
                return lhs.sortLetter.compareTo(rhs.sortLetter);
            }
        }
    }
}
