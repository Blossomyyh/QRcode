package com.example.eqrcode.utils

import android.content.Context
import android.graphics.ImageFormat
import android.graphics.SurfaceTexture
import android.hardware.camera2.*
import android.media.Image
import android.media.ImageReader
import android.os.HandlerThread
import android.os.Handler
import android.util.Log
import android.util.Size
import android.view.Surface
import android.view.TextureView
import android.view.View
import android.widget.Toast
import com.google.android.gms.vision.barcode.BarcodeDetector
import com.example.eqrcode.R
import com.google.android.gms.vision.Frame
import java.util.*


class Camera
/**
 * to set up the camera
 */
(private val view: View, context: Context) {
    //var： var是一个可变变量，这是一个可以通过重新分配来更改为另一个值的变量。这种声明变量的方式和Java中声明变量的方式一样。
    //val: val是一个只读变量，这种声明变量的方式相当于java中的final变量。一个val创建的时候必须初始化，因为以后不能被改变。
    private val cameraId: String? = null
    private val imageDimension: Size? = null
    protected var cameraCaptureSession: CameraCaptureSession? = null
    protected var captureRequestBuilder: CaptureRequest.Builder? = null
    protected var cameraDevice : CameraDevice? = null
    private val context: Context? = null
    private var handlerBackground: Handler? = null
    private val handlerThread: HandlerThread? = null
    private var textureView: TextureView? = null
    private var barcodeDetector: BarcodeDetector? = null
    private var captureResult: CaptureResult? = null
    private var cameraCharacteristics: CameraCharacteristics? = null
    private var imageReader: ImageReader? = null

    private var mImageAvailableListener = ImageReader.OnImageAvailableListener{
        reader: ImageReader? ->
        var image: Image? = reader?.acquireNextImage() ?: return@OnImageAvailableListener
        barcodeDetector!!.receiveFrame(Frame.Builder().setBitmap(this.textureView?.bitmap).build())
        image?.close()
    }


    public val cameraCaptureSessionListener = object : CameraCaptureSession.CaptureCallback() {
        override fun onCaptureCompleted(session: CameraCaptureSession, request: CaptureRequest, result: TotalCaptureResult) {
            super.onCaptureCompleted(session, request, result)
        }
    }

    private val cameraStateCallcack  = object : CameraDevice.StateCallback(){
        override fun onDisconnected(camera: CameraDevice) {
            cameraDevice?.close()
        }

        override fun onError(camera: CameraDevice, error: Int) {
            cameraDevice?.close()
            cameraDevice = null
        }

        override fun onOpened(camera: CameraDevice) {
            //when camera is open : called
            Log.e(TAG, "camera onopen")
            cameraDevice = camera
            createCameraPreview()
        }
    }

    init {
        this.textureView = view.findViewById(R.id.textureView)
    }

    fun setUpDetector(barcodeDetector: BarcodeDetector) {
        this.barcodeDetector = barcodeDetector
    }


    fun createCameraPreview() {
        try {
            val texture: SurfaceTexture = textureView!!.surfaceTexture
            if (texture != null){
                //Set the default size of the image buffers.
                texture.setDefaultBufferSize(imageDimension!!.width/4, imageDimension!!.width/4)

                val surface: Surface = Surface(texture)
//                cameraDevice?.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
                captureRequestBuilder = cameraDevice?.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)

                imageReader = ImageReader.newInstance(imageDimension?.width/4, imageDimension?.width/4, ImageFormat.YUV_420_888,1)
                imageReader?.setOnImageAvailableListener(mImageAvailableListener, handlerBackground)

                captureRequestBuilder?.addTarget(surface)
                captureRequestBuilder?.addTarget(imageReader?.surface)

                cameraDevice!!.createCaptureSession(Arrays.asList(surface, imageReader!!.surface), object : CameraCaptureSession.StateCallback() {
                    override fun onConfigured(session: CameraCaptureSession) {
                        if (null == cameraDevice){return}
                    }

                    override fun onConfigureFailed(session: CameraCaptureSession) {
                        Toast.makeText(context, "configuration change", Toast.LENGTH_SHORT).show()
                    }
                },null)

            }

        }catch (e: CameraAccessException){
            e.printStackTrace()
        }
    }




    companion object {
        private val TAG = "BARCODE"
        private val REQUEST_CAMERA_PERMISSION = 200
    }



}
