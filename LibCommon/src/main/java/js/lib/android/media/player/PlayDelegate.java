package js.lib.android.media.player;

import java.util.List;

import js.lib.android.media.bean.MediaBase;
import js.lib.android.media.engine.IAudioFocusListener;

/**
 * Play actions listener
 *
 * @author Jun.Wang
 */
public interface PlayDelegate extends IPlayStateLitener, IPlayProgressListener, IAudioFocusListener {
    /**
     * 设置播放器监听器
     *
     * @param delegate :{@link PlayDelegate}
     */
    void setPlayListener(PlayDelegate delegate);

    /**
     * 移除播放器监听器
     *
     * @param delegate :{@link PlayDelegate}
     */
    void removePlayListener(PlayDelegate delegate);

    /**
     * 设置媒体源数据
     * <p>
     * 请注意，为了保证播放的正常运行，此方法必须在数据获取到以后就设置进来，因为所有的播放数据源都来自此设置的数据集合。
     * </p>
     */
    void setListSrcMedias(List<? extends MediaBase> listSrcMedias);

    /**
     * 获取媒体源数据
     */
    List<? extends MediaBase> getListSrcMedias();

    /**
     * 设置播放列表
     * <p>1st parameter</p>
     *
     * @param listMedias Media list set
     */
    void setPlayList(List<? extends MediaBase> listMedias);

    /**
     * 获取播放列表
     */
    List<? extends MediaBase> getListMedias();

    /**
     * 设置播放位置
     * <p>2nd parameter</p>
     *
     * @param position : 目标位置
     */
    void setPlayPosition(int position);

    /**
     * 获取媒体列表媒体总数
     */
    int getTotalCount();

    /**
     * 获取当前媒体在列表中的索引位置
     */
    int getCurrIdx();

    /**
     * 获取当前媒体
     */
    MediaBase getCurrMedia();

    /**
     * 获取当前媒体路径
     * <p>return "../sdcard/Music/test.mp3"</p>
     */
    String getCurrMediaPath();

    /**
     * 获取当前媒体播放进度
     *
     * @return int : 单位 "毫秒"
     */
    int getProgress();

    /**
     * 获取当前媒体总时长
     *
     * @return int : 单位 "毫秒" 或 "秒"
     */
    int getDuration();

    /**
     * 是否可以播放
     * <p>取决于 {@link PlayEnableController#isPlayEnable()}</p>
     */
    boolean isPlayEnable();

    /**
     * 执行播放
     */
    void play();

    /**
     * 播放指定媒体文件
     *
     * @param mediaPath "../sdcard/Music/test.mp3"
     */
    void play(String mediaPath);

    /**
     * 播放指定媒体文件
     *
     * @param pos 指定位置媒体
     */
    void play(int pos);

    /**
     * 执行播放上一个
     */
    void playPrev();

    /**
     * 执行播放上一个
     * <p>防止高频点击，即用户在短时间内频繁点击执行上一个操作</p>
     */
    void playPrevBySecurity();

    /**
     * 执行播放下一个
     */
    void playNext();

    /**
     * 执行播放下一个
     * <p>防止高频点击，即用户在短时间内频繁点击执行下一个操作</p>
     */
    void playNextBySecurity();

    /**
     * 播放器是否正在播放
     */
    boolean isPlaying();

    /**
     * 暂停
     */
    void pause();

    /**
     * 被用户暂停
     * <p>该操作是由用户的意志而触发的</p>
     * {@link PlayEnableController#pauseByUser(boolean)}
     */
    void pauseByUser();

    /**
     * 是否被用户暂停了
     * <p>"被用户触碰或等同于此动作"的暂停</p>
     * {@link PlayEnableController#isPauseByUser()}
     */
    boolean isPauseByUser();

    /**
     * 执行恢复播放
     */
    void resume();

    /**
     * 被用户恢复播放
     * <p>该操作是由用户的意志而触发的</p>
     */
    void resumeByUser();

    /**
     * 释放播放器
     */
    void release();

    /**
     * Seek 到指定进度
     *
     * @param time : 单位 "毫秒" 或 "秒"
     */
    void seekTo(int time);

    /**
     * 保存目标媒体路径
     *
     * @param mediaPath 这个是指定要播放的，并不一定等同于上次播放过的，可能来源于语音等其它方式通知到播放器的
     */
    void saveTargetMediaPath(String mediaPath);

    /**
     * 上一次指定要播放的媒体路径
     * <p>这个是指定要播放的，并不一定等同于上次播放过的，可能来源于语音等其它方式通知到播放器的</p>
     * <p>注意: 该方法不应当暴露给外部使用，仅用于{{@link #getLastMediaPath()}} 返回.</p>
     */
    String getLastTargetMediaPath();

    /**
     * 获取上一次播放的媒体
     * <p>return 如果{@link #getLastTargetMediaPath()} 不为null,则应当等于{@link #getLastTargetMediaPath()}</p>
     * <p>return 如果{@link #getLastTargetMediaPath()} 不为null,则返回上一次播放存储的媒体路径。</p>
     */
    String getLastMediaPath();

    /**
     * 获取上一次播放的媒体进度
     * <p>条件 {@link #getLastTargetMediaPath()} == {@link #getLastMediaPath()}</p>
     * <p>在满足条件的情况下,返回存储的Progress; 否则应当返回0,并清空存储的Progress</p>
     *
     * @return int : 单位 "毫秒"
     */
    long getLastProgress();

    /**
     * 保存播放的媒体信息
     * <p>这个方法应该在{{@link #onProgressChanged(String, int, int)}}时调用，用来保存即时播放信息</p>
     *
     * @param mediaPath 当前媒体路径
     * @param progress  当前媒体进度
     */
    void savePlayMediaInfos(String mediaPath, int progress);

    /**
     * 获取保存的媒体信息
     * <p>[0] 当前媒体路径</p>
     * <p>[1] 当前媒体进度</p>
     */
    String[] getPlayedMediaInfos();

    /**
     * 清除播放过的媒体信息
     */
    void clearPlayedMediaInfos();

    /**
     * 设置播放器模式
     * <p>循环->随机->单曲->顺序</p>
     *
     * @param supportFlag <p>11~ AUDIO支持 LOOP/RANDOM/SINGLE/ORDER</p>
     *                    <p>12~ AUDIO支持 LOOP/RANDOM/SINGLE</p>
     *                    <p>51~ VIDEO支持 LOOP/RANDOM/SINGLE/ORDER</p>
     *                    <p>52~ VIDEO支持 LOOP/RANDOM/SINGLE</p>
     *                    <p>53~ VIDEO支持 LOOP/SINGLE</p>
     */
    void switchPlayMode(int supportFlag);

    /**
     * 设置播放器模式
     * <p>1. 音乐播放器 - 设置 "顺序/随机/单曲/..." 模式</p>
     *
     * @param mode {@link PlayMode}
     *             <p>将mode设置为playMode</p>
     */
    void setPlayMode(PlayMode mode);

    /**
     * 当播放模式发生改变
     * <p>Should be called after 'switchPlayMode' or 'setPlayMode' to notify play mode changed.</p>
     */
    void onPlayModeChange();
}