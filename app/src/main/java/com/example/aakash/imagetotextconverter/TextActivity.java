package com.example.aakash.imagetotextconverter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;
import com.google.firebase.ml.vision.text.RecognizedLanguage;

import java.util.List;

public class TextActivity extends AppCompatActivity {

    TextView textView;

    FirebaseVisionImage image;
    FirebaseVisionTextRecognizer detector;

    private static final String TAG = TextActivity.class.toString();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text);

        textView = findViewById(R.id.displayed_text);

        Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(),
                R.drawable.test_image);

        if(bitmap!=null)
        {
            image = FirebaseVisionImage.fromBitmap(bitmap);
            detector = FirebaseVision.getInstance()
                    .getOnDeviceTextRecognizer();

            StringBuilder sb = new StringBuilder();

            //start processing the image
            Task<FirebaseVisionText> result =
                    detector.processImage(image)
                            .addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                                @Override
                                public void onSuccess(FirebaseVisionText firebaseVisionText) {
                                    // Task completed successfully
                                    Toast.makeText(TextActivity.this, "Detected some text", Toast.LENGTH_SHORT).show();
                                    Log.d(TAG, "onSuccess: Detected some text");

                                    //extract Text
                                    String resultText = firebaseVisionText.getText();
                                    textView.setText(resultText);
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



            /*//build the String
            *//*sb.append(" ");
            sb.append();*//*
            textView.setText(sb);
*/

        }
        else
        {
            Log.d(TAG, "onCreate: Bitmap is empty");
        }
    }
}
