package com.example.aakash.imagetotextconverter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    private  static final String TAG = MainActivity.class.toString();
    private final int SELECT_PHOTO = 1;

    TextView promptText;
    ImageView imageView;
    ImageButton cancelButton;
    ImageButton nextButton;
    ImageButton galleryButton;
    ImageButton cameraButton;
    Bitmap receivedImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.image_container);
        cancelButton=findViewById(R.id.cancel);
        nextButton = findViewById(R.id.next);
        cameraButton = findViewById(R.id.capture);
        galleryButton = findViewById(R.id.gallery);
        promptText = findViewById(R.id.promptText);

        buttonEffect(cancelButton);
        buttonEffect(nextButton);
        buttonEffect(cameraButton);
        buttonEffect(galleryButton);

        if(savedInstanceState!=null)
        {
            //get the image from savedInstanceState
            String filename = savedInstanceState.getString("imageResource");
            try {
                    FileInputStream is = this.openFileInput(filename);
                    receivedImage = BitmapFactory.decodeStream(is);
                    is.close();
                    imageView.setImageBitmap(receivedImage);
            } catch (Exception e) {
                e.printStackTrace();
            }

            imageView.setVisibility(savedInstanceState.getInt("imageViewVisibility"));
            promptText.setVisibility(savedInstanceState.getInt("promptTextVisibility"));

        }
        /*else {
            imageView.setVisibility(View.GONE);
            promptText.setVisibility(View.VISIBLE);
        }*/

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
                        imageView.setVisibility(View.VISIBLE);
                        promptText.setVisibility(View.GONE);
                        imageView.setImageBitmap(selectedImage);
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
            //compress the bitmap and send to new activity

                try
                {
                    //Pop intent
                    String filename = bitmapToFile(receivedImage);
                    Intent in1 = new Intent(this, TextActivity.class);
                    in1.putExtra("image", filename);
                    startActivity(in1);
                }
                catch (Exception e)
                {
                    Log.d(TAG, "onNextClick: failed to convert the bitmap");
                    Toast.makeText(this, "Image is too large to be processed!", Toast.LENGTH_SHORT).show();
                }

        }
        else
        {
            Log.d(TAG, "onNextClick: recievedImage is empty!");
            Toast.makeText(this, "No image was  selected!", Toast.LENGTH_SHORT).show();
        }

    }

    String bitmapToFile (Bitmap image)
    {
        try {//Write file
            String filename = "bitmap.png";
            FileOutputStream stream = this.openFileOutput(filename, Context.MODE_PRIVATE);
            image.compress(Bitmap.CompressFormat.PNG, 100, stream);
            //Cleanup
            stream.close();
            return filename;
        }
        catch (Exception e) {
        e.printStackTrace();
         }

        return null;
    }
    
    //when gallery button is clicked
    public void onGalleryCLick(View view) {

        //start the imagePicker
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, SELECT_PHOTO);

    }

    public void onCancelClick(View view) {

        if(imageView.getDrawable()!=null) {
            imageView.setVisibility(View.GONE);
            promptText.setVisibility(View.VISIBLE);
            //imageView.setImageDrawable(null);
        }
        else
        {
            Toast.makeText(this, "No image is selected!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        try
        {
            BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
            Bitmap bitmap = drawable.getBitmap();
            outState.putString("imageResource", bitmapToFile(bitmap));
            outState.putInt("imageViewVisibility", imageView.getVisibility());
            outState.putInt("promptTextVisibility", promptText.getVisibility());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    //button click effect
    public static void buttonEffect(View button){
        button.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        v.getBackground().setColorFilter(0xe0f47521,PorterDuff.Mode.SRC_ATOP);
                        v.invalidate();
                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        v.getBackground().clearColorFilter();
                        v.invalidate();
                        break;
                    }
                }
                return false;
            }
        });
    }

}

