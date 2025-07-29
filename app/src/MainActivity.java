package com.example.sql;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.security.crypto.EncryptedFile;
import androidx.security.crypto.MasterKey;

import com.google.android.gms.safetynet.SafetyNet;

import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private EditText usernameInput, phoneInput, emailInput, passwordInput;
    private Button signUpButton, uploadImageButton, btnPickDate;
    private ImageView profileImageView, backButton;
    private CheckBox userCheckbox;
    private TextView tvSelectedDate;

    private String base64Image = "";
    private String selectedDate = "";
    private static final int IMAGE_PICK_CODE = 1001;
    private static final String FILE_NAME = "secret_data.txt";
    private static final String SAFETYNET_API_KEY = "YOUR_API_KEY_HERE"; // MOVE TO SERVER for best security
    private static final String URL_PATH = ApiConfig.INSERT_USER;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ThemeUtil.applyBackground(this, R.id.mainLayout);
        ThemeUtil.applyThemeFromPrefs(this);
        initViews();

        if (isDeviceRooted()) {
            ToastUtil.show(this, "Rooted device detected! App will close.", 1/3);
            finish();
            return;
        }

        checkSafetyNet();
        secureSampleData();

        backButton.setOnClickListener(v -> finish());
        uploadImageButton.setOnClickListener(v -> pickImage());
        btnPickDate.setOnClickListener(v -> showDatePicker());
        signUpButton.setOnClickListener(v -> signup());
    }

    private void initViews() {
        usernameInput = findViewById(R.id.usernameInput);
        phoneInput = findViewById(R.id.phoneInput);
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        uploadImageButton = findViewById(R.id.uploadImageButton);
        profileImageView = findViewById(R.id.profileImageView);
        backButton = findViewById(R.id.backButton);
        signUpButton = findViewById(R.id.signUpButton);
        btnPickDate = findViewById(R.id.btn_pick_date);
        tvSelectedDate = findViewById(R.id.tv_selected_date);
        userCheckbox = findViewById(R.id.doctorCheckbox);
    }

    private void checkSafetyNet() {
        byte[] nonce = generateNonce();
        SafetyNet.getClient(this).attest(nonce, SAFETYNET_API_KEY)
                .addOnSuccessListener(response -> {
                    String jwsResult = response.getJwsResult();
                    Log.d("SafetyNet", "Success: " + jwsResult);
                    // TODO: Send jwsResult to your server for validation
                })
                .addOnFailureListener(e -> {
                    Log.e("SafetyNet", "Failed", e);
                });
    }

    private void secureSampleData() {
        try {
            MasterKey masterKey = new MasterKey.Builder(this)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();

            File file = new File(getFilesDir(), FILE_NAME);

            EncryptedFile encryptedFile = new EncryptedFile.Builder(
                    this,
                    file,
                    masterKey,
                    EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
            ).build();

            writeEncryptedFile(encryptedFile, "Sensitive information here!");
            String content = readEncryptedFile(encryptedFile);
            System.out.println("Decrypted content: " + content);

        } catch (IOException | GeneralSecurityException e) {   // <<< FIX HERE
            e.printStackTrace();
        }
    }


    private void showDatePicker() {
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR), month = c.get(Calendar.MONTH), day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dpd = new DatePickerDialog(this, (view, year1, month1, dayOfMonth) -> {
            selectedDate = year1 + "-" + (month1 + 1) + "-" + dayOfMonth;
            tvSelectedDate.setText("Date of Birth: " + selectedDate);
        }, year, month, day);

        dpd.show();
    }

    private void pickImage() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_PICK_CODE && resultCode == RESULT_OK && data != null) {
            profileImageView.setImageURI(data.getData());
            base64Image = encodeImage();
        }
    }

    private String encodeImage() {
        BitmapDrawable drawable = (BitmapDrawable) profileImageView.getDrawable();
        if (drawable == null) return "";

        Bitmap bitmap = drawable.getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return Base64.encodeToString(baos.toByteArray(), Base64.NO_WRAP);
    }

    private void signup() {
        String username = usernameInput.getText().toString().trim();
        String phone = phoneInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();
        String dob = selectedDate.isEmpty() ? "2000-01-01" : selectedDate;
        String isUser = userCheckbox.isChecked() ? "1" : "0";

        if (username.isEmpty() || phone.isEmpty() || email.isEmpty() || password.isEmpty()) {
            ToastUtil.show(this, "Please fill all required fields", 1/3);
            return;
        }

        new Thread(() -> {
            try {
                URL url = new URL(URL_PATH);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                String data = "username=" + URLEncoder.encode(username, "UTF-8") +
                        "&email=" + URLEncoder.encode(email, "UTF-8") +
                        "&phone=" + URLEncoder.encode(phone, "UTF-8") +
                        "&password=" + URLEncoder.encode(password, "UTF-8") +
                        "&date_of_birth=" + URLEncoder.encode(dob, "UTF-8") +
                        "&profile_image=" + URLEncoder.encode(base64Image, "UTF-8");

                OutputStream os = conn.getOutputStream();
                os.write(data.getBytes(StandardCharsets.UTF_8));
                os.flush();
                os.close();

                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    response.append(line);
                }
                br.close();

                JSONObject obj = new JSONObject(response.toString());
                runOnUiThread(() -> {
                    if (obj.optBoolean("success")) {
                        int userId = obj.optInt("user_id", -1);
                        String returnedEmail = obj.optString("email", email);

                        Intent intent = new Intent(this, Welcome.class);
                        intent.putExtra("user_id", userId);
                        intent.putExtra("email", returnedEmail);
                        startActivity(intent);

                        ToastUtil.show(this, "Signup Success!", 1/3);
                    } else {
                        ToastUtil.show(this, "Error: " + obj.optString("error"), 1/3);
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> ToastUtil.show(this, "Error: " + e.getMessage(), 1/3));
            }
        }).start();
    }

    private boolean isDeviceRooted() {
        String[] paths = {
                "/system/app/Superuser.apk", "/sbin/su", "/system/bin/su", "/system/xbin/su",
                "/data/local/xbin/su", "/data/local/bin/su", "/system/sd/xbin/su",
                "/system/bin/failsafe/su", "/data/local/su"
        };

        for (String path : paths) {
            if (new File(path).exists()) {
                return true;
            }
        }
        return false;
    }

    private byte[] generateNonce() {
        byte[] nonce = new byte[24];
        new SecureRandom().nextBytes(nonce);
        return nonce;
    }

    private void writeEncryptedFile(EncryptedFile encryptedFile, String data) throws IOException, GeneralSecurityException {
        try (OutputStream outputStream = encryptedFile.openFileOutput()) {
            outputStream.write(data.getBytes(StandardCharsets.UTF_8));
        }
    }


    private String readEncryptedFile(EncryptedFile encryptedFile) throws IOException, GeneralSecurityException {
        try (InputStream inputStream = encryptedFile.openFileInput();
             ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {

            byte[] data = new byte[1024];
            int nRead;
            while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }

            return new String(buffer.toByteArray(), StandardCharsets.UTF_8);
        }
    }


}
