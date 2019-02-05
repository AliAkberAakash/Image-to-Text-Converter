package com.example.aakash.imagetotextconverter;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;

import java.io.FileInputStream;


public class TextActivity extends AppCompatActivity {

    TextView textView;
    Button saveButton;
    Button copyButton;
    Boolean textViewState;

    FirebaseVisionImage image;
    FirebaseVisionTextRecognizer detector;

    private static final String TAG = TextActivity.class.toString();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text);

        saveButton = findViewById(R.id.saveButton);
        copyButton = findViewById(R.id.copyButton);
        textView = findViewById(R.id.displayed_text);

        buttonEffect(saveButton);
        buttonEffect(copyButton);

        textViewState = false;
        processImage();
    }

    //function to process the image
    private void processImage()
    {
        //todo: replace with a progressbar
        Toast.makeText(this, "Processing the image", Toast.LENGTH_SHORT).show();

        //solution found from StackOverFlow
        Bitmap bitmap = null;
        String filename = getIntent().getStringExtra("image");
        try {
            FileInputStream is = this.openFileInput(filename);
            bitmap = BitmapFactory.decodeStream(is);
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //if image is received start processing it
        if(bitmap!=null)
        {
            image = FirebaseVisionImage.fromBitmap(bitmap);
            detector = FirebaseVision.getInstance()
                    .getOnDeviceTextRecognizer();

            //start processing the image
            Task<FirebaseVisionText> result =
                    detector.processImage(image)
                            .addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                                @Override
                                public void onSuccess(FirebaseVisionText firebaseVisionText) {
                                    // Task completed successfully
                                    Log.d(TAG, "onSuccess: Detected some text");

                                    //extract Text
                                    String resultText = firebaseVisionText.getText();
                                    if(resultText.length()>0) {
                                        textView.setText(resultText);
                                        textViewState=true;
                                    }
                                    else {
                                        textView.setText(getString(R.string.text_not_detected_msg));
                                        textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                                        textViewState=false;
                                    }
                                }
                            })
                            .addOnFailureListener(
                                    new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            // Task failed with an exception
                                            Toast.makeText(TextActivity.this,
                                                    "Could not detect any text", Toast.LENGTH_SHORT).show();
                                            Log.d(TAG, "onFailure: Failed to recognized text");
                                        }
                                    });


        }
        else
        {
            Log.d(TAG, "processImage: Bitmap is empty");
        }
    }

    public void onSaveButtonClicked(View view) {



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

    public void onCopyButtonClicked(View view) {
        if(textViewState) {
            Toast.makeText(this, "Text has been copied to clipboard", Toast.LENGTH_SHORT).show();
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("label", textView.getText().toString());
            clipboard.setPrimaryClip(clip);
        }
        else
        {
            Toast.makeText(this, "No text was detected", Toast.LENGTH_SHORT).show();
        }
    }
}
