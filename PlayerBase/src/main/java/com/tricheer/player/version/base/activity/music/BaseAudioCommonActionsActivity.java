package com.tricheer.player.version.base.activity.music;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.tricheer.player.service.TrPlayService;
import com.tricheer.player.service.TrPlayService.LocalBinder;
import com.tricheer.player.version.base.activity.BasePlayerActivity;

import java.util.List;

import js.lib.android.media.bean.MediaBase;
import js.lib.android.media.bean.ProAudio;
import js.lib.android.media.player.PlayListener;
import js.lib.android.media.player.PlayMode;
import js.lib.android.utils.Logs;

/**
 * 播放器统一动作 - BASE
 * <p>
 * (1) 实现了"自定义统一行为"
 * <p>
 * (2) 实现了"注册的服务及广播"的统一行为
 *
 * @author Jun.Wang
 */
public abstract class BaseAudioCommonActionsActivity extends BasePlayerActivity {
    // TAG
    private final String TAG = "MusicComActionsActivity";

    /**
     * Music Play Service
     */
    protected TrPlayService mPlayService;
    protected ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            mPlayService = ((LocalBinder) binder).getService();
            if (mPlayService != null) {
                onPlayServiceConnected(mPlayService);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            // mPlayService = null;
            Log.i(TAG, "mServiceConnection-> onServiceDisconnected");
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle bundle) {
        super.onCreate(bundle);
    }

    /**
     * 当MusicPlayService绑定成功
     */
    protected void onPlayServiceConnected(Service service) {
    }

    @Override
    public void setPlayMode(PlayMode mode) {
        Logs.i(TAG, "^^ setPlayMode(" + mode + ") ^^");
        if (mPlayService != null) {
            mPlayService.setPlayMode(mode);
        }
    }

    @Override
    public void switchPlayMode(int supportFlag) {
        Logs.i(TAG, "^^ switchPlayMode(" + supportFlag + ") ^^");
        if (mPlayService != null) {
            mPlayService.switchPlayMode(supportFlag);
        }
    }

    @Override
    public void setListSrcMedias(List<? extends MediaBase> listSrcMedias) {
//        super.setListSrcMedias(listSrcMedias);
        if (mPlayService != null) {
            mPlayService.setListSrcMedias(listSrcMedias);
        }
    }

    @Override
    public List<ProAudio> getListSrcMedias() {
        Logs.i(TAG, "^^ getListMedias() ^^");
        if (mPlayService != null) {
            List<?> list = mPlayService.getListSrcMedias();
            if (list != null) {
                List<ProAudio> listAudios = (List<ProAudio>) list;
                return listAudios;
            }
        }
        return null;
    }

    @Override
    public void setPlayList(List<? extends MediaBase> listPros) {
        Logs.i(TAG, "^^ setPlayList() ^^");
        if (mPlayService != null) {
            mPlayService.setPlayList(listPros);
        }
    }

    @Override
    public List<ProAudio> getListMedias() {
        Logs.i(TAG, "^^ getListMedias() ^^");
        if (mPlayService != null) {
            List<?> list = mPlayService.getListMedias();
            if (list != null) {
                List<ProAudio> listAudios = (List<ProAudio>) list;
                return listAudios;
            }
        }
        return null;
    }

    @Override
    public void setPlayPosition(int playPos) {
        Logs.i(TAG, "^^ setPlayPosition(" + playPos + ") ^^");
        if (mPlayService != null) {
            mPlayService.setPlayPosition(playPos);
        }
    }

    @Override
    public MediaBase getCurrMedia() {
        if (mPlayService != null) {
            return mPlayService.getCurrMedia();
        }
        return null;
    }

    @Override
    public void play() {
        Logs.i(TAG, "^^ play() ^^");
        if (mPlayService != null) {
            mPlayService.play();
        }
    }

    @Override
    public void play(String mediaPath) {
        Logs.i(TAG, "^^ play(" + mediaPath + ") ^^");
        if (mPlayService != null) {
            mPlayService.play(mediaPath);
        }
    }

    @Override
    public void play(int pos) {
        Logs.i(TAG, "^^ play(" + pos + ") ^^");
        if (mPlayService != null) {
            mPlayService.play(pos);
        }
    }

    @Override
    public void playPrev() {
        Logs.i(TAG, "^^ playPrev() ^^");
        if (mPlayService != null) {
            mPlayService.playPrev();
        }
    }

    @Override
    public void playPrevBySecurity() {
        Logs.i(TAG, "^^ playPrevBySecurity() ^^");
        if (mPlayService != null) {
            mPlayService.playPrevBySecurity();
        }
    }

    @Override
    public void playNext() {
        super.playNext();
        Logs.i(TAG, "^^ playNext() ^^");
        if (mPlayService != null) {
            mPlayService.playNext();
        }
    }

    @Override
    public void playNextBySecurity() {
        Logs.i(TAG, "^^ playNextBySecurity() ^^");
        if (mPlayService != null) {
            mPlayService.playNextBySecurity();
        }
    }

    @Override
    public void pause() {
        Logs.i(TAG, "^^ pause() ^^");
        if (mPlayService != null) {
            mPlayService.pause();
        }
    }

    @Override
    public void pauseByUser() {
        Logs.i(TAG, "^^ pauseByUser() ^^");
        if (mPlayService != null) {
            mPlayService.pauseByUser();
        }
    }

    @Override
    public void resume() {
        Logs.i(TAG, "^^ resume() ^^");
        if (mPlayService != null) {
            mPlayService.resume();
        }
    }

    @Override
    public void resumeByUser() {
        Logs.i(TAG, "^^ resumeByUser() ^^");
        if (mPlayService != null) {
            mPlayService.resumeByUser();
        }
    }

    @Override
    public void release() {
        Logs.i(TAG, "^^ release() ^^");
        if (mPlayService != null) {
            mPlayService.release();
        }
    }

    @Override
    public String getLastMediaPath() {
        if (mPlayService != null) {
            return mPlayService.getLastMediaPath();
        }
        return "";
    }

    @Override
    public long getLastProgress() {
        if (mPlayService != null) {
            return mPlayService.getLastProgress();
        }
        return 0;
    }

    @Override
    public String getCurrMediaPath() {
        if (mPlayService != null) {
            return mPlayService.getCurrMediaPath();
        }
        return "";
    }

    @Override
    public int getCurrIdx() {
        if (mPlayService != null) {
            return mPlayService.getCurrIdx();
        }
        return 0;
    }

    @Override
    public int getTotalCount() {
        if (mPlayService != null) {
            return mPlayService.getTotalCount();
        }
        return 0;
    }

    @Override
    public int getProgress() {
        if (mPlayService != null) {
            return mPlayService.getProgress();
        }
        return 0;
    }

    @Override
    public int getDuration() {
        if (mPlayService != null) {
            return mPlayService.getDuration();
        }
        return 0;
    }

    @Override
    public boolean isPlaying() {
        return mPlayService != null && mPlayService.isPlaying();
    }

    @Override
    public boolean isPauseByUser() {
        return mPlayService != null && mPlayService.isPauseByUser();
    }

    @Override
    public void seekTo(int msec) {
        Logs.i(TAG, "^^ seekTo(" + msec + ") ^^");
        if (mPlayService != null) {
            mPlayService.seekTo(msec);
        }
    }

    @Override
    public void setPlayListener(PlayListener l) {
        if (mPlayService != null) {
            mPlayService.setPlayListener(l);
        }
    }

    @Override
    public void removePlayListener(PlayListener l) {
        if (mPlayService != null) {
            mPlayService.removePlayListener(l);
        }
    }

    /**
     * Bind/Unbind Create/Destroy service
     */
    protected void bindAndCreatePlayService(int... flags) {
        try {
            Intent serviceIntent = new Intent(mContext, TrPlayService.class);
            for (int flag : flags) {
                switch (flag) {
                    case 1:
                        startService(serviceIntent);
                        break;
                    case 2:
                        bindService(serviceIntent, mServiceConnection, BIND_AUTO_CREATE);
                        break;
                    case 3:
                        if (mPlayService != null) {
                            unbindService(mServiceConnection);
                        }
                    case 4:
                        if (mPlayService != null) {
                            stopService(serviceIntent);
                        }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}