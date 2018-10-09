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

#### 指定编译所用SDK  ####
TARGET_PLATFORM=android-22

LOCAL_MODULE := Player_SCL_LC2010_VDC_TEMP
LOCAL_MODULE_CLASS := APPS

## user: 指该模块只在user版本下才编译
## eng: 指该模块只在eng版本下才编译
## tests: 指该模块只在tests版本下才编译
## optional:指该模块在所有版本下都编译
LOCAL_MODULE_TAGS := optional

LOCAL_BUILT_MODULE_STEM := package.apk
LOCAL_MODULE_SUFFIX := $(COMMON_ANDROID_PACKAGE_SUFFIX)

#### 签名 ####
## ^^ 使用场景 ^^
## (1)	系统中所有使用android.uid.system作为共享UID的APK，
##  	都会首先在manifest节点中增加android:sharedUserId="android.uid.system"，
##  	然后在Android.mk中增加LOCAL_CERTIFICATE := platform
## 		可以参见Settings等
## (2)	系统中所有使用android.uid.shared作为共享UID的APK，
## 		都会在manifest节点中增加android:sharedUserId="android.uid.shared"，
## 		然后在Android.mk中增加LOCAL_CERTIFICATE := shared。
## 		可以参见Launcher等
## (3)	系统中所有使用android.uid.shared作为共享UID的APK，
## 		都会在manifest节点中增加android:sharedUserId="android.uid.shared"，
## 		然后在Android.mk中增加LOCAL_CERTIFICATE := shared。
## 		可以参见Launcher等
## (4)	系统中所有使用android.media作为共享UID的APK，
## 		都会在manifest节点中增加android:sharedUserId="android.media"，
## 		然后在Android.mk中增加LOCAL_CERTIFICATE := media。
## 		可以参见Gallery等。
## ^^ 取值范围 ^^
## (1)	testkey: 普通APK，默认情况下使用
## (2)	platform: 该APK完成一些系统的核心功能。经过对系统中存在的文件夹的访问测试，这种方式编译出来的APK所在进程的UID为system。
## (3)	shared: 该APK需要和home/contacts进程共享数据。
## (4)	media: 该APK是media/download系统中的一环。
## (5)	presigned - Use apk`s origin signature.
LOCAL_CERTIFICATE := PRESIGNED

LOCAL_SRC_FILES := PlayerBase-release.apk
LOCAL_MODULE_PATH := $(PRODUCT_OUT)/system/priv-app

#use the folling include to make apk
include $(BUILD_PREBUILT)