package com.tricheer.player.version.cj.slc_lc2010_vdc.bean;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import js.lib.android.media.bean.ProVideo;
import js.lib.android.utils.EmptyUtil;
import js.lib.android.utils.Logs;

public class VideoFilter {
    public boolean isSelected = false;
    public String folderPath = "";
    public String folderPathPinYin = "";
    public List<ProVideo> listMedias;
    public String sortLetter = "";

    public static void sortByFolder(List<VideoFilter> listAfs) {
        if (EmptyUtil.isEmpty(listAfs)) {
            return;
        }
        PinyinComparator comparator = new PinyinComparator();
        for (VideoFilter filter : listAfs) {
            parseSortLetter(filter, filter.folderPathPinYin);
        }
        Collections.sort(listAfs, comparator);
    }

    private static void parseSortLetter(VideoFilter filter, String sortBy) {
        try {
            String firstChar = sortBy.substring(0, 1).toUpperCase();
            if (firstChar.matches("[A-Z]")) {
                filter.sortLetter = firstChar;
            } else {
                filter.sortLetter = "#";
            }
        } catch (Exception e) {
            Logs.printStackTrace("AudioFilter - sortMediaList()", e);
        }
    }

    static class PinyinComparator implements Comparator<VideoFilter> {

        @Override
        public int compare(VideoFilter lhs, VideoFilter rhs) {
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
