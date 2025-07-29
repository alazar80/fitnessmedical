package com.example.sql;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.objects.DetectedObject;
import com.google.mlkit.vision.objects.ObjectDetection;
import com.google.mlkit.vision.objects.ObjectDetector;
import com.google.mlkit.vision.objects.defaults.ObjectDetectorOptions;
import java.io.IOException;
import java.util.List;

public class detect extends AppCompatActivity {
    private static final int REQUEST_IMAGE = 100;
    private ImageView imageView;
    private TextView tvTextResult, tvObjectResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detect);
        ThemeUtil.applyBackground(this, R.id.mainLayout);
        ThemeUtil.applyThemeFromPrefs(this);
        imageView      = findViewById(R.id.imageView);
        tvTextResult   = findViewById(R.id.tvTextResult);
        tvObjectResult = findViewById(R.id.tvObjectResult);
        Button btnSelect = findViewById(R.id.btnSelect);

        btnSelect.setOnClickListener(v -> {
            Intent i = new Intent(Intent.ACTION_PICK,
                                  MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(i, REQUEST_IMAGE);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            try {
                Bitmap bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                imageView.setImageBitmap(bmp);
                processImage(bmp);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void processImage(Bitmap bitmap) {
        InputImage image = InputImage.fromBitmap(bitmap, 0);

        // 1) Text Recognition
        TextRecognizer recognizer =
                TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
        recognizer.process(image)
            .addOnSuccessListener(this::displayText)
            .addOnFailureListener(e -> tvTextResult.setText("Text failed: "+e));

        // 2) Object Detection
        ObjectDetectorOptions options =
            new ObjectDetectorOptions.Builder()
                .setDetectorMode(ObjectDetectorOptions.SINGLE_IMAGE_MODE)
                .enableMultipleObjects()
                .enableClassification()  // Optional: returns labels
                .build();

        ObjectDetector detector = ObjectDetection.getClient(options);
        detector.process(image)
            .addOnSuccessListener(this::displayObjects)
            .addOnFailureListener(e -> tvObjectResult.setText("Objects failed: "+e));
    }

    private void displayText(Text result) {
        StringBuilder sb = new StringBuilder();
        for (Text.TextBlock block : result.getTextBlocks()) {
            sb.append(block.getText()).append("\n");
        }
        tvTextResult.setText("Recognized Text:\n" + sb);
    }

    private void displayObjects(List<DetectedObject> objects) {
        if (objects.isEmpty()) {
            tvObjectResult.setText("Detected Objects:\nNone");
            return;
        }
        StringBuilder sb = new StringBuilder();
        for (DetectedObject obj : objects) {
            sb.append("ID ").append(obj.getTrackingId()).append(": ");
            if (obj.getLabels().isEmpty()) {
                sb.append("Unknown");
            } else {
                for (DetectedObject.Label lbl : obj.getLabels()) {
                    sb.append(lbl.getText())
                      .append(" (").append(lbl.getConfidence()*100).append("%) ");
                }
            }
            sb.append("\n");
        }
        tvObjectResult.setText("Detected Objects:\n" + sb);
    }
}
