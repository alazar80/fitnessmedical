package com.example.sql;
import com.android.volley.toolbox.JsonObjectRequest;
import com.hbb20.CountryCodePicker;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.*;
import androidx.annotation.Nullable;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import android.content.res.Configuration;
import java.util.Locale;

public class SignupActivity  extends BaseActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private EditText emailEdit, passEdit, phoneEdit, usernameEdit, firstNameEdit, lastNameEdit;
    private TextView dateText, titleTextView;
    private Button signupBtn, pickDateBtn, uploadImgBtn;
    private ImageView profileImage;
    private RadioGroup genderGroup;
    private CheckBox doctorCheckbox;
    private CountryCodePicker countryCodePicker;
    private ProgressDialog progressDialog;
    private Bitmap selectedImageBitmap;
    private String selectedDate = "";
    private final String URL_PATH = ApiConfig.INSERT_USER;
ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ThemeUtil.applyBackground(this, R.id.mainLayout);
        ThemeUtil.applyThemeFromPrefs(this);

        String lang = getSharedPreferences("prefs", MODE_PRIVATE).getString("lang", "en");
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.setLocale(locale);
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
        countryCodePicker = findViewById(R.id.countryCodePicker);

        emailEdit = findViewById(R.id.emailInput);
        passEdit = findViewById(R.id.passwordInput);
        phoneEdit = findViewById(R.id.phoneInput);
        usernameEdit = findViewById(R.id.usernameInput);
        firstNameEdit = findViewById(R.id.firstNameInput);
        lastNameEdit = findViewById(R.id.lastNameInput);
        titleTextView = findViewById(R.id.titleTextView);
        progressBar = new ProgressBar(this);
        progressBar.setIndeterminate(true);
        progressBar.setVisibility(ProgressBar.GONE);

        signupBtn = findViewById(R.id.signUpButton);
        dateText = findViewById(R.id.tv_selected_date);
        pickDateBtn = findViewById(R.id.btn_pick_date);
        uploadImgBtn = findViewById(R.id.uploadImageButton);
        profileImage = findViewById(R.id.profileImageView);
        genderGroup = findViewById(R.id.genderRadioGroup);

        doctorCheckbox = findViewById(R.id.doctorCheckbox);
        progressDialog = new ProgressDialog(this);

        pickDateBtn.setOnClickListener(v -> showDatePickerDialog());
        uploadImgBtn.setOnClickListener(v -> openImagePicker());
        signupBtn.setOnClickListener(v -> signup());

        emailEdit.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                passEdit.requestFocus(); // 👈 move to next input
                return true;
            }
            return false;
        });

        usernameEdit.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                phoneEdit.requestFocus(); // 👈 move to next input
                return true;
            }
            return false;
        });

        doctorCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            usernameEdit.setVisibility(isChecked ? View.GONE : View.VISIBLE);
            firstNameEdit.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            lastNameEdit.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            titleTextView.setText(isChecked ? "Doctor's Signup Page" : "User Signup Page");
        });

    }

    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            selectedDate = year + "-" + (month + 1) + "-" + dayOfMonth;
            dateText.setText("DOB: " + selectedDate);
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            try {
                selectedImageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                profileImage.setImageBitmap(selectedImageBitmap);
            } catch (IOException e) {
                ToastUtil.show(this, "Failed to load image.", Toast.LENGTH_SHORT);
            }
        }
    }

//    private void signup() {
//        boolean isDoctor = doctorCheckbox.isChecked();
//        String email = emailEdit.getText().toString().trim();
//        String password = passEdit.getText().toString().trim();
//        String phone = phoneEdit.getText().toString().trim();
//        String fullPhone = countryCodePicker.getSelectedCountryCodeWithPlus() + phone;
//        String username = usernameEdit.getText().toString().trim();
//        String firstName = firstNameEdit.getText().toString().trim();
//        String lastName = lastNameEdit.getText().toString().trim();
//        int selectedGenderId = genderGroup.getCheckedRadioButtonId();
//
//        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
//            emailEdit.setError("Invalid email");
//            return;
//        }
//        if (password.length() < 6) {
//            passEdit.setError("Password too short");
//            return;
//        }
//        if (selectedDate.isEmpty()) {
//            ToastUtil.show(this, "Pick a date of birth", Toast.LENGTH_SHORT);
//            return;
//        }
//        if (selectedImageBitmap == null) {
//            ToastUtil.show(this, "Upload a profile image", Toast.LENGTH_SHORT);
//            return;
//        }
//        if (selectedGenderId == -1) {
//            ToastUtil.show(this, "Select gender", Toast.LENGTH_SHORT);
//            return;
//        }
//
//        String gender = ((RadioButton) findViewById(selectedGenderId)).getText().toString();
//        String base64Image = encodeImageToBase64(selectedImageBitmap);
//
//        showProgressDialog("Registering...");
//
//        new Thread(() -> {
//            try {
//                URL url = new URL(URL_PATH);
//                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//                conn.setRequestMethod("POST");
//                conn.setDoOutput(true);
//                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
//
//                String data = "email=" + URLEncoder.encode(email, "UTF-8") +
//                        "&phone=" + URLEncoder.encode(fullPhone, "UTF-8") +
//                        "&password=" + URLEncoder.encode(password, "UTF-8") +
//                        "&date_of_birth=" + URLEncoder.encode(selectedDate, "UTF-8") +
//                        "&gender=" + URLEncoder.encode(gender, "UTF-8") +
//                        "&profile_image=" + URLEncoder.encode(base64Image, "UTF-8") +
//                        "&is_doctor=" + URLEncoder.encode(String.valueOf(isDoctor), "UTF-8") +
//                        "&username=" + URLEncoder.encode(username, "UTF-8") +
//                        "&first_name=" + URLEncoder.encode(firstName, "UTF-8") +
//                        "&last_name=" + URLEncoder.encode(lastName, "UTF-8");
//
//                OutputStream os = conn.getOutputStream();
//                os.write(data.getBytes());
//                os.flush();
//                os.close();
//
//                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
//                StringBuilder response = new StringBuilder();
//                String line;
//                while ((line = br.readLine()) != null) {
//                    response.append(line);
//                }
//                br.close();
//
//                JSONObject obj = new JSONObject(response.toString());
//
//                runOnUiThread(() -> {
//                    hideProgressDialog();
//                    if (obj.optBoolean("success")) {
//                        ToastUtil.show(this, "Signup Successful!", Toast.LENGTH_SHORT);
//
//                        int newId = isDoctor
//                                ? obj.optInt("doctor_id", -1)
//                                : obj.optInt("user_id", -1);
//
//                        if (newId != -1) {
//                            if (isDoctor) {
//                                assignDefaultDataToDoctor(newId);
//                            } else {
//                                assignDefaultDataToUser(newId);
//                            }
//                        }
//
//                        Intent intent;
//                        if (isDoctor) {
//                            intent = new Intent(SignupActivity.this, doctorwelcome.class);
//                            intent.putExtra("doctor_id", newId);
//                            intent.putExtra("email", email);
//                        } else {
//                            intent = new Intent(SignupActivity.this, Welcome.class);
//                            intent.putExtra("user_id", newId);
//                            intent.putExtra("email", email);
//                        }
//                        startActivity(intent);
//                        finish();
//                    }
//
////                    if (obj.optBoolean("success")) {
////                        ToastUtil.show(this, "Signup Successful!", Toast.LENGTH_SHORT);
////
////                        int newUserId = isDoctor ? obj.optInt("doctor_id", -1) : obj.optInt("user_id", -1);
////
////                        if (newUserId != -1) {
////                            assignDefaultDataToUser(newUserId);
////                        }
////
////                        Intent intent;
////                        if (isDoctor) {
////                            intent = new Intent(SignupActivity.this, doctorwelcome.class);
////                            intent.putExtra("doctor_id", newUserId);
////                            intent.putExtra("email", email);
////                        } else {
////                            intent = new Intent(SignupActivity.this, Welcome.class);
////                            intent.putExtra("user_id", newUserId);
////                            intent.putExtra("email", email);
////                        }
////                        startActivity(intent);
////                        finish();
////
////                    } else {
//                        ToastUtil.show(this, "Error: " + obj.optString("error"), Toast.LENGTH_SHORT);
//      //              }
//                });
//
//            } catch (Exception e) {
//                runOnUiThread(() -> {
//                    hideProgressDialog();
//                    ToastUtil.show(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT);
//                });
//            }
//        }).start();
//    }
//private void signup() {
//    boolean isDoctor = doctorCheckbox.isChecked();
//    String email     = emailEdit.getText().toString().trim();
//    String password  = passEdit.getText().toString().trim();
//    String phone     = phoneEdit.getText().toString().trim();
//    String fullPhone = countryCodePicker.getSelectedCountryCodeWithPlus() + phone;
//    String username  = usernameEdit.getText().toString().trim();
//    String firstName = firstNameEdit.getText().toString().trim();
//    String lastName  = lastNameEdit.getText().toString().trim();
//    int selectedGenderId = genderGroup.getCheckedRadioButtonId();
//
//    // (Perform your existing local validation: email format, password ≥6, DOB non-empty, image non-null, gender chosen, etc.)
//    if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
//        emailEdit.setError("Invalid email");
//        return;
//    }
//    if (password.length() < 6) {
//        passEdit.setError("Password too short");
//        return;
//    }
//    if (selectedDate.isEmpty()) {
//        ToastUtil.show(this, "Pick date of birth", Toast.LENGTH_SHORT);
//        return;
//    }
//    if (selectedImageBitmap == null) {
//        ToastUtil.show(this, "Upload a profile image", Toast.LENGTH_SHORT);
//        return;
//    }
//    if (selectedGenderId == -1) {
//        ToastUtil.show(this, "Select gender", Toast.LENGTH_SHORT);
//        return;
//    }
//
//    String gender      = ((RadioButton) findViewById(selectedGenderId)).getText().toString();
//    String base64Image = encodeImageToBase64(selectedImageBitmap);
//
//    // 1) Build JSON body exactly like our send_verification_signup.php expects:
//    JSONObject body = new JSONObject();
//    try {
//        body.put("email",          email);
//        body.put("password",       password);
//        body.put("phone",          fullPhone);
//        body.put("date_of_birth",  selectedDate);   // format "YYYY-MM-DD"
//        body.put("gender",         gender);
//        body.put("profile_image",  base64Image);
//        body.put("is_doctor",      isDoctor);
//        body.put("username",       isDoctor ? JSONObject.NULL : username);
//        body.put("first_name",     isDoctor ? firstName : JSONObject.NULL);
//        body.put("last_name",      isDoctor ? lastName  : JSONObject.NULL);
//    } catch (Exception e) {
//        ToastUtil.show(this, "Error building JSON", Toast.LENGTH_SHORT);
//        return;
//    }
//
//    showProgressDialog("Sending verification code...");
//    String url = ApiConfig.SEND_SIGNUP_VERIFICATION_URL; // ← new constant
//
//    JsonObjectRequest req = new JsonObjectRequest(
//            Request.Method.POST,
//            url,
//            body,
//            response -> {
//                hideProgressDialog();
//                boolean success = response.optBoolean("success", false);
//                if (success) {
//                    // 2) Verification email was sent. Move to VerifySignupActivity:
//                    Intent i = new Intent(SignupActivity.this, VerifySignupActivity.class);
//                    i.putExtra("email",          email);
//                    i.putExtra("is_doctor",      isDoctor);
//                    i.putExtra("password",       password);
//                    i.putExtra("phone",          fullPhone);
//                    i.putExtra("date_of_birth",  selectedDate);
//                    i.putExtra("gender",         gender);
//                    i.putExtra("profile_image",  base64Image);
//                    i.putExtra("username",       username);
//                    i.putExtra("first_name",     firstName);
//                    i.putExtra("last_name",      lastName);
//
//                    startActivity(i);
//                } else {
//                    String err = response.optString("error", "Unknown error");
//                    ToastUtil.show(this, "Error: " + err, Toast.LENGTH_LONG);
//                }
//            },
//            error -> {
//                hideProgressDialog();
//                ToastUtil.show(this, "Network error: " + error.getMessage(), Toast.LENGTH_LONG);
//            }
//    ) {
//        @Override
//        public Map<String, String> getHeaders() {
//            Map<String, String> h = new HashMap<>();
//            h.put("Content-Type", "application/json");
//            return h;
//        }
//    };
//    Volley.newRequestQueue(this).add(req);
//}






    private void signup() {
        boolean isDoctor = doctorCheckbox.isChecked();
        String email     = emailEdit.getText().toString().trim();
        String password  = passEdit.getText().toString();
        String phone     = phoneEdit.getText().toString().trim();
        String username  = usernameEdit.getText().toString().trim();
        String firstName = firstNameEdit.getText().toString().trim();
        String lastName  = lastNameEdit.getText().toString().trim();
        int selectedGenderId = genderGroup.getCheckedRadioButtonId();

        // 1) Local validation
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEdit.setError(getString(R.string.error_invalid_email));
            return;
        }
        if (password.length() < 6) {
            passEdit.setError(getString(R.string.error_password_too_short));
            return;
        }
        if (selectedDate.isEmpty()) {
            ToastUtil.show(this, getString(R.string.error_pick_dob), Toast.LENGTH_SHORT);
            return;
        }
        if (selectedImageBitmap == null) {
            ToastUtil.show(this, getString(R.string.error_upload_image), Toast.LENGTH_SHORT);
            return;
        }
        if (selectedGenderId == -1) {
            ToastUtil.show(this, getString(R.string.error_select_gender), Toast.LENGTH_SHORT);
            return;
        }
        if (!isDoctor && username.isEmpty()) {
            usernameEdit.setError(getString(R.string.error_enter_username));
            return;
        }
        if (isDoctor && (firstName.isEmpty() || lastName.isEmpty())) {
            ToastUtil.show(this, getString(R.string.error_enter_full_name), Toast.LENGTH_SHORT);
            return;
        }

        // 2) Build JSON
        JSONObject body;
        try {
            body = buildSignupJson(
                    email,
                    password,
                    countryCodePicker.getSelectedCountryCodeWithPlus() + phone,
                    selectedDate,
                    ((RadioButton) findViewById(selectedGenderId)).getText().toString(),
                    encodeImageToBase64(selectedImageBitmap),
                    isDoctor,
                    username,
                    firstName,
                    lastName
            );
        } catch (Exception e) {
            ToastUtil.show(this, getString(R.string.error_building_request), Toast.LENGTH_SHORT);
            return;
        }

        // 3) Check connectivity
        if (!NetworkUtil.isOnline(this)) {
            ToastUtil.show(this, getString(R.string.error_no_internet), Toast.LENGTH_SHORT);
            return;
        }

        // 4) Send request
        showProgressDialog(getString(R.string.sending_verification));
        JsonObjectRequest req = new JsonObjectRequest(
                Request.Method.POST,
                ApiConfig.SEND_SIGNUP_VERIFICATION_URL,
                body,
                response -> {
                    hideProgressDialog();
                    if (response.optBoolean("success", false)) {
                        launchVerifyActivity(
                                email,
                                password,
                                phone,
                                selectedDate,
                                isDoctor,
                                username,
                                firstName,
                                lastName,
                                ((RadioButton) findViewById(selectedGenderId)).getText().toString(),
                                selectedImageBitmap  // pass the Bitmap!
                        );
                    } else {
                        String err = response.optString("error", getString(R.string.error_unknown));
                        ToastUtil.show(this, err, Toast.LENGTH_LONG);
                    }
                },
                error -> {
                    hideProgressDialog();
                    Log.e("SignupError", error.toString());
                    ToastUtil.show(this, getString(R.string.error_network_generic), Toast.LENGTH_LONG);
                }
        );
        // Optionally set a retry policy if you expect very large payloads:
        req.setRetryPolicy(new DefaultRetryPolicy(
                15000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));
        Volley.newRequestQueue(this).add(req);
    }


private String encodeImageToBase64(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, baos);
        return Base64.encodeToString(baos.toByteArray(), Base64.NO_WRAP);
    }

    private void showProgressDialog(String msg) {
        progressDialog.setMessage(msg);
        progressDialog.show();
    }
    private void assignDefaultDataToUser(int newUserId) {
        String assignUrl = ApiConfig.userassignUrl;

        StringRequest request = new StringRequest(Request.Method.POST, assignUrl,
                response -> {
                    Log.d("AssignResponse", "Response: " + response);
                    ToastUtil.show(this, "Defaults assigned successfully", Toast.LENGTH_SHORT);
                },
                error -> {
                    Log.e("AssignError", "Error: " + error.toString());
                    ToastUtil.show(this, "Failed to assign defaults", Toast.LENGTH_SHORT);
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", String.valueOf(newUserId));
                return params;
            }
        };

        // Set retry policy to handle large payload
        request.setRetryPolicy(new DefaultRetryPolicy(
                20000, // 20 seconds timeout
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        Volley.newRequestQueue(this).add(request);
    }


    private void assignDefaultDataToDoctor(int newDoctorId) {
        String assignUrl = ApiConfig.doctorassignUrl;

        StringRequest request = new StringRequest(Request.Method.POST, assignUrl,
                response -> {
                    Log.d("AssignDoctorResponse", "Response: " + response);
                    ToastUtil.show(this, "Doctor defaults assigned successfully", Toast.LENGTH_SHORT);
                },
                error -> {
                    Log.e("AssignDoctorError", "Error: " + error.toString());
                    ToastUtil.show(this, "Failed to assign doctor defaults", Toast.LENGTH_SHORT);
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                // The backend expects “doctor_id” (per your ApiConfig):
                params.put("doctor_id", String.valueOf(newDoctorId));
                return params;
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(
                20000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));
        Volley.newRequestQueue(this).add(request);
    }


    private void hideProgressDialog() {
        if (progressDialog.isShowing()) progressDialog.dismiss();
    }

    // buildSignupJson: pack all signup fields into a JSONObject the same way
// send_verification_signup.php expects.
    private JSONObject buildSignupJson(
            String email,
            String password,
            String fullPhone,
            String dateOfBirth,
            String gender,
            String base64Image,
            boolean isDoctor,
            String username,
            String firstName,
            String lastName
    ) throws Exception {
        JSONObject body = new JSONObject();
        body.put("email",         email);
        body.put("password",      password);
        body.put("phone",         fullPhone);
        body.put("date_of_birth", dateOfBirth);      // format "YYYY-MM-DD"
        body.put("gender",        gender);
        body.put("profile_image", base64Image);
        body.put("is_doctor",     isDoctor);
        // If doctor, send firstName/lastName; else send username
        if (isDoctor) {
            body.put("first_name", firstName);
            body.put("last_name",  lastName);
            body.put("username",   JSONObject.NULL);
        } else {
            body.put("username",   username);
            body.put("first_name", JSONObject.NULL);
            body.put("last_name",  JSONObject.NULL);
        }
        return body;
    }

    // launchVerifyActivity: start VerifySignupActivity with all extras
    // ... [your code]
    private void launchVerifyActivity(
            String email,
            String password,
            String phone,
            String dateOfBirth,
            boolean isDoctor,
            String username,
            String firstName,
            String lastName,
            String gender,
            Bitmap imageBitmap // << CHANGED!
    ) {
        Intent i = new Intent(SignupActivity.this, VerifySignupActivity.class);
        i.putExtra("email", email);
        i.putExtra("password", password);
        i.putExtra("phone", phone);
        i.putExtra("date_of_birth", dateOfBirth);
        i.putExtra("gender", gender);
        // i.putExtra("profile_image", base64Image); // REMOVE THIS!
        try {
            String imagePath = saveBitmapToCache(imageBitmap);
            i.putExtra("profile_image_path", imagePath);
        } catch (Exception e) {
            ToastUtil.show(this, "Image error: " + e.getMessage(), Toast.LENGTH_SHORT);
            return;
        }
        i.putExtra("is_doctor", isDoctor);
        i.putExtra("username", username);
        i.putExtra("first_name", firstName);
        i.putExtra("last_name", lastName);
        startActivity(i);
    }

    private String saveBitmapToCache(Bitmap bitmap) throws IOException {
        File tempFile = new File(getCacheDir(), "signup_img_" + System.currentTimeMillis() + ".jpg");
        FileOutputStream fos = new FileOutputStream(tempFile);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
        fos.close();
        return tempFile.getAbsolutePath();
    }

}