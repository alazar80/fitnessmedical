package com.example.sql;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class PaymentActivity extends AppCompatActivity {

    private Button btnPay;
    private WebView webView;
    private int userId;
    private String doctorId;
    private String txRef;  // single txRef for the entire flow

    public static final String CHAPA_INIT_URL    = "https://api.chapa.co/v1/transaction/initialize";
    public static final String CHAPA_VERIFY_BASE = "https://api.chapa.co/v1/transaction/verify/";
    public static final String CHAPA_SECRET_KEY  = "CHASECK_TEST-luIMRyqINQreokWdZKjE2gk0XBGpqUgN";
    public static final String CHAPA_PUBLIC_KEY  = "CHAPUBK_TEST-YuBwsyyAMZuEjr95ArfaJvO0rNLPEq52";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LocaleHelper.applySavedLocale(this);
        setContentView(R.layout.activity_payment);
        ThemeUtil.applyBackground(this, R.id.mainLayout);
        ThemeUtil.applyThemeFromPrefs(this);

        btnPay  = findViewById(R.id.btnPay);
        webView = findViewById(R.id.webView);

        userId   = getIntent().getIntExtra("user_id", -1);
        doctorId = getIntent().getStringExtra("doctor_id");

        btnPay.setOnClickListener(v -> fetchUserInfoAndStartPayment());
    }

    private void fetchUserInfoAndStartPayment() {
        String url = ApiConfig.GET_USER_INFO;
        JSONObject requestData = new JSONObject();
        try {
            requestData.put("user_id", userId);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                url,
                requestData,
                response -> {
                    try {
                        if (response.has("error")) {
                            String errorMessage = response.getString("error");
                            ToastUtil.show(this, "Server Error: " + errorMessage, Toast.LENGTH_LONG);
                            return;
                        }
                        String email     = response.getString("email");
                        String firstName = response.getString("name");
                        String lastName  = "User";
                        String phone     = response.getString("phone");
                        startPayment(email, firstName, lastName, phone);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        ToastUtil.show(this, "Failed to parse user info!", Toast.LENGTH_LONG);
                    }
                },
                error -> ToastUtil.show(this, "Failed to fetch user info!", Toast.LENGTH_LONG)
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }

//    private void startPayment(String email, String firstName, String lastName, String phoneNumber) {
//        try {
//            txRef = "txn_" + System.currentTimeMillis();  // generate once
//
//            JSONObject paymentData = new JSONObject();
//            paymentData.put("public_key", CHAPA_PUBLIC_KEY);
//            paymentData.put("amount", "10");
//            paymentData.put("currency", "ETB");
//            paymentData.put("email", email);
//            paymentData.put("first_name", firstName);
//            paymentData.put("last_name", lastName);
//            paymentData.put("phone_number", phoneNumber);
//            paymentData.put("tx_ref", txRef);
//            // Optional webhook if you have one:
//            paymentData.put("callback_url", "https://your-server.com/chapa-webhook");
//            // Use verify-endpoint itself as return_url:
////            paymentData.put("return_url", CHAPA_VERIFY_BASE + txRef);
//            // after you generate txRef:
//            String receiptUrl = "https://checkout.chapa.co/checkout/test-payment-receipt?tx_ref=" + txRef;
//            paymentData.put("return_url", receiptUrl);
//
//            paymentData.put("customization[title]", "Payment for Appointment");
//
//            JsonObjectRequest request = new JsonObjectRequest(
//                    Request.Method.POST,
//                    CHAPA_INIT_URL,
//                    paymentData,
//                    response -> {
//                        try {
//                            String checkoutUrl = response.getJSONObject("data").getString("checkout_url");
//
//                            // LOG “pending” immediately
//                            logPaymentToServer(txRef, "pending");
//                            openWebView(checkoutUrl);
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                            ToastUtil.show(this, "JSON Parse Error!", Toast.LENGTH_LONG);
//                        }
//                    },
//                    error -> {
//                        if (error.networkResponse != null && error.networkResponse.data != null) {
//                            String body = new String(error.networkResponse.data);
//                            ToastUtil.show(this, "Server Error: " + body, Toast.LENGTH_LONG);
//                        } else {
//                            ToastUtil.show(this, "Network Error: " + error.toString(), Toast.LENGTH_LONG);
//                        }
//                    }
//            ) {
//                @Override
//                public Map<String, String> getHeaders() {
//                    Map<String, String> headers = new HashMap<>();
//                    headers.put("Authorization", "Bearer " + CHAPA_SECRET_KEY);
//                    headers.put("Content-Type", "application/json");
//                    return headers;
//                }
//            };
//
//            request.setRetryPolicy(new DefaultRetryPolicy(
//                    10000,
//                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
//                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
//            ));
//
//            RequestQueue queue = Volley.newRequestQueue(this);
//            queue.add(request);
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//            ToastUtil.show(this, "JSON Error!", Toast.LENGTH_LONG);
//        }
//    }

//    private void openWebView(String url) {
//        btnPay.setVisibility(View.GONE);
//        webView.setVisibility(View.VISIBLE);
//
//        webView.getSettings().setJavaScriptEnabled(true);
//        webView.setWebViewClient(new WebViewClient() {
//            @Override
//            public void onPageFinished(WebView view, String pageUrl) {
//                // Once Chapa redirects to verify-url (i.e. https://api.chapa.co/v1/transaction/verify/{txRef}),
//                // treat that as “user finished payment” and call verify ourselves:
//                if (pageUrl.startsWith(CHAPA_VERIFY_BASE + txRef)) {
//                    verifyChapaPayment(txRef);
//                }
//            }
//        });
//        webView.loadUrl(url);
//    }



//    private void openWebView(String url) {
//        btnPay.setVisibility(View.GONE);
//        webView.setVisibility(View.VISIBLE);
//
//        webView.getSettings().setJavaScriptEnabled(true);
//        webView.setWebViewClient(new WebViewClient() {
//            @Override
//            public void onPageFinished(WebView view, String pageUrl) {
//                // 1) if Chapa just redirected back to our verify endpoint...
//                if (pageUrl.startsWith(CHAPA_VERIFY_BASE + txRef)) {
//                    verifyChapaPayment(txRef);
//                }
//                // 2) else if we're on the receipt page itself, give user time to grab it
//                else if (pageUrl.contains("/checkout/payment-receipt")) {
//                    // keep the receipt visible for 15 seconds, then finish()
//                    new Handler(Looper.getMainLooper()).postDelayed(
//                            () -> finish(),
//                            15_000 // 15 seconds; adjust as needed
//                    );
//                }
//            }
//        });
//        webView.loadUrl(url);
//    }

//    private void verifyChapaPayment(String txRefToVerify) {
//        String verifyUrl = CHAPA_VERIFY_BASE + txRefToVerify;
//        JsonObjectRequest req = new JsonObjectRequest(
//                Request.Method.GET,
//                verifyUrl,
//                null,
//                response -> {
//                    try {
//                        JSONObject data   = response.getJSONObject("data");
//                        String status     = data.getString("status"); // “success” or “failed”
//                        if ("success".equalsIgnoreCase(status)) {
//                            ToastUtil.show(this, "Payment Verified!", 1/3);
//                            assignDoctorToUser();
//                            logPaymentToServer(txRefToVerify, "success");
//
//
//
//                            // Optional: email the doctor
//                            String subject = "New Appointment Paid";
//                            String body    = "Hello Doctor,\n\nUser #" + userId +
//                                    " has completed payment and is now assigned to you.\n\n— Fitness Medical App";
//                            fetchDoctorEmailAndSend(subject, body);
//                            String receiptUrl = "https://checkout.chapa.co/checkout/payment-receipt?tx_ref=" + txRefToVerify;
//                            webView.loadUrl(receiptUrl);
//                        } else {
//                            ToastUtil.show(this, "Payment not successful: " + status, 1/3);
//                            logPaymentToServer(txRefToVerify, "failed");
//                        }
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                        ToastUtil.show(this, "Verify JSON error", 1/3);
//                    }
//                },
//                error -> ToastUtil.show(this, "Verify network error", 1/3)
//        ) {
//            @Override
//            public Map<String, String> getHeaders() {
//                Map<String, String> h = new HashMap<>();
//                h.put("Authorization", "Bearer " + CHAPA_SECRET_KEY);
//                return h;
//            }
//        };
//
//        Volley.newRequestQueue(this).add(req);
//    }
//    private void verifyChapaPayment(String txRefToVerify) {
//        String verifyUrl = CHAPA_VERIFY_BASE + txRefToVerify;
//        JsonObjectRequest req = new JsonObjectRequest(
//                Request.Method.GET,
//                verifyUrl,
//                null,
//                response -> {
//                    try {
//                        JSONObject data   = response.getJSONObject("data");
//                        String status     = data.getString("status"); // “success” or “failed”
//                        if ("success".equalsIgnoreCase(status)) {
//                            ToastUtil.show(this, "Payment Verified!", Toast.LENGTH_SHORT);
//                            assignDoctorToUser();
//                            logPaymentToServer(txRefToVerify, "success");
//                            String subject = "New Appointment Paid";
//                            String body    = "Hello Doctor,\n\nUser #" + userId +
//                                    " has completed payment and is now assigned to you.\n\n— Fitness Medical App";
//                            fetchDoctorEmailAndSend(subject, body);
//
//
//                            // Load the receipt page instead of finishing immediately:
//                            String receiptUrl = "https://checkout.chapa.co/checkout/payment-receipt?tx_ref=" + txRefToVerify;
//                            webView.loadUrl(receiptUrl);
//                        } else {
//                            ToastUtil.show(this, "Payment not successful: " + status, Toast.LENGTH_LONG);
//                            logPaymentToServer(txRefToVerify, "failed");
//                        }
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                        ToastUtil.show(this, "Verify JSON error", Toast.LENGTH_LONG);
//                    }
//                },
//                error -> ToastUtil.show(this, "Verify network error", Toast.LENGTH_LONG)
//        ) {
//            @Override
//            public Map<String, String> getHeaders() {
//                Map<String, String> h = new HashMap<>();
//                h.put("Authorization", "Bearer " + CHAPA_SECRET_KEY);
//                return h;
//            }
//        };
//
//        Volley.newRequestQueue(this).add(req);
//    }



//    private void assignDoctorToUser() {
//        String url = ApiConfig.ASSIGN_DOCTORS_URL;
//        StringRequest request = new StringRequest(
//                Request.Method.POST,
//                url,
//                response -> {
//                    ToastUtil.show(this, "Doctor Assigned Successfully!", Toast.LENGTH_LONG);
//                    finish();
//                },
//                error -> {
//                    if (error.networkResponse != null && error.networkResponse.data != null) {
//                        String body = new String(error.networkResponse.data);
//                        ToastUtil.show(this, "Assign Error: " + body, Toast.LENGTH_LONG);
//                    } else {
//                        ToastUtil.show(this, "Assign Failed: " + error.toString(), Toast.LENGTH_LONG);
//                    }
//                }
//        ) {
//            @Override
//            protected Map<String, String> getParams() {
//                Map<String, String> params = new HashMap<>();
//                params.put("user_id", String.valueOf(userId));
//                params.put("doctor_id", doctorId);
//                return params;
//            }
//        };
//
//        request.setRetryPolicy(new DefaultRetryPolicy(
//                10000,
//                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
//                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
//        ));
//
//        RequestQueue queue = Volley.newRequestQueue(this);
//        queue.add(request);
//    }
private void startPayment(String email, String firstName, String lastName, String phoneNumber) {
    try {
        txRef = "txn_" + System.currentTimeMillis();

        JSONObject paymentData = new JSONObject();
        paymentData.put("public_key", CHAPA_PUBLIC_KEY);
        paymentData.put("amount", "10");
        paymentData.put("currency", "ETB");
        paymentData.put("email", email);
        paymentData.put("first_name", firstName);
        paymentData.put("last_name", lastName);
        paymentData.put("phone_number", phoneNumber);
        paymentData.put("tx_ref", txRef);
        paymentData.put("callback_url", "https://your-server.com/chapa-webhook");

        // ← POINT return_url AT YOUR VERIFY ENDPOINT, not directly at the receipt page
        paymentData.put("return_url", CHAPA_VERIFY_BASE + txRef);

        paymentData.put("customization[title]", "Payment for Appointment");

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                CHAPA_INIT_URL,
                paymentData,
                response -> {
                    try {
                        String checkoutUrl = response.getJSONObject("data").getString("checkout_url");
                        logPaymentToServer(txRef, "pending");
                        openWebView(checkoutUrl);

                    } catch (JSONException e) {
                        e.printStackTrace();
                        ToastUtil.show(this, "JSON Parse Error!", Toast.LENGTH_LONG);
                    }
                },
                error -> {
                    if (error.networkResponse != null && error.networkResponse.data != null) {
                        String body = new String(error.networkResponse.data);
                        ToastUtil.show(this, "Server Error: " + body, Toast.LENGTH_LONG);
                    } else {
                        ToastUtil.show(this, "Network Error: " + error.toString(), Toast.LENGTH_LONG);
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + CHAPA_SECRET_KEY);
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);

    } catch (JSONException e) {
        e.printStackTrace();
        ToastUtil.show(this, "JSON Error!", Toast.LENGTH_LONG);
    }
}

    private void openWebView(String url) {
        btnPay.setVisibility(View.GONE);
        webView.setVisibility(View.VISIBLE);

        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String pageUrl) {
                // 1) if Chapa redirects back to your VERIFY endpoint…
                if (pageUrl.startsWith(CHAPA_VERIFY_BASE + txRef)) {
                    verifyChapaPayment(txRef);
                }
                // 2) if we’re on the TEST receipt page, keep it visible 15s then finish:
                else if (pageUrl.contains("/checkout/test-payment-receipt/")) {
                    new Handler(Looper.getMainLooper()).postDelayed(
                            () -> finish(),
                            15_000
                    );
                }
            }
        });
        webView.loadUrl(url);
    }

    private void verifyChapaPayment(String txRefToVerify) {
        String verifyUrl = CHAPA_VERIFY_BASE + txRefToVerify;
        JsonObjectRequest req = new JsonObjectRequest(
                Request.Method.GET,
                verifyUrl,
                null,
                response -> {
                    try {
                        JSONObject data   = response.getJSONObject("data");
                        String status     = data.getString("status"); // “success” or “failed”
                        if ("success".equalsIgnoreCase(status)) {
                            ToastUtil.show(this, "Payment Verified!", Toast.LENGTH_SHORT);
                            assignDoctorToUser();
                            logPaymentToServer(txRefToVerify, "success");

                            String subject = "New Appointment Paid";
                            String body    = "Hello Doctor,\n\nUser #" + userId +
                                    " has completed payment and is now assigned to you.\n\n— Fitness Medical App";
                            fetchDoctorEmailAndSend(subject, body);

                            // ← ONLY NOW load the TEST‐mode receipt page (no “?tx_ref=”)
                            String receiptUrl = "https://checkout.chapa.co/checkout/test-payment-receipt/" + txRefToVerify;
                            webView.loadUrl(receiptUrl);
//                            // ✅ Go to DietMedicalActivity
//                            Intent intent = new Intent(PaymentActivity.this, DietMedicalActivity.class);
//                            intent.putExtra("user_id", userId);
//                            startActivity(intent);
//                            finish(); // optional
                        } else {
                            ToastUtil.show(this, "Payment not successful: " + status, Toast.LENGTH_LONG);
                            logPaymentToServer(txRefToVerify, "failed");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        ToastUtil.show(this, "Verify JSON error", Toast.LENGTH_LONG);
                    }
                },
                error -> ToastUtil.show(this, "Verify network error", Toast.LENGTH_LONG)
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> h = new HashMap<>();
                h.put("Authorization", "Bearer " + CHAPA_SECRET_KEY);
                return h;
            }
        };

        Volley.newRequestQueue(this).add(req);
    }

    private void assignDoctorToUser() {
        String url = ApiConfig.ASSIGN_DOCTORS_URL;
        StringRequest request = new StringRequest(
                Request.Method.POST,
                url,
                response -> {
                    ToastUtil.show(this, "Doctor Assigned Successfully!", Toast.LENGTH_LONG);
                    // ← DO NOT call finish() here; let the receipt page stay up
                },
                error -> {
                    if (error.networkResponse != null && error.networkResponse.data != null) {
                        String body = new String(error.networkResponse.data);
                        ToastUtil.show(this, "Assign Error: " + body, Toast.LENGTH_LONG);
                    } else {
                        ToastUtil.show(this, "Assign Failed: " + error.toString(), Toast.LENGTH_LONG);
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", String.valueOf(userId));
                params.put("doctor_id", doctorId);
                return params;
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }
    private void logPaymentToServer(String txRefToLog, String status) {
        String url = ApiConfig.LOG_PAYMENT_URL;
        StringRequest request = new StringRequest(
                Request.Method.POST,
                url,
                response -> {
//                    ToastUtil.show(
//                            this,
//                            "Payment log (" + status + ") recorded!",
//                            Toast.LENGTH_SHORT
//                    ).show();
                },
                error -> ToastUtil.show(this, "Failed to log payment!", Toast.LENGTH_SHORT)
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", String.valueOf(userId));
                params.put("doctor_id", doctorId);
                params.put("tx_ref", txRefToLog);
                params.put("status", status);
                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }

    private void fetchDoctorEmailAndSend(final String subject, final String body) {
        String url = ApiConfig.SEND_EMAIL_URL;
        JSONObject req = new JSONObject();
        try {
            req.put("doctor_id", doctorId);
            req.put("subject", subject);
            req.put("body", body);
        } catch (JSONException ignored) { }

        JsonObjectRequest emailReq = new JsonObjectRequest(
                Request.Method.POST,
                url,
                req,
                response -> {
                    if (response.optBoolean("success", false)) {
                        ToastUtil.show(this, "Email sent to doctor!", Toast.LENGTH_SHORT);
                    } else {
                        String err = response.optString("error", "Unknown error");
                        ToastUtil.show(this, "Email error: " + err, Toast.LENGTH_LONG);
                    }
                },
                error -> ToastUtil.show(this, "Network error sending email", Toast.LENGTH_LONG)
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> h = new HashMap<>();
                h.put("Content-Type", "application/json");
                return h;
            }
        };

        Volley.newRequestQueue(this).add(emailReq);
    }
}
