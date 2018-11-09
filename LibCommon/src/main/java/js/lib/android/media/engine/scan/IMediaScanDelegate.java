/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: E:\\WorkSpace\\Code\\Studio\\TrMedia2.0\\LibCommon\\src\\main\\aidl\\js\\lib\\android\\media\\engine\\scan\\IMediaScanDelegate.aidl
 */
package js.lib.android.media.engine.scan;
// Declare any non-default types here with import statements

public interface IMediaScanDelegate extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements IMediaScanDelegate
{
private static final String DESCRIPTOR = "js.lib.android.media.engine.scan.IMediaScanDelegate";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an js.lib.android.media.engine.scan.IMediaScanDelegate interface,
 * generating a proxy if needed.
 */
public static IMediaScanDelegate asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof IMediaScanDelegate))) {
return ((IMediaScanDelegate)iin);
}
return new Proxy(obj);
}
@Override public android.os.IBinder asBinder()
{
return this;
}
@Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
{
String descriptor = DESCRIPTOR;
switch (code)
{
case INTERFACE_TRANSACTION:
{
reply.writeString(descriptor);
return true;
}
case TRANSACTION_onMediaScanningStart:
{
data.enforceInterface(descriptor);
this.onMediaScanningStart();
reply.writeNoException();
return true;
}
case TRANSACTION_onMediaScanningEnd:
{
data.enforceInterface(descriptor);
this.onMediaScanningEnd();
reply.writeNoException();
return true;
}
case TRANSACTION_onMediaScanningCancel:
{
data.enforceInterface(descriptor);
this.onMediaScanningCancel();
reply.writeNoException();
return true;
}
default:
{
return super.onTransact(code, data, reply, flags);
}
}
}
private static class Proxy implements IMediaScanDelegate
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
public String getInterfaceDescriptor()
{
return DESCRIPTOR;
}
@Override public void onMediaScanningStart() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_onMediaScanningStart, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void onMediaScanningEnd() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_onMediaScanningEnd, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void onMediaScanningCancel() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_onMediaScanningCancel, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
}
static final int TRANSACTION_onMediaScanningStart = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_onMediaScanningEnd = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_onMediaScanningCancel = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
}
public void onMediaScanningStart() throws android.os.RemoteException;
public void onMediaScanningEnd() throws android.os.RemoteException;
public void onMediaScanningCancel() throws android.os.RemoteException;
}
