/*
**
** Copyright 2012, The Android Open Source Project
**
** Licensed under the Apache License, Version 2.0 (the "License");
** you may not use this file except in compliance with the License.
** You may obtain a copy of the License at
**
**     http://www.apache.org/licenses/LICENSE-2.0
**
** Unless required by applicable law or agreed to in writing, software
** distributed under the License is distributed on an "AS IS" BASIS,
** WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
** See the License for the specific language governing permissions and
** limitations under the License.
*/


package android.os;

/**
 *  {@hide}
 */
interface IFmListener {
        void onFreqChanged(int currfreq,int type);
        void onSeachAvailableFreq(int currentSeachFreq,int count,in int[] freqs,int tpye);
        void onStChange(boolean show);

        //
        void onSeachFreqStart(int type);
        void onSeachFreqEnd(int type);
        void onSeachFreqFail(int type,int reason);

        //
        void onScanFreqStart(int type);
        void onScanFreqEnd(int type);
        void onScanFreqFail(int type,int reason);

        //
        void onScanStrongFreqLeftStart(int type);
        void onScanStrongFreqLeftEnd(int type);
        void onScanStrongFreqLeftFail(int type,int reason);
        void onScanStrongFreqRightStart(int type);
        void onScanStrongFreqRightEnd(int type);
        void onScanStrongFreqRightFail(int type,int reason);
}
