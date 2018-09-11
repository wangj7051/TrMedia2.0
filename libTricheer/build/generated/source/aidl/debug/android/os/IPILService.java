/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: E:\\WorkSpace\\Code\\Studio\\TrMedia2.0\\libTricheer\\src\\main\\aidl\\android\\os\\IPILService.aidl
 */
package android.os;
public interface IPILService extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements android.os.IPILService
{
private static final java.lang.String DESCRIPTOR = "android.os.IPILService";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an android.os.IPILService interface,
 * generating a proxy if needed.
 */
public static android.os.IPILService asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof android.os.IPILService))) {
return ((android.os.IPILService)iin);
}
return new android.os.IPILService.Stub.Proxy(obj);
}
@Override public android.os.IBinder asBinder()
{
return this;
}
@Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
{
switch (code)
{
case INTERFACE_TRANSACTION:
{
reply.writeString(DESCRIPTOR);
return true;
}
case TRANSACTION_setBrightness:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
boolean _result = this.setBrightness(_arg0);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_getVoltage:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.getVoltage();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_getStatus:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.getStatus();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_getBootReason:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.getBootReason();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_setSystemState:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
this.setSystemState(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_setMainAudio:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
this.setMainAudio(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_setSubAudio:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
this.setSubAudio(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_setMainVolume:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
this.setMainVolume(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_setAudioEffect:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
this.setAudioEffect(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_setFmOpen:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
int _arg1;
_arg1 = data.readInt();
this.setFmOpen(_arg0, _arg1);
reply.writeNoException();
return true;
}
case TRANSACTION_setFmClose:
{
data.enforceInterface(DESCRIPTOR);
this.setFmClose();
reply.writeNoException();
return true;
}
case TRANSACTION_setFmSearch:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
int _arg1;
_arg1 = data.readInt();
this.setFmSearch(_arg0, _arg1);
reply.writeNoException();
return true;
}
case TRANSACTION_getFmStatus:
{
data.enforceInterface(DESCRIPTOR);
this.getFmStatus();
reply.writeNoException();
return true;
}
case TRANSACTION_getMainVolume:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.getMainVolume();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_getVersions:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String[] _result = this.getVersions();
reply.writeNoException();
reply.writeStringArray(_result);
return true;
}
case TRANSACTION_commonCmd:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
byte[] _arg1;
_arg1 = data.createByteArray();
byte[] _result = this.commonCmd(_arg0, _arg1);
reply.writeNoException();
reply.writeByteArray(_result);
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements android.os.IPILService
{
private android.os.IBinder mRemote;
Proxy(android.os.IBinder remote)
{
mRemote = remote;
}
@Override public android.os.IBinder asBinder()
{
return mRemote;
}
public java.lang.String getInterfaceDescriptor()
{
return DESCRIPTOR;
}
@Override public boolean setBrightness(int brightness) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(brightness);
mRemote.transact(Stub.TRANSACTION_setBrightness, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public int getVoltage() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getVoltage, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public int getStatus() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getStatus, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public int getBootReason() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getBootReason, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public void setSystemState(int state) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(state);
mRemote.transact(Stub.TRANSACTION_setSystemState, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void setMainAudio(int main) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(main);
mRemote.transact(Stub.TRANSACTION_setMainAudio, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void setSubAudio(int sub) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(sub);
mRemote.transact(Stub.TRANSACTION_setSubAudio, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void setMainVolume(int volume) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(volume);
mRemote.transact(Stub.TRANSACTION_setMainVolume, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void setAudioEffect(int effect) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(effect);
mRemote.transact(Stub.TRANSACTION_setAudioEffect, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void setFmOpen(int band, int freq) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(band);
_data.writeInt(freq);
mRemote.transact(Stub.TRANSACTION_setFmOpen, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void setFmClose() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_setFmClose, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void setFmSearch(int band, int auto) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(band);
_data.writeInt(auto);
mRemote.transact(Stub.TRANSACTION_setFmSearch, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void getFmStatus() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getFmStatus, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public int getMainVolume() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getMainVolume, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public java.lang.String[] getVersions() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.lang.String[] _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getVersions, _data, _reply, 0);
_reply.readException();
_result = _reply.createStringArray();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public byte[] commonCmd(int cmd, byte[] data) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
byte[] _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(cmd);
_data.writeByteArray(data);
mRemote.transact(Stub.TRANSACTION_commonCmd, _data, _reply, 0);
_reply.readException();
_result = _reply.createByteArray();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
}
static final int TRANSACTION_setBrightness = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_getVoltage = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_getStatus = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
static final int TRANSACTION_getBootReason = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
static final int TRANSACTION_setSystemState = (android.os.IBinder.FIRST_CALL_TRANSACTION + 4);
static final int TRANSACTION_setMainAudio = (android.os.IBinder.FIRST_CALL_TRANSACTION + 5);
static final int TRANSACTION_setSubAudio = (android.os.IBinder.FIRST_CALL_TRANSACTION + 6);
static final int TRANSACTION_setMainVolume = (android.os.IBinder.FIRST_CALL_TRANSACTION + 7);
static final int TRANSACTION_setAudioEffect = (android.os.IBinder.FIRST_CALL_TRANSACTION + 8);
static final int TRANSACTION_setFmOpen = (android.os.IBinder.FIRST_CALL_TRANSACTION + 9);
static final int TRANSACTION_setFmClose = (android.os.IBinder.FIRST_CALL_TRANSACTION + 10);
static final int TRANSACTION_setFmSearch = (android.os.IBinder.FIRST_CALL_TRANSACTION + 11);
static final int TRANSACTION_getFmStatus = (android.os.IBinder.FIRST_CALL_TRANSACTION + 12);
static final int TRANSACTION_getMainVolume = (android.os.IBinder.FIRST_CALL_TRANSACTION + 13);
static final int TRANSACTION_getVersions = (android.os.IBinder.FIRST_CALL_TRANSACTION + 14);
static final int TRANSACTION_commonCmd = (android.os.IBinder.FIRST_CALL_TRANSACTION + 15);
}
public boolean setBrightness(int brightness) throws android.os.RemoteException;
public int getVoltage() throws android.os.RemoteException;
public int getStatus() throws android.os.RemoteException;
public int getBootReason() throws android.os.RemoteException;
public void setSystemState(int state) throws android.os.RemoteException;
public void setMainAudio(int main) throws android.os.RemoteException;
public void setSubAudio(int sub) throws android.os.RemoteException;
public void setMainVolume(int volume) throws android.os.RemoteException;
public void setAudioEffect(int effect) throws android.os.RemoteException;
public void setFmOpen(int band, int freq) throws android.os.RemoteException;
public void setFmClose() throws android.os.RemoteException;
public void setFmSearch(int band, int auto) throws android.os.RemoteException;
public void getFmStatus() throws android.os.RemoteException;
public int getMainVolume() throws android.os.RemoteException;
public java.lang.String[] getVersions() throws android.os.RemoteException;
public byte[] commonCmd(int cmd, byte[] data) throws android.os.RemoteException;
}
