package android.pil;

oneway interface IPILListener {

    void onILLStateChanged(int state);

    void onAccStateChanged(int state);

    void onScreenTemperatureOverProtected(boolean overProtected, float temp);

    void onVoltageOverProtected(boolean overProtected, float voltage);

    void onEnvTemperatureOverProtected(boolean overProtected, float temp);

}