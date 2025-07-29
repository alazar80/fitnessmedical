    package com.example.sql;

    import android.Manifest;
    import android.app.AlertDialog;
    import android.content.Context;
    import android.content.Intent;
    import android.content.pm.PackageManager;
    import android.os.Bundle;
    import android.telephony.SmsManager;
    import android.telephony.SubscriptionInfo;
    import android.telephony.SubscriptionManager;
    import android.view.View;
    import android.widget.Button;
    import android.widget.EditText;
    import android.widget.Toast;

    import androidx.appcompat.app.AppCompatActivity;
    import androidx.core.app.ActivityCompat;
    import androidx.recyclerview.widget.LinearLayoutManager;
    import androidx.recyclerview.widget.RecyclerView;

    import java.util.ArrayList;
    import java.util.List;

    public class sms extends AppCompatActivity {

        private EditText message;
        private Button send, clear;
        private RecyclerView messageRecyclerView;
        private MessageAdapter messageAdapter;
        private List<Message> messageList;  // Use your custom Message class

        private static final int SMS_PERMISSION_REQUEST_CODE = 101;


        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_sms);
            ThemeUtil.applyBackground(this, R.id.mainLayout);

            // Initialize UI components
            message = findViewById(R.id.message);
            send = findViewById(R.id.send);
            clear = findViewById(R.id.clear);
            messageRecyclerView = findViewById(R.id.messageRecyclerView);

            // Set up RecyclerView
            messageList = new ArrayList<>();
            messageAdapter = new MessageAdapter(messageList);
            messageRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            messageRecyclerView.setAdapter(messageAdapter);

            // Request permissions
            requestPermissions();
            String FIXED_PHONE_NUMBER = getIntent().getStringExtra("phone");

            send.setOnClickListener(v -> {
                String textMessage = message.getText().toString().trim();
                if (!textMessage.isEmpty()) {
                    sendSms(FIXED_PHONE_NUMBER, textMessage);
                } else {
                    ToastUtil.show(sms.this, "Please enter a message", 1/3);
                }
            });

            clear.setOnClickListener(v -> message.setText(""));
        }

        private void requestPermissions() {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.SEND_SMS,
                        Manifest.permission.READ_PHONE_STATE
                }, SMS_PERMISSION_REQUEST_CODE);
            }
        }

        private void sendSms(String phoneNumber, String textMessage) {
            SubscriptionManager subscriptionManager = (SubscriptionManager) getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);
            if (subscriptionManager == null) {
                ToastUtil.show(this, "Subscription Manager not available", 1/3);
                return;
            }

            List<SubscriptionInfo> subscriptionInfoList = subscriptionManager.getActiveSubscriptionInfoList();
            if (subscriptionInfoList == null || subscriptionInfoList.isEmpty()) {
                ToastUtil.show(this, "No active SIM cards available", 1/3);
                return;
            }

            if (subscriptionInfoList.size() == 1) {
                sendSmsWithSim(phoneNumber, textMessage, subscriptionInfoList.get(0).getSubscriptionId());
                return;
            }

            String[] simOptions = new String[subscriptionInfoList.size()];
            for (int i = 0; i < subscriptionInfoList.size(); i++) {
                simOptions[i] = "SIM " + (i + 1) + " - " + subscriptionInfoList.get(i).getCarrierName();
            }

            new AlertDialog.Builder(this)
                    .setTitle("Select SIM for SMS")
                    .setItems(simOptions, (dialog, which) -> {
                        int selectedSimId = subscriptionInfoList.get(which).getSubscriptionId();
                        sendSmsWithSim(phoneNumber, textMessage, selectedSimId);
                    })
                    .show();
        }

        private void sendSmsWithSim(String phoneNumber, String textMessage, int simId) {
            try {
                SmsManager smsManager = SmsManager.getSmsManagerForSubscriptionId(simId);
                smsManager.sendTextMessage(phoneNumber, null, textMessage, null, null);

                messageList.add(new Message(textMessage, true));  // Add sent message
                messageAdapter.notifyItemInserted(messageList.size() - 1);
                messageRecyclerView.scrollToPosition(messageList.size() - 1);

                ToastUtil.show(this, "Message sent!", 1/3);
                message.setText("");
            } catch (Exception e) {
                ToastUtil.show(this, "Failed to send: " + e.getMessage(), 1/3);
            }
        }
        @Override
        public void onBackPressed() {
            new AlertDialog.Builder(this)
                    .setTitle("Appointment")
                    .setMessage("Do you need an appointment?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        // Go to the appointment activity
                        Intent intent = new Intent(sms.this, BookAppointmentActivity.class); // Replace with your target activity
                        startActivity(intent);
                        finish(); // Optional: close this activity
                    })
                    .setNegativeButton("No", (dialog, which) -> {
                        super.onBackPressed(); // Just go back
                    })
                    .setCancelable(false)
                    .show();
        }


    }
