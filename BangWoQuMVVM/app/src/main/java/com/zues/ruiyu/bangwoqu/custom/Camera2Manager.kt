package com.zues.ruiyu.bangwoqu.custom

import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CameraDevice
import android.view.SurfaceHolder
import com.zues.ruiyu.bangwoqu.module.home.view.BaseScannerActivity

/**
 *@Author liforent
 *@create 2020/12/17 14:05
 */
class Camera2Manager(var activity:BaseScannerActivity) {
    var m2CameraManager: android.hardware.camera2.CameraManager? = null
    var m2CameraDevice: CameraDevice? = null
    var m2CameraId: String? = null
    var m2CameraCaptureSession: CameraCaptureSession? = null
    var mSurfaceHolder: SurfaceHolder? = null

    companion object {
       // var sCamera2Manager = Camera2Manager().init()
    }

    fun init(): Camera2Manager {
        return this
    }

    public fun openDriver() {

    }

    public fun startPreview(){

    }
}