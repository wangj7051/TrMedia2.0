#
# Copyright (C) 2008 The Android Open Source Project
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

#### Start ####
LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE := Player_SCL_LC2010_VDC_TEMP
LOCAL_CLASS := APPS

## user: ָ��ģ��ֻ��user�汾�²ű���
## eng: ָ��ģ��ֻ��eng�汾�²ű���
## tests: ָ��ģ��ֻ��tests�汾�²ű���
## optional:ָ��ģ�������а汾�¶�����
LOCAL_MODULE_TAGS := optional

LOCAL_BUILT_MODULE_STEM := package.apk
LOCAL_MODULE_SUFFIX := $(COMMON_ANDROID_PACKAGE_SUFFIX)

#### ǩ�� ####
## ^^ ʹ�ó��� ^^
## (1)	ϵͳ������ʹ��android.uid.system��Ϊ����UID��APK��
##  	����������manifest�ڵ�������android:sharedUserId="android.uid.system"��
##  	Ȼ����Android.mk������LOCAL_CERTIFICATE := platform
## 		���Բμ�Settings��
## (2)	ϵͳ������ʹ��android.uid.shared��Ϊ����UID��APK��
## 		������manifest�ڵ�������android:sharedUserId="android.uid.shared"��
## 		Ȼ����Android.mk������LOCAL_CERTIFICATE := shared��
## 		���Բμ�Launcher��
## (3)	ϵͳ������ʹ��android.uid.shared��Ϊ����UID��APK��
## 		������manifest�ڵ�������android:sharedUserId="android.uid.shared"��
## 		Ȼ����Android.mk������LOCAL_CERTIFICATE := shared��
## 		���Բμ�Launcher��
## (4)	ϵͳ������ʹ��android.media��Ϊ����UID��APK��
## 		������manifest�ڵ�������android:sharedUserId="android.media"��
## 		Ȼ����Android.mk������LOCAL_CERTIFICATE := media��
## 		���Բμ�Gallery�ȡ�
## ^^ ȡֵ��Χ ^^
## (1)	testkey: ��ͨAPK��Ĭ�������ʹ��
## (2)	platform: ��APK���һЩϵͳ�ĺ��Ĺ��ܡ�������ϵͳ�д��ڵ��ļ��еķ��ʲ��ԣ����ַ�ʽ���������APK���ڽ��̵�UIDΪsystem��
## (3)	shared: ��APK��Ҫ��home/contacts���̹������ݡ�
## (4)	media: ��APK��media/downloadϵͳ�е�һ����
## (5)	presigned: ��ʾAPKǩ��ʹ��ԭ��ǩ��
LOCAL_CERTIFICATE := presigned

LOCAL_SRC_FILES := PlayerBase-release.apk
LOCAL_MODULE_PATH := $(PRODUCT_OUT)/system/priv-app

#use the folling include to make apk
include $(call all-makefiles-under,$(LOCAL_PATH))
