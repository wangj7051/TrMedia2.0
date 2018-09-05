package com.tricheer.player.engine;

import com.tricheer.player.engine.PlayerConsts.PlayMode;

import java.io.Serializable;
import java.util.List;

import js.lib.android.media.local.player.IPlayerListener;

/**
 * 播放器统一行为
 *
 * @author Jun.Wang
 */
public interface PlayerActionsListener extends IPlayerListener {

    /**
     * 设置播放器模式
     * <p>
     * 1. 音乐播放器 - 设置 "顺序/随机/单曲/..." 模式
     *
     * @param mode : {@link PlayMode}
     *             <p>
     *             当 playMode=PlayMode.NONE 时，表示根据上一次的mode设置 ; 否则表示要将mode设置为playMode
     */
    void setPlayMode(int mode);

    /**
     * 当播放模式发生改变
     */
    void onPlayModeChange();

    /**
     * 设置播放列表
     *
     * @param listMedias : List< ProMusic | ProVideo | ... >
     */
    void setPlayList(List<?> listMedias);

    /**
     * 设置播放位置
     *
     * @param position : 目标位置
     */
    void setPlayPosition(int position);

    /**
     * 获取当前媒体对象
     */
    Serializable getCurrMedia();

    /**
     * 获取播放使能标记
     */
    PlayEnableFlag getPlayEnableFlag();

    /**
     * 是否可以播放
     */
    boolean isPlayEnable();

    /**
     * 去除播放线程
     */
    void removePlayRunnable();

    /**
     * 执行播放
     */
    void play();

    /**
     * 执行播放上一个
     */
    void playPrev();

    /**
     * 执行播放上一个
     * <p>
     * 防高频点击
     */
    void playPrevBySecurity();

    /**
     * 执行播放下一个
     */
    void playNext();

    /**
     * 执行播放下一个
     * <p>
     * 防高频点击
     */
    void playNextBySecurity();

    /**
     * 暂停
     */
    void pause();

    /**
     * 被用户暂停
     * <p>
     * "被用户触碰或等同于此动作"的暂停
     */
    void pauseByUser();

    /**
     * 执行恢复播放
     */
    void resume();

    /**
     * 被用户恢复播放
     * <p>
     * "被用户触碰或等同于此动作"的恢复
     */
    void resumeByUser();

    /**
     * 释放播放器
     */
    void release();

    /**
     * 获取上一次播放的媒体路径
     */
    String getLastPath();

    /**
     * 获取上一次播放的媒体进度
     *
     * @return int : 单位 "毫秒" 或 "秒"
     */
    int getLastProgress();

    /**
     * 获取当前媒体路径
     */
    String getPath();

    /**
     * 获取当前媒体在列表中的索引位置
     */
    int getPosition();

    /**
     * 获取媒体列表总数
     */
    int getTotalCount();

    /**
     * 获取当前媒体进度
     *
     * @return int : 单位 "毫秒" 或 "秒"
     */
    int getProgress();

    /**
     * 获取当前媒体总时长
     *
     * @return int : 单位 "毫秒" 或 "秒"
     */
    int getDuration();

    /**
     * 播放器是否正在播放
     */
    boolean isPlaying();

    /**
     * 是否被用户暂停了
     * <p>
     * "被用户触碰或等同于此动作"的暂停
     */
    boolean isPauseByUser();

    /**
     * Seek 到指定进度
     *
     * @param time : 单位 "毫秒" 或 "秒"
     */
    void seekTo(int time);

    /**
     * 调节音量
     * <p>
     * If flag==1, 减小音量
     * <p>
     * If flag==2, 恢复音量
     */
    void adjustVol(int flag);

    /**
     * 上一次指定要播放的媒体路径
     */
    String getLastTargetMediaUrl();

    /**
     * 保存要播放的媒体路径
     */
    void saveTargetMediaUrl(String mediaUrl);

    /**
     * 获取保存的媒体信息
     */
    String[] getPlayedMediaInfos();

    /**
     * 保存播放的媒体信息
     */
    void savePlayMediaInfos(String mediaUrl, int progress);

    /**
     * 清除播放过的媒体信息
     */
    void clearPlayedMediaInfos();

    /**
     * 设置播放器监听器
     *
     * @param l :{@link PlayerActionsListener}
     */
    void setPlayerActionsListener(PlayerActionsListener l);

    /**
     * 移除播放器监听器
     *
     * @param l :{@link PlayerActionsListener}
     */
    void removePlayerActionsListener(PlayerActionsListener l);

    /**
     * 暂时失去AudioFocus，但是可以继续播放，不过要在降低音量。
     */
    void onAudioFocusDuck();

    /**
     * 暂时失去Audio Focus，并会很快再次获得。必须停止Audio的播放， 但是因为可能会很快再次获得AudioFocus，这里可以不释放Media资源；
     */
    void onAudioFocusTransient();

    /**
     * 失去了Audio Focus，并将会持续很长的时间。 这里因为可能会停掉很长时间，所以不仅仅要停止Audio的播放，最好直接释放掉Media资源。
     * 而因为停止播放Audio的时间会很长，如果程序因为这个原因而失去AudioFocus，
     * 最好不要让它再次自动获得AudioFocus而继续播放，不然突然冒出来的声音会让用户感觉莫名其妙，感受很不好。
     * 这里直接放弃AudioFocus，当然也不用再侦听远程播放控制【如下面代码的处理】。 要再次播放，除非用户再在界面上点击开始播放，才重新初始化Media，进行播放
     */
    void onAudioFocusLoss();

    /**
     * 获得了Audio Focus；
     */
    void onAudioFocusGain();
}
