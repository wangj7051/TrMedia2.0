package android.os;

interface IPILService {
	boolean setBrightness(int brightness);
	int getVoltage();
	int getStatus();
	int getBootReason();
	void setSystemState(int state);
	void setMainAudio(int main);
	void setSubAudio(int sub);
	void setMainVolume(int volume);
	void setAudioEffect(int effect);
	void setFmOpen(int band, int freq);
	void setFmClose();
	void setFmSearch(int band, int auto);
	void getFmStatus();
	int getMainVolume();
	String []getVersions();
	byte[] commonCmd(int cmd,in byte[] data);
}