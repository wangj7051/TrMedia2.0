package js.lib.android.media.bean;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;

import js.lib.android.media.engine.audio.utils.AudioInfo;
import js.lib.android.utils.EmptyUtil;
import js.lib.android.utils.Logs;
import js.lib.utils.CharacterParser;

/**
 * Audio Program
 *
 * @author Jun.Wang
 */
public class ProAudio extends Program {
    //TAG
    private static final String TAG = "ProAudio";

    /**
     * System Database Media ID
     */
    public long sysMediaID = 0;

    /**
     * Album ID
     */
    public long albumID = 0;
    /**
     * Album Name
     */
    public String album = "";
    /**
     * If album="专辑", albumPinYin="zhuanji"
     */
    public String albumPinYin = "";

    /**
     * Artist
     */
    public String artist = "";
    /**
     * If album="艺术家", albumPinYin="yishujia"
     */
    public String artistPinYin = "";

    /**
     * Words of a song
     */
    public String lyric = "";

    /**
     * Empty Construct
     */
    public ProAudio() {
    }

    /**
     * Construct ProMusic From Media File
     */
    public ProAudio(String mediaPath) {
        File file = new File(mediaPath);
        if (!file.exists()) {
            return;
        }

        //
        //sysMediaID
        String fName = file.getName();
        if (EmptyUtil.isEmpty(fName)) {
            title = UNKNOWN;
        } else {
            int lastIdxOfDot = fName.lastIndexOf(".");
            if (lastIdxOfDot != -1) {
                title = fName.substring(0, lastIdxOfDot);
            }
        }
        titlePinYin = UNKNOWN;

        //
        artist = UNKNOWN;
        artistPinYin = UNKNOWN;

        // albumID
        album = UNKNOWN;
        albumPinYin = UNKNOWN;

        //
        mediaUrl = mediaPath;
        File parentFile = file.getParentFile();
        if (parentFile != null) {
            mediaDirectory = parentFile.getName();
            mediaDirectoryPinYin = UNKNOWN;
        }

        //
        duration = 0;
    }

    /**
     * Construct {@link ProAudio} From {@link AudioInfo}
     */
    public ProAudio(AudioInfo info) {
        this.sysMediaID = info.sysMediaID;
        this.title = info.title;
        this.titlePinYin = info.titlePinYin;
        this.artist = info.artist;
        this.artistPinYin = info.artistPinYin;
        this.albumID = info.albumID;
        this.album = info.album;
        this.albumPinYin = info.albumPinYin;
        this.mediaUrl = info.path;
        this.mediaDirectory = info.directory;
        this.mediaDirectoryPinYin = info.directoryPinYin;
        this.duration = info.duration;
    }

    /**
     * Parse media information
     *
     * @param context {@link Context}
     * @param media   {@link ProAudio}
     */
    public static void parseMedia(Context context, ProAudio media) {
        MediaMetadataRetriever mmr = null;
        try {
            //
            String mediaPath = media.mediaUrl;
            File file = new File(mediaPath);
            if (!file.exists()) {
                return;
            }

            //
            mmr = new MediaMetadataRetriever();
            mmr.setDataSource(context, Uri.parse(mediaPath));

            //
            //sysMediaID
            String parsedTitle = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
            if (!EmptyUtil.isEmpty(parsedTitle)) {
                media.title = parsedTitle;
            }
            media.titlePinYin = CharacterParser.getPingYin(media.title);

            //
            String parsedArtist = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
            if (!EmptyUtil.isEmpty(parsedArtist)) {
                media.artist = parsedArtist;
            }
            media.artistPinYin = CharacterParser.getPingYin(media.artist);

            // albumID
            String parsedAlbum = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
            if (!EmptyUtil.isEmpty(parsedAlbum)) {
                media.album = parsedAlbum;
            }
            media.albumPinYin = CharacterParser.getPingYin(media.album);

            //
//            media.mediaUrl = mediaPath;
            File parentFile = file.getParentFile();
            if (parentFile != null) {
                media.mediaDirectory = parentFile.getName();
                media.mediaDirectoryPinYin = CharacterParser.getPingYin(media.mediaDirectory);
            }

            //
            String strDuration = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            if (TextUtils.isDigitsOnly(strDuration)) {
                media.duration = Integer.parseInt(strDuration);
            }
        } catch (Exception e) {
            Logs.debugI(TAG, "Parse audio failure ....!!!!");
        } finally {
            if (mmr != null) {
                mmr.release();
            }
        }
    }

    /**
     * Get video thumbnail
     */
    public Bitmap getThumbNail(Context context, String mediaPath) {
        MediaMetadataRetriever mmr = null;
        try {
            //
            mmr = new MediaMetadataRetriever();
            mmr.setDataSource(context, Uri.parse(mediaPath));

            //
            byte[] picture = mmr.getEmbeddedPicture();
            if (picture != null) {
                return BitmapFactory.decodeByteArray(picture, 0, picture.length);
            }
        } catch (Exception e) {
            Logs.debugI(TAG, "Parse audio cover image ....!!!!");
        } finally {
            if (mmr != null) {
                mmr.release();
            }
        }
        return null;
    }

    /**
     * Copy srcPro`s DATA to targetPro
     */
    public static void copy(ProAudio targetPro, ProAudio srcPro) {
        try {
            targetPro.sysMediaID = srcPro.sysMediaID;
            targetPro.title = srcPro.title;
            targetPro.titlePinYin = srcPro.titlePinYin;
            targetPro.artist = srcPro.artist;
            targetPro.artistPinYin = srcPro.artistPinYin;
            targetPro.albumID = srcPro.albumID;
            targetPro.album = srcPro.album;
            targetPro.albumPinYin = srcPro.albumPinYin;
            targetPro.mediaUrl = srcPro.mediaUrl;
            targetPro.mediaDirectory = srcPro.mediaDirectory;
            targetPro.mediaDirectoryPinYin = srcPro.mediaDirectoryPinYin;
            targetPro.duration = srcPro.duration;
        } catch (Exception e) {
            Log.i(TAG, "copy(targetPro,srcPro)");
        }
    }
}
