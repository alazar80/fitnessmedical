package com.example.sql;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class Shop extends AppCompatActivity {
    private EditText usernameInput, phoneInput, emailInput, passwordInput;
    private ImageView backButton;
    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);
         backButton=findViewById(R.id.backButton);


//        usernameInput = findViewById(R.id.usernameInput);
//        phoneInput = findViewById(R.id.phoneInput);
//        emailInput = findViewById(R.id.emailInput);
//        passwordInput = findViewById(R.id.passwordInput);
//        ArrayList<String> gifUrls = getIntent().getStringArrayListExtra("gifUrls");
//
//        if (gifUrls != null && !gifUrls.isEmpty()) {
//            for (String gif : gifUrls) {
//                ToastUtil.show(this, "GIF: " + gif, 1/3);
//            }
//        } else {
//            ToastUtil.show(this, "No GIF URLs received", 1/3);
//        }
//
//        String username = usernameInput.getText().toString().trim();
//        String phone = phoneInput.getText().toString().trim();
//        String email = emailInput.getText().toString().trim();
//        String password = passwordInput.getText().toString().trim();
//        String postData;
        backButton.setOnClickListener(v ->onBackPressed());
//        try {
//            postData = "username=" + URLEncoder.encode(username, "UTF-8") +
//                    "&password=" + URLEncoder.encode(password, "UTF-8") +
//                    "&phone=" + URLEncoder.encode(phone, "UTF-8") +
//                    "&email=" + URLEncoder.encode(email, "UTF-8");
//        } catch (UnsupportedEncodingException e) {
//            throw new RuntimeException(e);
//        }
//        ServerRequest.sendPost(url, postData, new ServerRequest.ServerCallback() {
//            @Override
//            public void onSuccess(String result) {
//                runOnUiThread(() -> ToastUtil.show(getApplicationContext(), result, 1/3));
//            }
//
//            @Override
//            public void onError(String error) {
//                runOnUiThread(() -> ToastUtil.show(getApplicationContext(), "Error: " + error, 1/3));
//            }
//        });
    }
}
