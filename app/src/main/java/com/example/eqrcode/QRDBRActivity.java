package com.example.eqrcode;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.dynamsoft.barcode.BarcodeReader;
import com.dynamsoft.barcode.EnumBarcodeFormat;
import com.dynamsoft.barcode.PublicRuntimeSettings;
import com.example.eqrcode.utils.DBRCache;

public class QRDBRActivity extends AppCompatActivity {

    private BarcodeReader mBarcodeReader;
    private DBRCache mDBRCache;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrdbrcode);

        try {
            mBarcodeReader = new BarcodeReader("t0068MgAAACm/O50JQCeJC5TJTNpXrUs4Do3MPzQxK0CvQvCGslylduMz/icYA3lAmVbE7NYhTFM60BRpW3QUav1sP6MWdZo=");
        } catch (Exception e) {
            e.printStackTrace();
        }

        mDBRCache = DBRCache.get(this);
        mDBRCache.put("linear", "1");

        if (savedInstanceState == null){
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.qrcode_container, QRDBRFragment.newInstance())
                    .commit();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try{
            int nBarcodeFormat =0;
            nBarcodeFormat = nBarcodeFormat| EnumBarcodeFormat.BF_QR_CODE;

            PublicRuntimeSettings runtimeSettings =  mBarcodeReader.getRuntimeSettings();
            runtimeSettings.mBarcodeFormatIds = nBarcodeFormat;
            mBarcodeReader.updateRuntimeSettings(runtimeSettings);

        } catch (Exception e){
            e.printStackTrace();
        }

    }
    public BarcodeReader getMainBarcdoeReader(){
        return mBarcodeReader;
    }
}
