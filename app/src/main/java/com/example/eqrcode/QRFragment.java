package com.example.eqrcode;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;

import com.example.eqrcode.utils.AutoFitTextureView;

public class QRFragment extends Fragment implements ActivityCompat.OnRequestPermissionsResultCallback {

    private QRActivity mQRActivity;
    /**
     * Conversion from screen rotation to JPEG orientation.
     */
    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();
    private static final int REQUEST_CAMERA_PERMISSION = 1;
    private static final String FRAGMENT_DIALOG = "dialog";

    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    /**
     * Tag for the {@link Log}.
     */
    private static final String TAG = "QRFragment";

    /**
     * Camera state: Showing camera preview.
     */
    private static final int STATE_PREVIEW = 0;

    /**
     * Camera state: Waiting for the focus to be locked.
     */
    private static final int STATE_WAITING_LOCK = 1;

    /**
     * Camera state: Waiting for the exposure to be precapture state.
     */
    private static final int STATE_WAITING_PRECAPTURE = 2;

    /**
     * Camera state: Waiting for the exposure state to be something other than precapture.
     */
    private static final int STATE_WAITING_NON_PRECAPTURE = 3;

    /**
     * Camera state: Picture was taken.
     */
    private static final int STATE_PICTURE_TAKEN = 4;

    /**
     * Max preview width that is guaranteed by Camera2 API
     */
    private static final int MAX_PREVIEW_WIDTH = 1920;

    /**
     * Max preview height that is guaranteed by Camera2 API
     */
    private static final int MAX_PREVIEW_HEIGHT = 1080;


    /**
     * ID of the current {@link CameraDevice}.
     */
    private String mCameraId;

    /**
     * An {@link AutoFitTextureView} for camera preview.
     */
    private AutoFitTextureView mTextureView;

    /**
     * A {@link CameraCaptureSession } for camera preview.
     */
    private CameraCaptureSession mCaptureSession;

    /**
     * A reference to the opened {@link CameraDevice}.
     */
    private CameraDevice mCameraDevice;

    /**
     * The {@link Size} of camera preview.
     */
    private Size mPreviewSize;

    /**
     * {@link TextureView.SurfaceTextureListener} handles several lifecycle events on a
     * {@link TextureView}.
     *
     * <p>A TextureView can be used to display a content stream. Such a content
     * stream can for instance be a video or an OpenGL scene. The content stream
     * can come from the application's process as well as a remote process.</p>
     *
     * <p>TextureView can only be used in a hardware accelerated window. When
     * rendered in software, TextureView will draw nothing.</p>
     *
     * <p>Unlike {@link SurfaceView}, TextureView does not create a separate
     * window but behaves as a regular View. This key difference allows a
     * TextureView to be moved, transformed, animated, etc. For instance, you
     * can make a TextureView semi-translucent by calling
     * <code>myView.setAlpha(0.5f)</code>.</p>
     *
     * <p>Using a TextureView is simple: all you need to do is get its
     * {@link SurfaceTexture}. The {@link SurfaceTexture} can then be used to
     * render content. The following example demonstrates how to render the
     * camera preview into a TextureView:</p>
     *
     *
     * <p>A TextureView's SurfaceTexture can be obtained either by invoking
     * {@link #getSurfaceTexture()} or by using a {@link TextureView.SurfaceTextureListener}.
     * It is important to know that a SurfaceTexture is available only after the
     * TextureView is attached to a window (and {@link #onAttachedToWindow()} has
     * been invoked.) It is therefore highly recommended you use a listener to
     * be notified when the SurfaceTexture becomes available.</p>
     *
     * <p>It is important to note that only one producer can use the TextureView.
     * For instance, if you use a TextureView to display the camera preview, you
     * cannot use {@link #lockCanvas()} to draw onto the TextureView at the same
     * time.</p>
     *
     * @see SurfaceView
     * @see SurfaceTexture
     */

    private final TextureView.SurfaceTextureListener mSurfaceTestureListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            openCamera
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {

        }
    }

    /**
     *
     * QRFragment initiate
     *
     * */

    public static QRFragment newInstance() {

        Bundle args = new Bundle();

        QRFragment fragment = new QRFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_qrcode, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }


    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    public void setRequestCameraPermission() {
        if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)){
            new Confirma
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }




    // initiate TextureView.SurfaceTextureListener functions
    /**
     * Opens the camera specified by {@link QRFragment#mCameraId}.
     */
    private void openCamera (int width, int height){
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            req
        }

    }



}
