package com.example.krsukdw;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class AwalActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_awal);

        }

    public void AwalMasuk(View view){
        Intent i = new Intent(AwalActivity.this,MainActivity.class);
        startActivity(i);
    }


}
