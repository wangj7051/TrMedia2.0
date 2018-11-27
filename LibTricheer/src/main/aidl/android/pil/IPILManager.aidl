package android.pil;

import android.pil.IPILListener;

interface IPILManager {
    void registerPilListener(IPILListener listener);
    void unregisterPilListener(IPILListener listener);
    int getIllState();
    int getAccState();
    String getMcuVersion();
    float getVoltage();
    float getEnvTemperature();
    void setSystemState(int state, int delay);
    void setAmplifierState(int state);
    void setParameter(int key, int value);
}
