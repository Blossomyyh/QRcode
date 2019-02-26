package com.example.eqrcode.utils

import android.app.Activity
import android.content.Context
import android.graphics.ImageFormat
import android.graphics.SurfaceTexture
import android.hardware.camera2.*
import android.hardware.camera2.params.StreamConfigurationMap
import android.media.Image
import android.media.ImageReader
import android.os.HandlerThread
import android.os.Handler
import android.support.v4.app.ActivityCompat
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
import android.Manifest
import android.content.pm.PackageManager


class Camera
/**
 * to set up the camera
 */
(private val view: View, var context: Context) {
    //var： var是一个可变变量，这是一个可以通过重新分配来更改为另一个值的变量。这种声明变量的方式和Java中声明变量的方式一样。
    //val: val是一个只读变量，这种声明变量的方式相当于java中的final变量。一个val创建的时候必须初始化，因为以后不能被改变。
    private var cameraId: String? = null
    private var imageDimension: Size? = null
    protected var cameraCaptureSession: CameraCaptureSession? = null
    protected var captureRequestBuilder: CaptureRequest.Builder? = null
    protected var cameraDevice: CameraDevice? = null
    private var handlerBackground: Handler? = null
    private var handlerThread: HandlerThread? = null
    private var textureView: TextureView? = null
    private var barcodeDetector: BarcodeDetector? = null
    private var captureResult: CaptureResult? = null
    private var cameraCharacteristics: CameraCharacteristics? = null
    private var imageReader: ImageReader? = null

    private val aInt by lazy { 10 }

    private val mImageAvailableListener = ImageReader.OnImageAvailableListener { reader ->
        val mImage = reader.acquireNextImage() ?: return@OnImageAvailableListener

        Log.d(TAG, Thread.currentThread().toString())
        barcodeDetector?.receiveFrame(Frame.Builder().setBitmap(textureView?.bitmap).build())
        mImage.close()
    }

    val cameraCaptureSessionListener = object : CameraCaptureSession.CaptureCallback() {
        override fun onCaptureCompleted(session: CameraCaptureSession, request: CaptureRequest, result: TotalCaptureResult) {
            super.onCaptureCompleted(session, request, result)
        }
    }

    private val cameraStateCallcack = object : CameraDevice.StateCallback() {
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
            if (texture != null) {
                //Set the default size of the image buffers.
                texture.setDefaultBufferSize(imageDimension!!.width / 4, imageDimension!!.width / 4)

                val surface: Surface = Surface(texture)
                cameraDevice?.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
                captureRequestBuilder = cameraDevice?.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)

                imageReader = ImageReader.newInstance(imageDimension!!.width / 4, imageDimension!!.width / 4, ImageFormat.YUV_420_888, 1)
                imageReader?.setOnImageAvailableListener(mImageAvailableListener, handlerBackground)

                captureRequestBuilder?.addTarget(surface)
                captureRequestBuilder?.addTarget(imageReader!!.surface)

                cameraDevice!!.createCaptureSession(Arrays.asList(surface, imageReader!!.surface), object : CameraCaptureSession.StateCallback() {
                    override fun onConfigured(session: CameraCaptureSession) {
                        if (null == cameraDevice) {
                            return
                        }
                        cameraCaptureSession = session
                        updatePreview(cameraCaptureSessionListener)
                    }

                    override fun onConfigureFailed(session: CameraCaptureSession) {
                        Toast.makeText(context, "configuration change", Toast.LENGTH_SHORT).show()
                    }
                }, null)

            }

        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }


    fun openCamera(context: Activity) {
        this.context = context
        var manager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        Log.e(TAG, "Camera is open")
        try {
            cameraId = manager.cameraIdList[0]

            /**
             * <p>An instance of this object is available from {@link CameraCharacteristics} using
             * the {@link CameraCharacteristics#SCALER_STREAM_CONFIGURATION_MAP} key and the
             * {@link CameraCharacteristics#get} method.</p>
             *
             * <p>This also contains the minimum frame durations and stall durations for each format/size
             * combination that can be used to calculate effective frame rate when submitting multiple captures.
             * </p>
             *
             * <pre><code>{@code
             * CameraCharacteristics characteristics = cameraManager.getCameraCharacteristics(cameraId);
             * StreamConfigurationMap configs = characteristics.get(
             *         CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
             * }</code></pre>
             * */

            var cameraCharacteristics = manager.getCameraCharacteristics(cameraId)
            var configs = cameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP) as StreamConfigurationMap
            imageDimension = configs.getOutputSizes(SurfaceTexture::class.java)[0]

            // Add permission for camera and let user grant the permission
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(context, arrayOf(Manifest.permission.CAMERA), REQUEST_CAMERA_PERMISSION)
                return
            }

            manager.openCamera(cameraId, cameraStateCallcack, null)
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
        Log.e(TAG, "openCamera")

    }

    fun updatePreview(listener: CameraCaptureSession.CaptureCallback) {
        if (null == cameraDevice) {
            Log.e(TAG, "updatePreview error, return")
        }
        captureRequestBuilder!!.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO)

        try {
            cameraCaptureSession!!.setRepeatingRequest(captureRequestBuilder!!.build(), listener, handlerBackground)
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

    fun closeCamera() {
        if (null != cameraDevice) {
            cameraDevice?.close()
            cameraDevice = null

        }

        if (null != imageReader) {
            imageReader?.close()
            imageReader = null
        }
    }


    fun startBackgroundThread() {
        handlerThread = HandlerThread("Camera Background")
        handlerThread!!.start()
        handlerBackground = Handler(handlerThread!!.looper)

    }

    fun stopBackgroundThread() {
        handlerThread!!.quitSafely()
        try {
            handlerThread!!.join()
            handlerThread = null
            handlerBackground = null
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

    companion object {
        private val TAG = "BARCODE"
        private val REQUEST_CAMERA_PERMISSION = 200
    }


}
