package com.example.aakash.imagetotextconverter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    private  static final String TAG = MainActivity.class.toString();
    private final int SELECT_PHOTO = 1;

    ImageView imageView;
    ImageButton cancelButton;
    Bitmap receivedImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = findViewById(R.id.image_container);
        cancelButton=findViewById(R.id.cancel);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch(requestCode) {
            case SELECT_PHOTO:
                if(resultCode == RESULT_OK){
                    try {
                        final Uri imageUri = imageReturnedIntent.getData();
                        final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                        final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                        imageView.setImageBitmap(selectedImage);
                        cancelButton.setClickable(true);
                        receivedImage=selectedImage;
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.d(TAG, "onActivityResult: Failed to load image");
                        Toast.makeText(this, "Failed to load Image", Toast.LENGTH_SHORT).show();
                    }
                }
        }


    }

    // When next button is clicked
    //start TextActivity
    public void onNextClick(View view) {


        if(receivedImage!=null)
        {
            //solution found from StackOverFlow
            //compress the bitmap and send to new activity
            try {
                //Write file
                String filename = "bitmap.png";
                FileOutputStream stream = this.openFileOutput(filename, Context.MODE_PRIVATE);
                receivedImage.compress(Bitmap.CompressFormat.PNG, 100, stream);

                //Cleanup
                stream.close();
                //receivedImage.recycle();

                //Pop intent
                Intent in1 = new Intent(this, TextActivity.class);
                in1.putExtra("image", filename);
                startActivity(in1);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        else
        {
            Log.d(TAG, "onNextClick: recievedImage is empty!");
            Toast.makeText(this, "No image was  selected!", Toast.LENGTH_SHORT).show();
        }

    }


    //when gallery button is clicked
    public void onGalleryCLick(View view) {

        //start the imagePicker
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, SELECT_PHOTO);

    }

    public void onCancelClick(View view) {

        imageView.setImageDrawable(null);
        cancelButton.setClickable(false);

    }
}

