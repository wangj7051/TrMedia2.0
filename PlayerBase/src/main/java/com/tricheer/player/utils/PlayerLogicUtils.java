package com.tricheer.player.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.text.TextUtils;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.tricheer.app.receiver.PlayerReceiverActions;
import com.tricheer.player.R;
import com.tricheer.player.bean.ProMusic;
import com.tricheer.player.bean.ProVideo;
import com.tricheer.player.bean.Program;
import com.tricheer.player.engine.PlayerAppManager.PlayerCxtFlag;
import com.tricheer.player.engine.PlayerConsts;
import com.tricheer.player.engine.VersionController;
import com.tricheer.player.receiver.PlayerReceiver;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import js.lib.android.media.audio.utils.MediaUtils;
import js.lib.android.utils.EmptyUtil;
import js.lib.android.utils.ImageLoaderUtils;
import js.lib.android.utils.Logs;
import js.lib.android.utils.SysVolUtils;
import js.lib.utils.ChineseUtils;

/**
 * Player Logic Methods
 *
 * @author Jun.Wang
 */
public class PlayerLogicUtils {
    // Tag
    private static final String TAG = "PlayerLogicUtils";

    /**
     * Get String first Char ASCII
     */
    public static int getStrFirstChar(String str) {
        if (EmptyUtil.isEmpty(str)) {
            return -1;
        }
        return str.charAt(0);
    }

    /**
     * Get PicFile Path
     *
     * @param picType : 1 Music Picture
     *                <p>
     *                2 Video Picture
     */
    public static String getMediaPicPath(String mediaName, int picType) {
        String picFilePath = "";
        try {
            String picName = URLEncoder.encode(mediaName, "UTF-8") + ".png";
            if (picType == 1) {
                picFilePath = PlayerFileUtils.getMusicPicPath(mediaName) + "/" + picName;
            } else if (picType == 2) {
                picFilePath = PlayerFileUtils.getVideoPicPath(mediaName) + "/" + picName;
            }
        } catch (Exception e) {
            Logs.printStackTrace(TAG + "getMediaPicPath()", e);
        }
        return picFilePath;
    }

    /**
     * Get Media Cover Image File Path
     */
    public static String getMediaPicFilePath(final Program program, final String storePath) throws UnsupportedEncodingException {
        if (!TextUtils.isEmpty(storePath) && storePath.endsWith("/")) {
            return storePath + URLEncoder.encode(program.title, "UTF-8") + ".png";
        }
        return storePath + "/" + URLEncoder.encode(program.title, "UTF-8") + ".png";
    }

    public static boolean isHttpUrl(String url) {
        if (EmptyUtil.isEmpty(url)) {
            return false;
        }

        if (url.startsWith("http://") || url.startsWith("HTTP://") || url.startsWith("https://") || url.startsWith("HTTPS://")) {
            return true;
        }

        return false;
    }

    /**
     * Set media cover Image
     */
    public static void setMediaCover(ImageView ivCover, ProMusic program) {
        if (EmptyUtil.isEmpty(program.coverUrl)) {
            ivCover.setImageResource(0);
        } else {
            ivCover.setImageURI(Uri.parse(program.coverUrl));
        }
    }

    /**
     * Set media cover Image
     */
    public static void setMediaCover(ImageView ivCover, ProMusic program, ImageLoader imgLoader) {
        if (EmptyUtil.isEmpty(program.coverUrl)) {
            ivCover.setImageResource(R.drawable.bg_cover_music);
        } else if (isHttpUrl(program.coverUrl)) {
            if (imgLoader != null) {
                ImageLoaderUtils.displayImage(imgLoader, program.coverUrl, ivCover);
            } else {
                ivCover.setImageResource(R.drawable.bg_cover_music);
            }
        } else {
            ivCover.setImageURI(Uri.parse(program.coverUrl));
        }
    }

    /**
     * Set media cover Image
     */
    public static void setMediaCover(ImageView ivCover, ProVideo program, boolean isSetSelected) {
        File bmFile = new File(PlayerLogicUtils.getMediaPicPath(program.mediaUrl, 2));
        if (bmFile.exists()) {
            ivCover.setImageURI(Uri.parse(bmFile.getPath()));
            if (isSetSelected) {
                ivCover.setBackgroundResource(R.drawable.bg_cover_video);
            } else {
                ivCover.setBackgroundResource(0);
            }
        } else {
            ivCover.setImageResource(R.drawable.bg_cover_video);
            ivCover.setBackgroundResource(0);
        }
    }

    /**
     * Return String or UnKnow
     */
    public static String getStrOrUnKnow(Context cxt, String str) {
        if (EmptyUtil.isEmpty(str) || ChineseUtils.isMessyCode(str) || "<unknown>".equalsIgnoreCase(str.trim())) {
            return cxt.getString(R.string.unknow);
        }

        return str;
    }

    /**
     * Return String or UnKnow
     */
    public static String getUnKnowOnNull(Context cxt, String str) {
        if (EmptyUtil.isEmpty(str)) {
            return cxt.getString(R.string.unknow);
        }
        return str;
    }

    /**
     * Notify AiSpeech Play String
     */
    public static void notifyAisPlayStr(Context cxt, String str) {
        Intent notifyAisIntent = new Intent(PlayerReceiverActions.NOTIFY_AIS_PLAY_STR);
        notifyAisIntent.putExtra("NOTIFY_PLAY_STR", str);
        cxt.sendBroadcast(notifyAisIntent);
    }

    /**
     * Notify local media player opened
     * <p>
     *
     * @param player : ["MUSIC_PLAYER" / "VIDEO_PLAYER"]
     */
    public static void notifyLocalMediaOpen(Context cxt, String player) {
        Logs.i(TAG, "^^ notifyLocalMediaOpen(cxt," + player + ") ^^");
        Intent mediaPlayerOpenIntent = new Intent(PlayerReceiverActions.MEDIA_PLAYER_OPEN);
        mediaPlayerOpenIntent.putExtra("PLAYER_FLAG", player);
        cxt.sendBroadcast(mediaPlayerOpenIntent);
    }

    /**
     * Get Temperature Mode
     *
     * @return 1 高温模式(>105度), 0 低温模式(<98度)
     */
    public static int getTemperatureMode() {
        try {
            File f1 = new File("/sys/devices/virtual/thermal/thermal_zone5/temp");
            FileInputStream fis = new FileInputStream(f1);
            InputStreamReader isReader = new InputStreamReader(fis);
            BufferedReader bufferedReader = new BufferedReader(isReader);
            String strTempVal = bufferedReader.readLine();
            int temp1 = Integer.valueOf(strTempVal) / 1000;
            bufferedReader.close();
            isReader.close();
            fis.close();

            File f2 = new File("/sys/devices/virtual/thermal/thermal_zone4/temp");
            fis = new FileInputStream(f2);
            isReader = new InputStreamReader(fis);
            bufferedReader = new BufferedReader(isReader);
            strTempVal = bufferedReader.readLine();
            int temp2 = Integer.valueOf(strTempVal);
            bufferedReader.close();
            isReader.close();
            fis.close();

            if (temp1 > 105 || temp2 > 105) {
                return 1;
            }
        } catch (Exception e) {
            Logs.printStackTrace(TAG + "getTemperatureMode()", e);
        }
        return 0;
    }

    /**
     * Cache Program Information
     * <p>
     * This method used to set program path/name/image for Screen/Launcher
     */
    public static void cacheMusicProgram(ContentResolver cr, ProMusic program) {
        if (VersionController.isCanAutoResume()) {
            try {
                Logs.i(TAG, "cacheMusicProgram(cr,program) ----Start----");
                Settings.Global.putString(cr, PlayerConsts.DEF_MUSIC_PLAY_NAME, program.title);
                Settings.Global.putString(cr, PlayerConsts.DEF_MUSIC_PLAY_ARTIST, program.artist);
                Settings.Global.putString(cr, PlayerConsts.DEF_MUSIC_PLAY_PATH, program.mediaUrl);
                Settings.Global.putString(cr, PlayerConsts.DEF_MUSIC_PLAY_IMAGE, program.coverUrl);
                cr.notifyChange(Settings.System.getUriFor(PlayerConsts.DEF_MUSIC_PLAY_PATH), null);
                Logs.i(TAG, "cacheMusicProgram(cr,program) ----notifyChange----");
            } catch (Exception e) {
                Logs.i(TAG, "cacheMusicProgram(cr,program) ----Failure----");
                Logs.printStackTrace(TAG + "cacheMusicProgram()", e);
            } finally {
                Logs.i(TAG, "cacheMusicProgram(cr,program) ----cacheMusicProgram----End----");
            }
        }
    }

    /**
     * Cache Player State
     *
     * @param playerFlag {@link PlayerCxtFlag}
     * @param playState  : 1 playing, 0 not playing
     */
    public static void cachePlayerState(ContentResolver cr, int playerFlag, int playState) {
        if (VersionController.isCanAutoResume()) {
            try {
                Logs.i(TAG, "cachePlayerState(cr,playerFlag,playState) ----Start----");
                // Music
                if (playerFlag == PlayerCxtFlag.MUSIC_PLAYER) {
                    Settings.Global.putInt(cr, PlayerConsts.DEF_MUSIC_PLAY_STATUS, playState);
                    Logs.i(TAG, "DEF_MUSIC_PLAY_STATUS : " + playState);
                    cr.notifyChange(Settings.System.getUriFor(PlayerConsts.DEF_MUSIC_PLAY_STATUS), null);
                    Logs.i(TAG, "----NotifyPlayState----");
                }
            } catch (Exception e) {
                Logs.printStackTrace(TAG + "cachePlayerState()", e);
                Logs.i(TAG, "----Failure----");
            } finally {
                Logs.i(TAG, "cachePlayerState(cr,playerFlag,playState) ----End----");
            }
        }
    }

    /**
     * Get HandBrake Enable
     */
    public static boolean isHandBrakeEnable(ContentResolver cr) {
        boolean isEnable = false;
        try {
            int handbrakeFlag = Settings.System.getInt(cr, PlayerConsts.HANDBRAKE_FLAG);
            Logs.i(TAG, "----getSoundMixVal---->handbrakeFlag:[" + handbrakeFlag + "]");
            isEnable = (handbrakeFlag == 1) ? true : false;
        } catch (Exception e) {
            Logs.printStackTrace(TAG + "isHandBrakeEnable()", e);
            isEnable = false;
        }
        return isEnable;
    }

    /**
     * Get Sound Mixing Value
     */
    public static float getSoundMixVal(Context cxt) {
        float playerMixVal = 0f;
        try {
            ContentResolver cr = cxt.getContentResolver();
            int mixEnable = Settings.System.getInt(cr, PlayerConsts.SOUND_MIXING_ENABLE);
            Logs.i(TAG, "----getSoundMixVal---->mixEnable:[" + mixEnable + "]");
            if (mixEnable == 1) {
                int sysVol = SysVolUtils.getMusicVolVal(cxt, false);
                if (sysVol <= 15) {
                    playerMixVal = 1.00f;
                    // Volume Mix is enable only sysVol > 15
                } else {
                    int mixRatio = Settings.System.getInt(cr, PlayerConsts.SOUND_MIXING_VAL);
                    Logs.i(TAG, "----getSoundMixVal---->mixRatio:[" + mixRatio + "]");
                    switch (mixRatio) {
                        case 10:
                            playerMixVal = 1.00f;
                            break;
                        case 9:
                            playerMixVal = 0.4f;
                            break;
                        case 8:
                            playerMixVal = 0.30f;
                            break;
                        case 7:
                            playerMixVal = 0.21f;
                            break;
                        case 6:
                            playerMixVal = 0.18f;
                            break;
                        case 5:
                            playerMixVal = 0.15f;
                            break;
                        case 4:
                            playerMixVal = 0.12f;
                            break;
                        case 3:
                            playerMixVal = 0.09f;
                            break;
                        case 2:
                            playerMixVal = 0.06f;
                            break;
                        case 1:
                            playerMixVal = 0.03f;
                            break;
                    }
                }
            }
        } catch (Exception e) {
            Logs.printStackTrace(TAG + "getSoundMixVal()", e);
            playerMixVal = 0f;
        }
        Logs.i(TAG, "getSoundMixVal(cr) -> [mediaPlayerMixVal:" + playerMixVal + "]");
        return playerMixVal;
    }

    /**
     * Get BlueTooth Dialing Status
     *
     * @return : 1 dialing, 0 no dialing
     */
    public static int getDialingStatus(Context cxt) {
        int dialingStatus = 0;
        try {
            dialingStatus = Settings.System.getInt(cxt.getContentResolver(), "exist_bluetooth_calling");
        } catch (SettingNotFoundException e) {
            dialingStatus = 0;
        }
        return dialingStatus;
    }

    /**
     * Print Play State
     */
    public static void printPlayState(String tag, String state) {
        Logs.i(TAG, " ");
        Logs.i(TAG, "---->>>> playState:[" + state + "] <<<<----");
    }

    /**
     * Toast Play Error
     */
    public static void toastPlayError(Context cxt, String mediaTitle) {
        String errorMsg = String.format(cxt.getString(R.string.play_error), mediaTitle);
        Logs.i(TAG, errorMsg);
//        Toast.makeText(cxt, errorMsg, Toast.LENGTH_LONG).show();
    }

    /**
     * Get Media Title
     */
    public static String getMediaTitle(Context context, int position, Program program, boolean isContainSuffix) {
        String title = "";
        try {
            if (position >= 0) {
                title = position + ". ";
            }
            title += getUnKnowOnNull(context, program.title);
            if (isContainSuffix) {
                title += MediaUtils.getSuffix(program.mediaUrl);
            }
        } catch (Exception e) {
            title = "";
        }
        return title;
    }

    /**
     * 蓝牙电话是否进行中 ...
     */
    public static boolean isBtCalling(Context cxt) {
        int btCallingStatus = getDialingStatus(cxt);
        return (btCallingStatus == 1 || PlayerReceiver.isBtCalling);
    }
}
