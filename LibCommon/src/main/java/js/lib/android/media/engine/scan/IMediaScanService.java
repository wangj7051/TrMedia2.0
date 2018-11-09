/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: E:\\WorkSpace\\Code\\Studio\\TrMedia2.0\\LibCommon\\src\\main\\aidl\\js\\lib\\android\\media\\engine\\scan\\IMediaScanService.aidl
 */
package js.lib.android.media.engine.scan;
// Declare any non-default types here with import statements

public interface IMediaScanService extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements IMediaScanService
{
private static final String DESCRIPTOR = "js.lib.android.media.engine.scan.IMediaScanService";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an js.lib.android.media.engine.scan.IMediaScanService interface,
 * generating a proxy if needed.
 */
public static IMediaScanService asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof IMediaScanService))) {
return ((IMediaScanService)iin);
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
case TRANSACTION_startScan:
{
data.enforceInterface(descriptor);
this.startScan();
reply.writeNoException();
return true;
}
case TRANSACTION_destroy:
{
data.enforceInterface(descriptor);
this.destroy();
reply.writeNoException();
return true;
}
case TRANSACTION_isMediaScanning:
{
data.enforceInterface(descriptor);
boolean _result = this.isMediaScanning();
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_registerDelegate:
{
data.enforceInterface(descriptor);
IMediaScanDelegate _arg0;
_arg0 = IMediaScanDelegate.Stub.asInterface(data.readStrongBinder());
this.registerDelegate(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_unregisterDelegate:
{
data.enforceInterface(descriptor);
IMediaScanDelegate _arg0;
_arg0 = IMediaScanDelegate.Stub.asInterface(data.readStrongBinder());
this.unregisterDelegate(_arg0);
reply.writeNoException();
return true;
}
default:
{
return super.onTransact(code, data, reply, flags);
}
}
}
private static class Proxy implements IMediaScanService
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
@Override public void startScan() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_startScan, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void destroy() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_destroy, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public boolean isMediaScanning() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_isMediaScanning, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public void registerDelegate(IMediaScanDelegate delegate) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeStrongBinder((((delegate!=null))?(delegate.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_registerDelegate, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void unregisterDelegate(IMediaScanDelegate delegate) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeStrongBinder((((delegate!=null))?(delegate.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_unregisterDelegate, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
}
static final int TRANSACTION_startScan = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_destroy = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_isMediaScanning = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
static final int TRANSACTION_registerDelegate = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
static final int TRANSACTION_unregisterDelegate = (android.os.IBinder.FIRST_CALL_TRANSACTION + 4);
}
public void startScan() throws android.os.RemoteException;
public void destroy() throws android.os.RemoteException;
public boolean isMediaScanning() throws android.os.RemoteException;
public void registerDelegate(IMediaScanDelegate delegate) throws android.os.RemoteException;
public void unregisterDelegate(IMediaScanDelegate delegate) throws android.os.RemoteException;
}
