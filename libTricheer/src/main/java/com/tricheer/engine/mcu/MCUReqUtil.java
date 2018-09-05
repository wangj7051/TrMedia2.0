package com.tricheer.engine.mcu;

import android.content.Context;
import android.os.IPILService;
import android.os.RemoteException;
import android.os.ServiceManager;

import com.tricheer.engine.mcu.MCUConsts.HandBrakeStatus;
import com.tricheer.engine.mcu.radio.BandCateory;
import com.tricheer.engine.mcu.radio.BandType;
import com.tricheer.engine.mcu.radio.RadioStatus;

import js.lib.android.utils.Logs;

/**
 * MCU Request Methods
 *
 * @author Jun.Wang
 */
public class MCUReqUtil {
    // TAG
    private String TAG = "MCUReqUtil";
    // private Context mContext;

    /**
     * IPILService
     */
    private IPILService mIPILService;

    /**
     * Constructor
     *
     * @param context : 上下文
     */
    public MCUReqUtil(Context context) {
        // mContext = context;
        mIPILService = IPILService.Stub.asInterface(ServiceManager.getService("pil"));
    }

    public void setLogTAG(String logTAG) {
        TAG += logTAG;
    }

    /**
     * Open Radio
     */
    public void openRadio() {
        Logs.i(TAG, "openRadio()");
        openRadio(-1, -1);
    }

    private void openRadio(int band, int freq) {
        if (mIPILService != null) {
            Logs.i(TAG, "openRadio(band:" + band + " , freq:" + freq + ")");
            try {
                mIPILService.setFmOpen(band, freq);
            } catch (RemoteException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Get Radio Status
     */
    public void getRadioStatus() {
        if (mIPILService != null) {
            Logs.i(TAG, "----getRadioStatus----");
            try {
                mIPILService.getFmStatus();
            } catch (RemoteException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Switch Band
     *
     * @param band : {@link BandType}
     */
    public void switchBand(int band) {
        openRadio(band, -1);
    }

    /**
     * Search Band Frequency
     *
     * @param bandCategory 扫频类型 : 0:FM扫描，1：AM扫描 <br/>
     *                     {@link BandCateory}
     * @param act          <p>
     *                     <li>扫频动作 : 0:手动扫描向上,即搜索上一个可播放频率并播放；
     *                     <li>1：手动扫描向下，即搜索下一个可播放频率并播放；
     *                     <li>2:搜索，即遍历所有波段；
     *                     <li>3:手动向上步进，即当前频率步进-0.1播放；
     *                     <li>4:手动向下步进,即当前频率步进+0.1播放；
     *                     <li>5:PS
     *                     </p>
     */
    private void search(int bandCategory, int act) {
        if (mIPILService != null) {
            Logs.i(TAG, "search(type:" + bandCategory + " , act:" + act + ")");
            try {
                mIPILService.setFmSearch(bandCategory, act);
            } catch (RemoteException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Scan previous Frequency And Play
     *
     * @param bandCatetory : {@link BandCateory}
     */
    public void scanAndPlayPrevFreq(int bandCatetory) {
        search(bandCatetory, 0);
    }

    /**
     * Scan next Frequency And Play
     *
     * @param bandCatetory : {@link BandCateory}
     */
    public void scanAndPlayNextFreq(int bandCatetory) {
        search(bandCatetory, 1);
    }

    /**
     * EXEC Auto Search
     *
     * @param bandCatetory : {@link BandCateory}
     */
    public void search(int bandCatetory) {
        search(bandCatetory, 2);
    }

    /**
     * EXEC Step -0.1 and Play
     *
     * @param bandCatetory : {@link BandCateory}
     */
    public void stepAndPlayPrev(int bandCatetory) {
        search(bandCatetory, 3);
    }

    /**
     * EXEC Step +0.1 and Next
     *
     * @param bandCatetory : {@link BandCateory}
     */
    public void stepAndPlayNext(int bandCatetory) {
        search(bandCatetory, 4);
    }

    /**
     * PS
     *
     * @param bandCatetory : {@link BandCateory}
     */
    public void ps(int bandCatetory) {
        search(bandCatetory, 5);
    }

    /**
     * Set Settings ST Status
     *
     * @param stStatus : {@link RadioStatus} ST_*
     */
    public void setStStatus(int stStatus) {
        if (mIPILService != null) {
            Logs.i(TAG, "setStStatus(stStatus:" + stStatus + ")");
            byte[] byteData = new byte[2];
            byteData[0] = (byte) stStatus;
            byteData[1] = -1;
            try {
                mIPILService.commonCmd(MCUReqCmds.SET_ST_FLAG, byteData);
            } catch (RemoteException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Set Settings LOC Status
     *
     * @param locStatus : {@link RadioStatus} LOC_*
     */
    public void setLocStatus(int locStatus) {
        if (mIPILService != null) {
            Logs.i(TAG, "setLocStatus(locStatus:" + locStatus + ")");
            byte[] byteData = new byte[2];
            byteData[0] = -1;
            byteData[1] = (byte) locStatus;
            try {
                mIPILService.commonCmd(MCUReqCmds.SET_LOC_FLAG, byteData);
            } catch (RemoteException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Set Volume Status
     *
     * @param volStatus : {@link RadioStatus} VOL_*
     */
    public void setVolStatus(int volStatus) {
        if (mIPILService != null) {
            Logs.i(TAG, "setVolStatus(volStatus:" + volStatus + ")");
            byte[] byteData = new byte[1];
            byteData[0] = (byte) volStatus;
            try {
                mIPILService.commonCmd(MCUReqCmds.SET_VOL, byteData);
            } catch (RemoteException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Save current frequency to selected position
     */
    public void saveCurrFreq(int position) {
        if (mIPILService != null) {
            Logs.i(TAG, "saveCurrFreq(position:" + position + ")");
            byte[] byteData = new byte[1];
            byteData[0] = (byte) position;
            try {
                mIPILService.commonCmd(MCUReqCmds.SAVE_CURR_FREQ, byteData);
            } catch (RemoteException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Play Select Position
     */
    public void playSelectedPos(int selectPos) {
        if (mIPILService != null) {
            Logs.i(TAG, "playSelectedPos(selectPos:" + selectPos + ")");
            byte[] data = new byte[1];
            data[0] = (byte) selectPos;
            try {
                mIPILService.commonCmd(MCUReqCmds.OPEN_FM_BY_POS, data);
            } catch (RemoteException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Play Selected Frequency
     *
     * @param bandType : {@link BandType}
     */
    public void playSelectFreq(int bandType, int freq) {
        openRadio(bandType, freq);
    }

    /**
     * Close Radio
     */
    public void closeRadio() {
        if (mIPILService != null) {
            Logs.i(TAG, "----closeRadio()----");
            try {
                mIPILService.setFmClose();
            } catch (RemoteException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Get HandBrake Status
     *
     * @return Integer : {@link HandBrakeStatus}
     */
    public int getHandBrakeOperateStatus() {
        int status = 0;
        if (mIPILService != null) {
            byte[] byteParams = new byte[1];
            byteParams[0] = 0;
            byte[] byteResults;
            try {
                byteResults = mIPILService.commonCmd(MCUReqCmds.GET_DEVICE_STATUS, byteParams);
                status = (byteResults[0] & 0x10) > 0 ? 1 : 0;
            } catch (RemoteException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return status;
    }

    /**
     * 获取设备掉电状态
     *
     * @return boolean : 是否掉电了
     */
    public boolean isLastDevicePowerDown() {
        boolean isLastPowerDown = false;
        if (mIPILService != null) {
            // 获取掉电状态
            try {
                byte[] byteParams = new byte[1];
                byteParams[0] = (byte) MCUReqCmds.DEVICE_POWER$DOWN_STATUS_GET;
                byte[] byteResults = mIPILService.commonCmd(MCUReqCmds.GET_DEVICE_POWER_DOWN_STATUS, byteParams);
                int status = byteResults[0];
                Logs.i(TAG, "^<&&&&&&&&&&&&&&&&  > {isLastDevicePowerDown - status:" + status + "} < &&&&&&&&&&&&&&&&>^");
                isLastPowerDown = (status == MCUConsts.DEVICE_POWER_DOWN);
            } catch (Exception e) {
                Logs.printStackTrace(TAG + "isLastDevicePowerDown()1", e);
            }

            // 如果上一次掉电了，清除掉掉电状态标记，以保证只使用一次
            if (isLastPowerDown) {
                try {
                    byte[] byteParams = new byte[1];
                    byteParams[0] = (byte) MCUReqCmds.DEVICE_POWER$DOWN_STATUS_CLEAR;
                    mIPILService.commonCmd(MCUReqCmds.GET_DEVICE_POWER_DOWN_STATUS, byteParams);
                } catch (Exception e) {
                    Logs.printStackTrace(TAG + "isLastDevicePowerDown()2", e);
                }
            }
        }
        return isLastPowerDown;
    }
}
