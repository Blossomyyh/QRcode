package com.example.eqrcode;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class QRActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode);
        if (savedInstanceState == null){
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.qrcode_container, QRFragment.newInstance())
                    .commit();
        }
    }



}
