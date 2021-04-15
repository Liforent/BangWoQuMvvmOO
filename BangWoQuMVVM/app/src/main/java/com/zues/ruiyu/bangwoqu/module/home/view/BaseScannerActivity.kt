package com.zues.ruiyu.bangwoqu.module.home.view

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Rect
import android.hardware.Camera
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.*
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.zxing.Result
import com.zues.ruiyu.bangwoqu.R
import com.zues.ruiyu.bangwoqu.base.Constant
import com.zues.ruiyu.bangwoqu.base.ZLog
import com.zues.ruiyu.bangwoqu.base.eventbus.BangwqMessageEvent
import com.zues.ruiyu.bangwoqu.base.mvvm.BaseLifeCycleActivity
import com.zues.ruiyu.bangwoqu.module.home.HomeViewModel
import com.zues.ruiyu.bangwoqu.ocr.Predictor
import com.zues.ruiyu.bangwoqu.ocr.camera.CameraManager
import com.zues.ruiyu.bangwoqu.ocr.camera.decode.CaptureActivityHandler
import com.zues.ruiyu.bangwoqu.ocr.camera.decode.DecodeManager
import com.zues.ruiyu.bangwoqu.ocr.camera.decode.InactivityTimer
import com.zues.ruiyu.bangwoqu.ocr.camera.tess.TesseractCallback
import com.zues.ruiyu.bangwoqu.ocr.camera.tess.TesseractThread
import com.zues.ruiyu.bangwoqu.ocr.camera.utils.Tools
import com.zues.ruiyu.bangwoqu.ocr.camera.view.ScannerFinderView
import kotlinx.android.synthetic.main.activity_scanner.*
import kotlinx.android.synthetic.main.activity_scanner.qr_code_view_finder
import kotlinx.android.synthetic.main.activity_scanner_out.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.anko.toast
import java.io.IOException

/**
 *@Author liforent
 *@create 2020/11/23 13:28
 *
 * 所有ocr Activity基类。
 * 1.子类需要重写如下方法：
 *  initScannerView()
 *   initScannerEvent()
 *    handleDeCode()
 *
 */
abstract class BaseScannerActivity : BaseLifeCycleActivity<HomeViewModel>(), SurfaceHolder.Callback,
    Camera.PictureCallback, Camera.ShutterCallback {
    private var mSurfaceView: SurfaceView? = null
    override fun initData() = false

    var isQRCode = true //扫条码还是手机号码
    val mDecodeManager = DecodeManager()
    open var mQrCodeViewFinder: ScannerFinderView? = null
    open var mQRCodeViewStub: ViewStub? = null
    private var mHasSurface = false
   // var mInactivityTimer: InactivityTimer? = null

    val predictor by lazy {
        Predictor()
    }

    val mContext by lazy {
        this
    }
    private var mCaptureActivityHandler: CaptureActivityHandler? = null
    val captureActivityHandler: CaptureActivityHandler?
        get() = mCaptureActivityHandler

    fun getMyCropRect(): Rect? {
        return mQrCodeViewFinder?.rect
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 100) {
            var permissionGranted = true
            for (i in grantResults) {
                if (i != PackageManager.PERMISSION_GRANTED) {
                    permissionGranted = false
                }
            }
            if (permissionGranted) {
                initScannerView()
                initScannerEvent()
            } else {
                // 无权限退出
                finish()
            }
        }
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        super.onCreate(savedInstanceState)
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE),
                100
            )
        } else {
            initScannerView()
            initScannerEvent()
        }
    }

    open fun initScannerView() {
        CameraManager.init()
        initCamera(mQRCodeViewStub)
        //mInactivityTimer = InactivityTimer(this)
        mHasSurface = false
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}
    override fun surfaceCreated(holder: SurfaceHolder) {
        if (!mHasSurface) {
            mHasSurface = true
            mSurfaceView?.postDelayed({ initCamera(holder) }, 50)
        }
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        mHasSurface = false
    }

    private fun initCamera(surfaceHolder: SurfaceHolder) {
        try {
            if (!CameraManager.get().openDriver(surfaceHolder)) {
                return
            }
        } catch (e: IOException) {
            // 基本不会出现相机不存在的情况
            Toast.makeText(this, getString(R.string.camera_not_found), Toast.LENGTH_SHORT).show()
            finish()
            return
        } catch (re: RuntimeException) {
            re.printStackTrace()
            return
        }
        // qr_code_view_finder.visibility = View.VISIBLE
        //findViewById<View>(R.id.qr_code_view_background).visibility = View.GONE
        if (mCaptureActivityHandler == null) {
            mCaptureActivityHandler = CaptureActivityHandler(this@BaseScannerActivity)
        }
    }


    open fun initScannerEvent() {}

    override fun onResume() {
        super.onResume()
        CameraManager.init()
        initCamera(mQRCodeViewStub)
        initModel()
    }

    override fun onPause() {
        if (mCaptureActivityHandler != null) {
            try {
                mCaptureActivityHandler!!.quitSynchronously()
                mCaptureActivityHandler = null
                if (null != mSurfaceView && !mHasSurface) {
                    mSurfaceView!!.holder.removeCallback(this)
                }
                CameraManager.get().closeDriver()
            } catch (e: Exception) {
                // 关闭摄像头失败的情况下,最好退出该Activity,否则下次初始化的时候会显示摄像头已占用.
            }
        }
        super.onPause()
        predictor.releaseModel()
    }

    open fun initCamera(viewStub: ViewStub?) {
        if (null == mSurfaceView) {
            viewStub?.layoutResource = R.layout.layout_surface_view
            mSurfaceView = viewStub?.inflate() as SurfaceView?
        }
        val surfaceHolder = mSurfaceView?.holder
        if (mHasSurface) {
            if (surfaceHolder != null) {
                initCamera(surfaceHolder)
            }
        } else {
            surfaceHolder?.addCallback(this)
        }
    }

    fun restartPreview() {
        try {
            mCaptureActivityHandler?.restartPreviewAndDecode()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun stopPreview() {
        try {
            mCaptureActivityHandler?.onPause()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Handler scan result
     *
     * @param result
     */
    open fun handleDecode(result: Result?) {
       // mInactivityTimer?.onActivity()
//        if (null == result) {
//            mDecodeManager.showCouldNotReadQrCodeFromScanner(
//                this
//            ) { restartPreview() }
//        } else {
//            successSound.start()
//            handleResult(result)
//        }

    }


    //初始化model
    private fun initModel() {
        try {
            if (onLoadModel()) {
                predictor.runModel()
            } else {
                toast("启动数字识别模型失败！")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            toast("启动数字识别模型失败，请重试！")
        }


    }


    private fun onLoadModel(): Boolean {
        val modelPath = "models"
        val labelPath = "labels/ppocr_keys_v1.txt"
        var imagePath = "images/5.jpg"
        val cpuThreadNum = 4
        val cpuPowerMode = "LITE_POWER_HIGH"
        val inputColorFormat = "BGR"
        val inputShape = longArrayOf(1, 3, 960)
        val inputMean = floatArrayOf(0.485f, 0.456f, 0.406f)
        val inputStd = floatArrayOf(0.229f, 0.224f, 0.225f)
        val scoreThreshold: Float = 0.1f
        return predictor.init(
            this@BaseScannerActivity, modelPath, labelPath, cpuThreadNum,
            cpuPowerMode,
            inputColorFormat,
            inputShape, inputMean,
            inputStd, scoreThreshold
        )
    }

    override fun onPictureTaken(data: ByteArray?, camera: Camera?) {
//        mCaptureActivityHandler?.onPause()
//        bmp = null
//        bmp = Tools.getFocusedBitmap(this, camera, data, cropRect)
//        val mTesseractThread = TesseractThread(bmp, object : TesseractCallback {
//            override fun succeed(result: String) {
//                val message = Message.obtain()
//                message.what = 0
//                message.obj = result
//                mHandler.sendMessage(message)
//            }
//
//            override fun fail() {
//                val message = Message.obtain()
//                message.what = 1
//                mHandler.sendMessage(message)
//            }
//        })
//        val thread = Thread(mTesseractThread)
//        thread.start()
    }


    override fun onShutter() {

    }

    override fun onBackPressed() {
        super.onBackPressed()
        ZLog.e("onBakcPressed")
        quit()
    }

    open fun quit() {
        finish()
    }

    override fun onDestroy() {
        //mInactivityTimer?.shutdown()
        super.onDestroy()
    }

    fun getScanMode(): Int? {
        return mQrCodeViewFinder?.scanMode
    }

    fun restartPreviewDelayed() {
        Handler(Looper.getMainLooper()).postDelayed({
           restartPreview()
        }, 500)

    }
}