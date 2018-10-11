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
import android.os.IFmListener;
/**
 *  {@hide}
 */
interface IFmManager {
         boolean OpenFm();
         int getFmMinSearchFreq();
         int getFmMaxSearchFreq();
         int getFmCurrentFreq();
         boolean setFmCurrentFreq(int currfreq);
         boolean closeFm();
         void regisFmStatusListener(in IFmListener listener);
         void unregisFmStatusListener(in IFmListener listener);
         boolean onStepLeft();
         boolean onStepRight();
         boolean onLongPressScanStrongFreqLeft();
         boolean onLongPressScanStrongFreqRight();
         boolean setSt(boolean enable);
         boolean setLoc(boolean enable);
         boolean startSearchAvailableFreq();
         int[] getSearchFreqs();
         void setSwitchType(int type);
         int getSwitchType();
         boolean startScanFreq();
}
