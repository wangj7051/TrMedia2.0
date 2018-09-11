/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: E:\\WorkSpace\\Code\\Studio\\TrMedia2.0\\RadioBase\\src\\main\\aidl\\android\\os\\IFmManager.aidl
 */
package android.os;
/**
 *  {@hide}
 */
public interface IFmManager extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements android.os.IFmManager
{
private static final java.lang.String DESCRIPTOR = "android.os.IFmManager";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an android.os.IFmManager interface,
 * generating a proxy if needed.
 */
public static android.os.IFmManager asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof android.os.IFmManager))) {
return ((android.os.IFmManager)iin);
}
return new android.os.IFmManager.Stub.Proxy(obj);
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
case TRANSACTION_OpenFm:
{
data.enforceInterface(DESCRIPTOR);
boolean _result = this.OpenFm();
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_getFmMinSearchFreq:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.getFmMinSearchFreq();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_getFmMaxSearchFreq:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.getFmMaxSearchFreq();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_getFmCurrentFreq:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.getFmCurrentFreq();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_setFmCurrentFreq:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
boolean _result = this.setFmCurrentFreq(_arg0);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_closeFm:
{
data.enforceInterface(DESCRIPTOR);
boolean _result = this.closeFm();
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_regisFmStatusListener:
{
data.enforceInterface(DESCRIPTOR);
android.os.IFmListener _arg0;
_arg0 = android.os.IFmListener.Stub.asInterface(data.readStrongBinder());
this.regisFmStatusListener(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_unregisFmStatusListener:
{
data.enforceInterface(DESCRIPTOR);
android.os.IFmListener _arg0;
_arg0 = android.os.IFmListener.Stub.asInterface(data.readStrongBinder());
this.unregisFmStatusListener(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_onStepLeft:
{
data.enforceInterface(DESCRIPTOR);
boolean _result = this.onStepLeft();
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_onStepRight:
{
data.enforceInterface(DESCRIPTOR);
boolean _result = this.onStepRight();
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_onLongPressScanStrongFreqLeft:
{
data.enforceInterface(DESCRIPTOR);
boolean _result = this.onLongPressScanStrongFreqLeft();
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_onLongPressScanStrongFreqRight:
{
data.enforceInterface(DESCRIPTOR);
boolean _result = this.onLongPressScanStrongFreqRight();
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_setSt:
{
data.enforceInterface(DESCRIPTOR);
boolean _arg0;
_arg0 = (0!=data.readInt());
boolean _result = this.setSt(_arg0);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_setLoc:
{
data.enforceInterface(DESCRIPTOR);
boolean _arg0;
_arg0 = (0!=data.readInt());
boolean _result = this.setLoc(_arg0);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_startSearchAvailableFreq:
{
data.enforceInterface(DESCRIPTOR);
boolean _result = this.startSearchAvailableFreq();
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_getSearchFreqs:
{
data.enforceInterface(DESCRIPTOR);
int[] _result = this.getSearchFreqs();
reply.writeNoException();
reply.writeIntArray(_result);
return true;
}
case TRANSACTION_setSwitchType:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
this.setSwitchType(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_getSwitchType:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.getSwitchType();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_startScanFreq:
{
data.enforceInterface(DESCRIPTOR);
boolean _result = this.startScanFreq();
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements android.os.IFmManager
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
@Override public boolean OpenFm() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_OpenFm, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public int getFmMinSearchFreq() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getFmMinSearchFreq, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public int getFmMaxSearchFreq() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getFmMaxSearchFreq, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public int getFmCurrentFreq() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getFmCurrentFreq, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public boolean setFmCurrentFreq(int currfreq) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(currfreq);
mRemote.transact(Stub.TRANSACTION_setFmCurrentFreq, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public boolean closeFm() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_closeFm, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public void regisFmStatusListener(android.os.IFmListener listener) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeStrongBinder((((listener!=null))?(listener.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_regisFmStatusListener, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void unregisFmStatusListener(android.os.IFmListener listener) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeStrongBinder((((listener!=null))?(listener.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_unregisFmStatusListener, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public boolean onStepLeft() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_onStepLeft, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public boolean onStepRight() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_onStepRight, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public boolean onLongPressScanStrongFreqLeft() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_onLongPressScanStrongFreqLeft, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public boolean onLongPressScanStrongFreqRight() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_onLongPressScanStrongFreqRight, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public boolean setSt(boolean enable) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(((enable)?(1):(0)));
mRemote.transact(Stub.TRANSACTION_setSt, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public boolean setLoc(boolean enable) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(((enable)?(1):(0)));
mRemote.transact(Stub.TRANSACTION_setLoc, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public boolean startSearchAvailableFreq() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_startSearchAvailableFreq, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public int[] getSearchFreqs() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int[] _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getSearchFreqs, _data, _reply, 0);
_reply.readException();
_result = _reply.createIntArray();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public void setSwitchType(int type) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(type);
mRemote.transact(Stub.TRANSACTION_setSwitchType, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public int getSwitchType() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getSwitchType, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public boolean startScanFreq() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_startScanFreq, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
}
static final int TRANSACTION_OpenFm = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_getFmMinSearchFreq = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_getFmMaxSearchFreq = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
static final int TRANSACTION_getFmCurrentFreq = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
static final int TRANSACTION_setFmCurrentFreq = (android.os.IBinder.FIRST_CALL_TRANSACTION + 4);
static final int TRANSACTION_closeFm = (android.os.IBinder.FIRST_CALL_TRANSACTION + 5);
static final int TRANSACTION_regisFmStatusListener = (android.os.IBinder.FIRST_CALL_TRANSACTION + 6);
static final int TRANSACTION_unregisFmStatusListener = (android.os.IBinder.FIRST_CALL_TRANSACTION + 7);
static final int TRANSACTION_onStepLeft = (android.os.IBinder.FIRST_CALL_TRANSACTION + 8);
static final int TRANSACTION_onStepRight = (android.os.IBinder.FIRST_CALL_TRANSACTION + 9);
static final int TRANSACTION_onLongPressScanStrongFreqLeft = (android.os.IBinder.FIRST_CALL_TRANSACTION + 10);
static final int TRANSACTION_onLongPressScanStrongFreqRight = (android.os.IBinder.FIRST_CALL_TRANSACTION + 11);
static final int TRANSACTION_setSt = (android.os.IBinder.FIRST_CALL_TRANSACTION + 12);
static final int TRANSACTION_setLoc = (android.os.IBinder.FIRST_CALL_TRANSACTION + 13);
static final int TRANSACTION_startSearchAvailableFreq = (android.os.IBinder.FIRST_CALL_TRANSACTION + 14);
static final int TRANSACTION_getSearchFreqs = (android.os.IBinder.FIRST_CALL_TRANSACTION + 15);
static final int TRANSACTION_setSwitchType = (android.os.IBinder.FIRST_CALL_TRANSACTION + 16);
static final int TRANSACTION_getSwitchType = (android.os.IBinder.FIRST_CALL_TRANSACTION + 17);
static final int TRANSACTION_startScanFreq = (android.os.IBinder.FIRST_CALL_TRANSACTION + 18);
}
public boolean OpenFm() throws android.os.RemoteException;
public int getFmMinSearchFreq() throws android.os.RemoteException;
public int getFmMaxSearchFreq() throws android.os.RemoteException;
public int getFmCurrentFreq() throws android.os.RemoteException;
public boolean setFmCurrentFreq(int currfreq) throws android.os.RemoteException;
public boolean closeFm() throws android.os.RemoteException;
public void regisFmStatusListener(android.os.IFmListener listener) throws android.os.RemoteException;
public void unregisFmStatusListener(android.os.IFmListener listener) throws android.os.RemoteException;
public boolean onStepLeft() throws android.os.RemoteException;
public boolean onStepRight() throws android.os.RemoteException;
public boolean onLongPressScanStrongFreqLeft() throws android.os.RemoteException;
public boolean onLongPressScanStrongFreqRight() throws android.os.RemoteException;
public boolean setSt(boolean enable) throws android.os.RemoteException;
public boolean setLoc(boolean enable) throws android.os.RemoteException;
public boolean startSearchAvailableFreq() throws android.os.RemoteException;
public int[] getSearchFreqs() throws android.os.RemoteException;
public void setSwitchType(int type) throws android.os.RemoteException;
public int getSwitchType() throws android.os.RemoteException;
public boolean startScanFreq() throws android.os.RemoteException;
}
