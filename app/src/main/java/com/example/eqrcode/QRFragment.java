package com.example.eqrcode;

import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.dynamsoft.barcode.BarcodeReader;
import com.example.eqrcode.utils.Camera;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

public class QRFragment extends Fragment {
    private static final String TAG = "Barcode";
    private Camera camera;
    private TextureView textureView;
    private EditText editText;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        render一个view
        return inflater.inflate(R.layout.qrcode_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        动态绑定放在onViewCreated
        editText = view.findViewById(R.id.textQR);
        textureView = view.findViewById(R.id.textureView);

//        textureView.post(new Runnable() {
//            @Override
//            public void run() {
//
//            }
//        });

        // todo: initiate CAMERA
        this.camera = new Camera(view, getContext());
        textureView.setSurfaceTextureListener(textureListener);
    }

    TextureView.SurfaceTextureListener textureListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            camera.openCamera(getActivity());
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
            // Transform you image captured size according to the surface width and height
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {

        }
    };

    @Override
    public void onPause() {
        super.onPause();
        Log.e(TAG, "onPause");
        camera.closeCamera();
        camera.stopBackgroundThread();

    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e(TAG, "onResume");
        camera.startBackgroundThread();

    }

    public void createDetector() {
        final BarcodeDetector barcodeDetector = new BarcodeDetector.Builder(getContext())
                .setBarcodeFormats(Barcode.DATA_MATRIX | Barcode.QR_CODE)
                .build();

        camera.setUpDetector(barcodeDetector);


        if (barcodeDetector.isOperational()){
            barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
                @Override
                public void release() {

                }

                @Override
                public void receiveDetections(Detector.Detections<Barcode> detections) {
                    final SparseArray<Barcode> barcodeSparseArray = detections.getDetectedItems();
                    if (barcodeSparseArray.size() > 0){
                        Log.i(TAG, barcodeSparseArray.size() + "barcode detected");
                        editText.setText(barcodeSparseArray.valueAt(0).displayValue);
                    }
                }
            });
        } else {
            Toast.makeText(getContext(), "Barcode detection not available", Toast.LENGTH_SHORT).show();
        }


    }

    //    private void releaseCamera(){ }

}
