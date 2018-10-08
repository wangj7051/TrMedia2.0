package com.tricheer.player.engine;

/**
 * Player CONSTS
 *
 * @author Jun.Wang
 */
public class PlayerConsts {
    // Play From File Manager Parameters
    public static final String INDEX = "index";
    public static final String FILE_LIST = "fileList";

    // AiSpeech Search Music List Parameters
    public static final String TITLE = "title";
    public static final String ARTIST = "artist";

    /**
     * Open Music/QingTingFM Service Flag
     * <p>
     * Values {true/false}
     */
    public static final String IS_JUST_OPEN_SERVICE = "IS_JUST_OPEN_SERVICE";

    // ----Cache Music Play Information to SettingsProvider----
    public static final String DEF_MUSIC_PLAY_STATUS = "music_play_status";
    public static final String DEF_MUSIC_PLAY_NAME = "music_play_name";
    public static final String DEF_MUSIC_PLAY_ARTIST = "music_play_artist";
    public static final String DEF_MUSIC_PLAY_PATH = "music_play_path";
    public static final String DEF_MUSIC_PLAY_IMAGE = "music_play_image";

    // ----Get Value From SettingsProvider----
    public static final String SOUND_MIXING_VAL = "sound_mixing_enabled";
    public static final String SOUND_MIXING_ENABLE = "sound_mixing";
    public static final String HANDBRAKE_FLAG = "hand_break_enabled";

    /**
     * TAB 类型
     */
    public interface TabTypes {
        /**
         * 媒体列表
         */
        int MEDIA_LIST = 1;
        /**
         * 文件列表
         */
        int FOLDER_LIST = 2;
        /**
         * 播放历史记录列表
         */
        int HISTORY_LIST = 2;
    }

    /**
     * 播放器打开方式
     */
    public interface PlayerOpenMethod {
        /**
         * 参数
         */
        String PARAM = "METHOD";

        /**
         * 点击Launcher图标打开
         */
        String VAL_LAUCHER_ICON = "LAUNCHER_ICON";
        /**
         * 点击文件管理器媒体打开
         */
        String VAL_FILE_MANAGER = "FILE_MANAGER";
        /**
         * ??
         */
        String VAL_ONLINE_MEDIA = "ONLINE_MEDIA";
    }
}
