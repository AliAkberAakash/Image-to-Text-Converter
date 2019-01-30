package com.example.aakash.imagetotextconverter;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    private  static final String TAG = MainActivity.class.toString();

    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.image_container);
    }

    // When next button is clicked
    public void onNextClick(View view) {

        //start TextActivity
        Intent intent = new Intent(MainActivity.this, TextActivity.class);
        startActivity(intent);

    }



}

