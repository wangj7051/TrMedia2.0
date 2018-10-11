package com.tricheer.player.receiver;

import android.os.AsyncTask;
import android.text.TextUtils;

import com.tricheer.player.utils.PlayerFileUtils;

import java.io.File;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import js.lib.android.media.bean.ProAudio;
import js.lib.android.media.bean.ProVideo;
import js.lib.android.media.engine.audio.utils.AudioInfo;
import js.lib.android.media.engine.audio.utils.AudioUtils;
import js.lib.android.media.engine.audio.db.AudioDBManager;
import js.lib.android.media.engine.video.utils.VideoInfo;
import js.lib.android.media.engine.video.utils.VideoUtils;
import js.lib.android.media.engine.video.db.VideoDBManager;
import js.lib.android.utils.Logs;

/**
 * Task for scanning medias
 */
public class MediaScanAsyncTask extends AsyncTask<Void, Void, Void> {
    //TAG
    private static final String TAG = "MediaScanAsyncTask";

    //
    private Map<String, ProAudio> mmMapDbAudios;
    private Map<String, AudioInfo> mmMapSysDbAudios;
    //
    private Map<String, ProVideo> mmMapDbVideos;
    private Map<String, VideoInfo> mmMapSysDbVideos;

    //
    private Set<String> mSetPathsWillScan = new HashSet<>();

    public MediaScanAsyncTask(Set<String> setPathsWillScan) {
        if (setPathsWillScan != null) {
            mSetPathsWillScan.addAll(setPathsWillScan);
        }
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Logs.i(TAG, "doInBackground");
    }

    @Override
    protected Void doInBackground(Void... voids) {
        Logs.i(TAG, "doInBackground");
        // Query DB Medias
        mmMapDbAudios = AudioDBManager.instance().getMapMusics();
        mmMapSysDbAudios = AudioUtils.queryMapAudioInfos(null);

        // Query SDCard Medias
        mmMapDbVideos = VideoDBManager.instance().getMapVideos(true, false);
        mmMapSysDbVideos = VideoUtils.queryMapVideoInfos(null);

        // List All Medias
        for (String supportPath : mSetPathsWillScan) {
            if (isCancelled()) {
                break;
            }
            Logs.debugI(TAG, "[supportPath:" + supportPath + "]");
            listAllMedias(new File(supportPath));
        }
        return null;
    }

    private void listAllMedias(File pf) {
        if (pf == null || TextUtils.isEmpty(pf.getPath())) {
            Logs.debugI(TAG, "listAllMedias-> ERROR :: NULL");
            return;
        }

        //Loop list files or folders
        try {
            Logs.debugI(TAG, "listAllMedias | " + pf.getPath());
            File[] fArrs = pf.listFiles();
            if (fArrs == null) {
                return;
            }

            for (File cf : pf.listFiles()) {
                if (isCancelled()) {
                    break;
                }

                // Loop List
                if (cf.isDirectory() && !cf.isHidden()) {
                    listAllMedias(cf);
                } else if (cf.isFile()) {
                    parseFileToMedia(cf);
                }
            }
        } catch (Exception e) {
            Logs.debugI(TAG, "EXCEPTION-listAllMedias : " + e.getMessage());
        }
    }

    private void parseFileToMedia(File cf) {
        try {
            String path = cf.getPath();
            Logs.debugI(TAG, "parseFileToMedia(" + path + ")");
            int lastIdxOfDot = path.lastIndexOf(".");
            if (lastIdxOfDot == -1) {
                return;
            }

            // Media Suffix
            String suffix = path.substring(lastIdxOfDot);
            // Get Music
            if (AudioInfo.isSupport(suffix) && !PlayerFileUtils.isInBlacklist(path)) {
                Logs.debugI(TAG, "Audio-> parseFileToMedia() " + cf.getName() + "\n" + path + "\n");
                renameFileWithSpecialName(cf);
                if (mmMapDbAudios.containsKey(path)) {
//                    mListMusics.add(mmMapDbAudios.get(path));
                } else if (mmMapSysDbAudios.containsKey(path)) {
                    ProAudio program = new ProAudio(mmMapSysDbAudios.get(path));
//                    mListNewMusics.add(program);
//                    mListMusics.add(program);
                } else {
                    ProAudio program = new ProAudio(path, PlayerFileUtils.getFileName(cf, false));
//                    mListNewMusics.add(program);
//                    mListMusics.add(program);
//                    mListToSysScanAudios.add(path);
                }

                // Get Video
            } else if (VideoInfo.isSupport(suffix) && !PlayerFileUtils.isInBlacklist(path)) {
                Logs.debugI(TAG, "Video-> parseFileToMedia() " + cf.getName() + "\n" + path + "\n");
                renameFileWithSpecialName(cf);
                if (mmMapDbVideos.containsKey(path)) {
//                    mlistVideos.add(mmMapDbVideos.get(path));
                } else if (mmMapSysDbVideos.containsKey(path)) {
                    ProVideo program = new ProVideo(mmMapSysDbVideos.get(path));
//                    mListNewVideos.add(program);
//                    mlistVideos.add(program);
                } else {
                    ProVideo program = new ProVideo(path, cf.getName());
//                    mListNewVideos.add(program);
//                    mlistVideos.add(program);
//                    mListToSysScanVideos.add(path);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Logs.printStackTrace(TAG + "parseFileToMedia()", e);
        }
    }

    private File renameFileWithSpecialName(File file) {
        if (file != null) {
            String fName = file.getName();
            if (fName.contains("'")) {
                fName = fName.replace("'", "`");
                String fPath = file.getParent() + "/" + fName;
                File targetFile = new File(fPath);
                boolean isRenamed = file.renameTo(targetFile);
                if (isRenamed) {
                    file = targetFile;
                }
            }
        }
        return file;
    }
}
