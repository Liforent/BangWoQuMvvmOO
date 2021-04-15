/*
 * Copyright (C) 2010 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.zues.ruiyu.bangwoqu.ocr.camera.decode;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.PlanarYUVLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.common.GlobalHistogramBinarizer;
import com.google.zxing.common.HybridBinarizer;
import com.zues.ruiyu.bangwoqu.R;
import com.zues.ruiyu.bangwoqu.base.ZLog;
import com.zues.ruiyu.bangwoqu.module.home.view.BaseScannerActivity;
//import com.zues.ruiyu.bangwoqu.module.home.view.ScannerActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Map;

final class DecodeHandler extends Handler {

    private final BaseScannerActivity mActivity;
    private final MultiFormatReader mMultiFormatReader;
    private final Map<DecodeHintType, Object> mHints;
    private byte[] mRotatedData;
    private static long lastTime = 0;
    //设定

    DecodeHandler(BaseScannerActivity activity) {
        this.mActivity = activity;
        mMultiFormatReader = new MultiFormatReader();
        mHints = new Hashtable<>();
        mHints.put(DecodeHintType.CHARACTER_SET, "utf-8");
        mHints.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);
        Collection<BarcodeFormat> barcodeFormats = new ArrayList<>();
        barcodeFormats.add(BarcodeFormat.CODE_39);
        barcodeFormats.add(BarcodeFormat.CODE_128); // 快递单常用格式39,128
        barcodeFormats.add(BarcodeFormat.QR_CODE); //扫描格式自行添加
        mHints.put(DecodeHintType.POSSIBLE_FORMATS, barcodeFormats);
    }

    @Override
    public void handleMessage(Message message) {
        switch (message.what) {
            case R.id.decode:
                decode((byte[]) message.obj, message.arg1, message.arg2);
                break;
            case R.id.quit:
                Looper looper = Looper.myLooper();
                if (null != looper) {
                    looper.quit();
                }
                break;
        }
    }

    /**
     * Decode the data within the viewfinder rectangle, and time how long it took. For efficiency, reuse the same reader
     * objects from one decode to the next.
     *
     * @param data   The YUV preview frame.
     * @param width  The width of the preview frame.
     * @param height The height of the preview frame.
     */
    private void decode(byte[] data, int width, int height) {
        if (null == mRotatedData) {
            mRotatedData = new byte[width * height];
        } else {
            if (mRotatedData.length < width * height) {
                mRotatedData = new byte[width * height];
            }
        }
        Arrays.fill(mRotatedData, (byte) 0);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (x + y * width >= data.length) {
                    break;
                }
                mRotatedData[x * height + height - y - 1] = data[x + y * width];
            }
        }
        int tmp = width; // Here we are swapping, that's the difference to #11
        width = height;
        height = tmp;

        Result rawResult = null;
        try {
            Rect rect = mActivity.getMyCropRect();
            if (rect == null) {
                return;
            }

            PlanarYUVLuminanceSource source = new PlanarYUVLuminanceSource(mRotatedData, width, height, rect.left, rect.top, rect.width(), rect.height(), false);
            long curTime = System.currentTimeMillis();
            //防止短时间内连续识别
            int INTERVAL_TIME = 500;
            if (curTime - lastTime < INTERVAL_TIME) {
                Message.obtain(mActivity.getCaptureActivityHandler(), R.id.decode_failed).sendToTarget();
                return;
            }
            if (mActivity.isQRCode()) {
                /*
                 HybridBinarizer算法使用了更高级的算法，针对渐变图像更优，也就是准确率高。
                 但使用GlobalHistogramBinarizer识别效率确实比HybridBinarizer要高一些。
                 */
                rawResult = mMultiFormatReader.decode(new BinaryBitmap(new GlobalHistogramBinarizer(source)), mHints);
                if (rawResult == null) {
                    rawResult = mMultiFormatReader.decode(new BinaryBitmap(new HybridBinarizer(source)), mHints);
                }
                // TODO: 2020/12/16 条码过滤方式需要完善
                if (rawResult.getText().length() < 8) {
                    Message.obtain(mActivity.getCaptureActivityHandler(), R.id.decode_failed).sendToTarget();
                    return;
                }
                if (rawResult.getText().contains(",")) {
                    Message.obtain(mActivity.getCaptureActivityHandler(), R.id.decode_failed).sendToTarget();
                    return;
                }
                if (rawResult.getText().contains("/")) {
                    Message.obtain(mActivity.getCaptureActivityHandler(), R.id.decode_failed).sendToTarget();
                    return;
                }
                Bitmap bitmap = source.renderCroppedGreyscaleBitmap();
                Bitmap phoneBitmap = getCropedBitmap(bitmap);
                rawResult.setBitmap(phoneBitmap);
                long startTime = System.currentTimeMillis();
                String result = decodeImage(phoneBitmap);
                long endTime = System.currentTimeMillis();
                double spendTime = (endTime - startTime) * 1.0 / 1000;
                if (result.length() > 10) {
                    rawResult = new Result(rawResult.getText() + "###" + result + " 耗时：" + spendTime, null, null, null);
                    rawResult.setBitmap(phoneBitmap);
                }
                ZLog.e("testResult:" + rawResult.getText() + "totaltime:" + spendTime);


            } else {
                Bitmap bitmap = source.renderCroppedGreyscaleBitmap();
                String result = decodeImage(bitmap);
                if (result.length() > 10) {
                    rawResult = new Result(result, null, null, null);
                    rawResult.setBitmap(bitmap);
                }


//                TessEngine tessEngine = TessEngine.Generate();
//                String result = tessEngine.detectText(bitmap);
//                if(!TextUtils.isEmpty(result)){
//                    rawResult = new Result(result, null, null, null);
//                    rawResult.setBitmap(bitmap);
//                }
            }


        } catch (Exception ignored) {
        } finally {
            mMultiFormatReader.reset();
        }

        Message message;
        if (rawResult != null) {
            message = Message.obtain(mActivity.getCaptureActivityHandler(), R.id.decode_succeeded, rawResult);
            lastTime = System.currentTimeMillis();
        } else {
            message = Message.obtain(mActivity.getCaptureActivityHandler(), R.id.decode_failed);
        }
        message.sendToTarget();
    }

    private void onImageChanged(Bitmap bitmap) {
        if (mActivity.getPredictor().isLoaded()) {
            mActivity.getPredictor().setInputImage(bitmap);
            mActivity.getPredictor().runModel();
        }
    }

    private String decodeImage(Bitmap bitmap) {
        if (mActivity.getPredictor().isLoaded()) {
            mActivity.getPredictor().setInputImage(bitmap);
            mActivity.getPredictor().runModel();
        }
        return mActivity.getPredictor().outputResult();
    }

    private Bitmap getCropedBitmap(Bitmap origin) {
        if (origin == null) {
            return null;
        }
        Bitmap newBM = Bitmap.createBitmap(origin, 0, origin.getHeight() / 3, origin.getWidth(), origin.getHeight() / 3);
        if (!origin.isRecycled()) {//这时候origin还有吗？
            origin.recycle();
        }
        return newBM;
    }


}
