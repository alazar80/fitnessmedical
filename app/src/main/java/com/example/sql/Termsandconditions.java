package com.example.sql;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.os.Bundle;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Termsandconditions extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_termsandconditions);
        ThemeUtil.applyBackground(this, R.id.mainLayout);
        ThemeUtil.applyThemeFromPrefs(this);
        // Initialize the ScrollView and TextView for displaying the security policy
        TextView securityPolicyTextView = findViewById(R.id.securityPolicyTextView);

        // Define the security policy text
        String securityPolicyText = "Security Policy for [Your Android Application Name]\n\n" +
                "_Last updated: [Insert Date]_\n\n" +
                "---\n\n" +
                "## 1. Application Overview\n\n" +
                "[Your Android Application Name] is designed to deliver essential features and services directly to general users. " +
                "This app is publicly accessible and intended for users of Android devices running versions 12 to 15.\n\n" +
                "Audience: General Public\n" +
                "Availability: Publicly Accessible (not currently on the Google Play Store)\n\n" +
                "---\n\n" +
                "## 2. Data Handling and Privacy\n\n" +
                "### Data Collected:\n" +
                "- Personal information (e.g., name, email, or other personally identifiable information as needed for app functionality).\n\n" +
                "### Sensitive Data:\n" +
                "- The application collects sensitive personal information and securely stores it locally on the user’s device.\n\n" +
                "### Data Storage:\n" +
                "- All sensitive personal data is securely stored locally within the device’s encrypted storage system.\n\n" +
                "---\n\n" +
                "## 3. Authentication and Authorization\n\n" +
                "### Authentication Method:\n" +
                "- Users authenticate themselves using email and password-based login systems.\n\n" +
                "### Authorization:\n" +
                "- Currently, there are no role-based restrictions; authenticated users receive uniform access to application features.\n\n" +
                "---\n\n" +
                "## 4. Network and API Security\n\n" +
                "### Server Communication:\n" +
                "- The app connects exclusively to private backend servers.\n\n" +
                "### Encryption:\n" +
                "- All communications between the app and servers utilize HTTPS protocol to ensure secure data transmission.\n\n" +
                "### API Security:\n" +
                "- Access to APIs is strictly controlled through token-based authentication mechanisms.\n\n" +
                "---\n\n" +
                "## 5. Third-party Services\n\n" +
                "The application integrates third-party ad networks to display advertisements within the app. " +
                "These services may independently collect and process user data in accordance with their respective privacy policies.\n\n" +
                "---\n\n" +
                "## 6. Platform and Deployment Security\n\n" +
                "- Platform Compatibility: Android devices running versions 12 through 15.\n" +
                "- Deployment Security: Application signing is enforced to ensure authenticity and integrity of deployments.\n\n" +
                "---\n\n" +
                "## 7. Security Features Implemented\n\n" +
                "To safeguard user data and maintain application integrity, the following measures have been implemented:\n\n" +
                "- Data Encryption: Sensitive data stored locally is encrypted using robust encryption methods.\n" +
                "- Root/Jailbreak Detection: Detection mechanisms are in place to prevent the use of the app on compromised or rooted devices.\n" +
                "- Code Obfuscation: Application code is obfuscated via ProGuard to prevent reverse-engineering and protect proprietary information.\n" +
                "- Secure Storage: Sensitive information is securely stored using Android’s EncryptedSharedPreferences.\n\n" +
                "---\n\n" +
                "## Policy Updates and Notifications\n\n" +
                "We reserve the right to update this security policy as needed. Users will be notified of significant changes through appropriate channels within the application or via email.\n\n" +
                "---\n\n" +
                "## Contact Information\n\n" +
                "For any questions, concerns, or feedback regarding the security practices of this application, please contact us at:\n\n" +
                "- Email: [your-support-email@example.com]\n" +
                "- Website: [your-website.com/contact]\n\n" +
                "---\n\n" +
                "Thank you for trusting [Your Android Application Name]. Your security and privacy remain our top priority.\n\n" +
                "---";

        // Set the policy text to the TextView
        securityPolicyTextView.setText(securityPolicyText);
    }
}
