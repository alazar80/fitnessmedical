package com.example.sql;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import com.example.sql.FFT;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
//import android.widget.CircleImageView;

import de.hdodenhof.circleimageview.CircleImageView;

import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class GuitarTunerActivity extends AppCompatActivity {
    private static final int REQUEST_RECORD_AUDIO = 200;
    private static final int SAMPLE_RATE = 44100;
    private AudioRecord recorder;
    private boolean isRecording = false;
    private int fftSize = 2048;
    private FFT fft = new FFT(fftSize);
    private Handler uiHandler = new Handler();
    private TextView freqText, noteText, detuneText;
    private LinearLayout stringLayout;
    private String[] notes = {"E", "A", "D", "G", "B", "E"};
    private double[] noteFreqs = {82.41, 110.00, 146.83, 196.00, 246.94, 329.63};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guitar_tuner);

        freqText = findViewById(R.id.frequencyText);
        noteText = findViewById(R.id.noteText);
        detuneText = findViewById(R.id.detuneText);
        stringLayout = findViewById(R.id.stringLayout);

        // create 6 circles
        for (String n : notes) {
            TextView tv = new TextView(this);
            tv.setText(n);
            tv.setTextSize(20);
            tv.setGravity(Gravity.CENTER);
            tv.setBackgroundResource(R.drawable.circle_white);
            LinearLayout.LayoutParams lp =
                new LinearLayout.LayoutParams(150, 150);
            lp.setMargins(8, 0, 8, 0);
            stringLayout.addView(tv, lp);
        }

        if (ContextCompat.checkSelfPermission(this,
            Manifest.permission.RECORD_AUDIO)
            != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.RECORD_AUDIO},
                REQUEST_RECORD_AUDIO);
        } else {
            startTuner();
        }
    }

    private void startTuner() {
        int bufSize = AudioRecord.getMinBufferSize(
            SAMPLE_RATE,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT);

        recorder = new AudioRecord(
            MediaRecorder.AudioSource.MIC,
            SAMPLE_RATE,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT,
            bufSize * 2);

        recorder.startRecording();
        isRecording = true;
        new Thread(this::recordLoop).start();
    }

    private void recordLoop() {
        short[] audioBuffer = new short[fftSize];
        double[] real = new double[fftSize];
        double[] imag = new double[fftSize];

        while (isRecording) {
            int read = recorder.read(audioBuffer, 0, fftSize);
            if (read > 0) {
                // to double, windowing could be added
                for (int i = 0; i < fftSize; i++) {
                    real[i] = audioBuffer[i];
                    imag[i] = 0;
                }
                fft.fft(real, imag);

                // find peak
                int peakIndex = 1;
                double maxMag = 0;
                for (int i = 1; i < fftSize/2; i++) {
                    double mag = Math.hypot(real[i], imag[i]);
                    if (mag > maxMag) {
                        maxMag = mag;
                        peakIndex = i;
                    }
                }
                final double freq = peakIndex * SAMPLE_RATE / (double)fftSize;
                uiHandler.post(() -> updateUI(freq));
            }
        }
    }

    private void updateUI(double frequency) {
        freqText.setText(String.format("%.1f Hz", frequency));
        // find closest note
        double minDiff = Double.MAX_VALUE;
        int best = -1;
        for (int i = 0; i < noteFreqs.length; i++) {
            double diff = Math.abs(frequency - noteFreqs[i]);
            if (diff < minDiff) {
                minDiff = diff;
                best = i;
            }
        }
        if (best >= 0) {
            String n = notes[best];
            noteText.setText(n);
            double cents = 1200 * Math.log(frequency / noteFreqs[best]) / Math.log(2);
            detuneText.setText(String.format("%+.1f cents", cents));

                // highlight strings
                        for (int i = 0; i < stringLayout.getChildCount(); i++) {
                        View v = stringLayout.getChildAt(i);
                        v.setBackgroundResource(
                                    i == best ? R.drawable.circle_green : R.drawable.circle_white);
                    }
        }
    }

    @Override
    public void onRequestPermissionsResult(int req, 
        @NonNull String[] perms, @NonNull int[] results) {
        if (req == REQUEST_RECORD_AUDIO &&
            results.length > 0 &&
            results[0] == PackageManager.PERMISSION_GRANTED) {
            startTuner();
        } else {
            // permission denied
            noteText.setText("Microphone permission required");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isRecording = false;
        if (recorder != null) recorder.release();
    }
}
