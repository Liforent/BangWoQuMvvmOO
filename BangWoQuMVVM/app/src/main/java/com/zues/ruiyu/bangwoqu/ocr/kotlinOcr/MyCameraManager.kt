package com.zues.ruiyu.bangwoqu.ocr.kotlinOcr

import android.app.Activity
import android.content.Context
import android.hardware.Camera
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import com.zues.ruiyu.bangwoqu.base.ZLog

/**
 *@Author liforent
 *@create 2020/12/1 10:04
 */
class MyCameraManager(var activity:Activity) {

    private var mCameraManager = activity.getSystemService(Context.CAMERA_SERVICE) as CameraManager

    fun openCameraDriver(){

    }

    fun getCameraID(){
        var mCameraList = mCameraManager.cameraIdList
        for( i in mCameraList.indices){
            var cameraCharacteristics = mCameraManager.getCameraCharacteristics(mCameraList[i])
            var fields =
            ZLog.e(mCameraList[i].toString())
        }
    }


}