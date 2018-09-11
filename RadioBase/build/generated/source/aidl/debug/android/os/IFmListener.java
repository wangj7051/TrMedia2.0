/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: E:\\WorkSpace\\Code\\Studio\\TrMedia2.0\\RadioBase\\src\\main\\aidl\\android\\os\\IFmListener.aidl
 */
package android.os;
/**
 *  {@hide}
 */
public interface IFmListener extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements android.os.IFmListener
{
private static final java.lang.String DESCRIPTOR = "android.os.IFmListener";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an android.os.IFmListener interface,
 * generating a proxy if needed.
 */
public static android.os.IFmListener asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof android.os.IFmListener))) {
return ((android.os.IFmListener)iin);
}
return new android.os.IFmListener.Stub.Proxy(obj);
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
case TRANSACTION_onFreqChanged:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
int _arg1;
_arg1 = data.readInt();
this.onFreqChanged(_arg0, _arg1);
reply.writeNoException();
return true;
}
case TRANSACTION_onSeachAvailableFreq:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
int _arg1;
_arg1 = data.readInt();
int[] _arg2;
_arg2 = data.createIntArray();
int _arg3;
_arg3 = data.readInt();
this.onSeachAvailableFreq(_arg0, _arg1, _arg2, _arg3);
reply.writeNoException();
return true;
}
case TRANSACTION_onSeachFreqStart:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
this.onSeachFreqStart(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_onSeachFreqEnd:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
this.onSeachFreqEnd(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_onSeachFreqFail:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
int _arg1;
_arg1 = data.readInt();
this.onSeachFreqFail(_arg0, _arg1);
reply.writeNoException();
return true;
}
case TRANSACTION_onStChange:
{
data.enforceInterface(DESCRIPTOR);
boolean _arg0;
_arg0 = (0!=data.readInt());
this.onStChange(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_onScanFreqStart:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
this.onScanFreqStart(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_onScanFreqEnd:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
this.onScanFreqEnd(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_onScanFreqFail:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
int _arg1;
_arg1 = data.readInt();
this.onScanFreqFail(_arg0, _arg1);
reply.writeNoException();
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements android.os.IFmListener
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
@Override public void onFreqChanged(int currfreq, int type) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(currfreq);
_data.writeInt(type);
mRemote.transact(Stub.TRANSACTION_onFreqChanged, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void onSeachAvailableFreq(int currentSeachFreq, int count, int[] freqs, int tpye) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(currentSeachFreq);
_data.writeInt(count);
_data.writeIntArray(freqs);
_data.writeInt(tpye);
mRemote.transact(Stub.TRANSACTION_onSeachAvailableFreq, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void onSeachFreqStart(int type) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(type);
mRemote.transact(Stub.TRANSACTION_onSeachFreqStart, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void onSeachFreqEnd(int type) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(type);
mRemote.transact(Stub.TRANSACTION_onSeachFreqEnd, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void onSeachFreqFail(int type, int reason) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(type);
_data.writeInt(reason);
mRemote.transact(Stub.TRANSACTION_onSeachFreqFail, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void onStChange(boolean show) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(((show)?(1):(0)));
mRemote.transact(Stub.TRANSACTION_onStChange, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void onScanFreqStart(int type) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(type);
mRemote.transact(Stub.TRANSACTION_onScanFreqStart, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void onScanFreqEnd(int type) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(type);
mRemote.transact(Stub.TRANSACTION_onScanFreqEnd, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void onScanFreqFail(int type, int reason) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(type);
_data.writeInt(reason);
mRemote.transact(Stub.TRANSACTION_onScanFreqFail, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
}
static final int TRANSACTION_onFreqChanged = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_onSeachAvailableFreq = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_onSeachFreqStart = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
static final int TRANSACTION_onSeachFreqEnd = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
static final int TRANSACTION_onSeachFreqFail = (android.os.IBinder.FIRST_CALL_TRANSACTION + 4);
static final int TRANSACTION_onStChange = (android.os.IBinder.FIRST_CALL_TRANSACTION + 5);
static final int TRANSACTION_onScanFreqStart = (android.os.IBinder.FIRST_CALL_TRANSACTION + 6);
static final int TRANSACTION_onScanFreqEnd = (android.os.IBinder.FIRST_CALL_TRANSACTION + 7);
static final int TRANSACTION_onScanFreqFail = (android.os.IBinder.FIRST_CALL_TRANSACTION + 8);
}
public void onFreqChanged(int currfreq, int type) throws android.os.RemoteException;
public void onSeachAvailableFreq(int currentSeachFreq, int count, int[] freqs, int tpye) throws android.os.RemoteException;
public void onSeachFreqStart(int type) throws android.os.RemoteException;
public void onSeachFreqEnd(int type) throws android.os.RemoteException;
public void onSeachFreqFail(int type, int reason) throws android.os.RemoteException;
public void onStChange(boolean show) throws android.os.RemoteException;
public void onScanFreqStart(int type) throws android.os.RemoteException;
public void onScanFreqEnd(int type) throws android.os.RemoteException;
public void onScanFreqFail(int type, int reason) throws android.os.RemoteException;
}
