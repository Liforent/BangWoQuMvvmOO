package com.zues.ruiyu.bangwoqu.module.home.view

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
import android.view.SurfaceHolder
import android.view.SurfaceView
import androidx.core.app.ActivityCompat
import com.zues.ruiyu.bangwoqu.R
import com.zues.ruiyu.bangwoqu.base.mvvm.BaseLifeCycleActivity
import com.zues.ruiyu.bangwoqu.module.home.HomeViewModel
import com.zues.ruiyu.bangwoqu.ocr.camera.decode.CaptureActivityHandler
import kotlinx.android.synthetic.main.activity_scanner_out.*
import org.jetbrains.anko.toast

class TestCamera2 : BaseLifeCycleActivity<HomeViewModel>(), SurfaceHolder.Callback {

    private var mCameraManager: CameraManager? = null
    private var mCameraDevice: CameraDevice? = null
    private var mSurfaceView: SurfaceView? = null
    private var mCaptureActivityHandler: CaptureActivityHandler? = null
    val captureActivityHandler: CaptureActivityHandler?
        get() = mCaptureActivityHandler

    private var mStateCallback = object : CameraDevice.StateCallback() {
        override fun onOpened(cameraDevice: CameraDevice) {
            mCameraDevice = cameraDevice
            takePreview();
        }

        override fun onDisconnected(p0: CameraDevice) {
            // 关闭摄像头
            if (null != mCameraDevice) {
                // 关闭摄像头
                mCameraDevice?.close();
                mCameraDevice = null;
            }
        }

        override fun onError(p0: CameraDevice, p1: Int) {
            toast("开启摄像头失败")
        }

    }

    private fun takePreview() {

    }

    override fun surfaceCreated(p0: SurfaceHolder?) {
        initCame()
    }

    private fun initCame() {
        val mCameraId = CameraCharacteristics.LENS_FACING_FRONT.toString()
        mCameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        mCameraManager?.openCamera(mCameraId, mStateCallback, mCaptureActivityHandler)
    }

    override fun surfaceChanged(p0: SurfaceHolder?, p1: Int, p2: Int, p3: Int) {

    }

    override fun surfaceDestroyed(p0: SurfaceHolder?) {
        mCameraDevice?.close()
        mCameraDevice = null
    }

    override fun initData(): Boolean = false

    override fun initDataObserver() {

    }

    override fun getLayoutId(): Int = R.layout.activity_scanner_out

    override fun initView() {
        super.initView()
        if (null == mSurfaceView) {
            qr_code_view_stub.layoutResource = R.layout.layout_surface_view
            mSurfaceView = qr_code_view_stub.inflate() as SurfaceView?
        }
        val surfaceHolder = mSurfaceView?.holder
        //initCamera(surfaceHolder)

        mCaptureActivityHandler = CaptureActivityHandler(this@BaseScannerActivity)
        surfaceHolder?.addCallback(this)
    }


}