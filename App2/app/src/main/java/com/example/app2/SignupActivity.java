package com.example.app2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Signature;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class SignupActivity extends AppCompatActivity {

    EditText signupEmail, signupUsername, signupPassword;
    TextView loginRedirectText;
    Button signupButton;
    FirebaseDatabase database;
    DatabaseReference reference;
    boolean flag = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        signupUsername = findViewById(R.id.signup_username);
        signupEmail = findViewById(R.id.signup_email);
        signupPassword = findViewById(R.id.signup_password);
        signupButton = findViewById(R.id.signup_button);
        loginRedirectText = findViewById(R.id.loginRedirectText);

        // sign up button clicked
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // database connection
                database = FirebaseDatabase.getInstance();
                reference = database.getReference("users");

                // get user's information
                String username = signupUsername.getText().toString().trim();
                String email = signupEmail.getText().toString().trim();
                String password = signupPassword.getText().toString().trim();

                if (checkUser(username, email, password)) {
                    password = hashPassword(password);
                    HelperClass helperClass = new HelperClass(username, email, password);
                    reference.child(username).setValue(helperClass);
                    Toast.makeText(SignupActivity.this, "You have signup successfully", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
            }
        });

        // login text redirect clicked
        loginRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    // receives username, email and password the user inserted,
    // and check validation
    public boolean checkUser(String username, String email, String password) {
        if (username.isEmpty()) {
            signupUsername.setError("Username can't be empty");
            return false;
        }

        if (email.isEmpty()) {
            signupEmail.setError("Email can't be empty");
            return false;
        }

        if (password.isEmpty()) {
            signupPassword.setError("Password can't be empty");
            return false;
        }

        // checks username using regex
        String regex_username = "^(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{4,15}$";
        // It contains at least 4 characters and at most 15 characters.
        // It contains at least one upper case alphabet.
        // It contains at least one lower case alphabet.
        // It doesn't contain any white space.
        Pattern pattern_username = Pattern.compile(regex_username);
        Matcher matcher_username = pattern_username.matcher(username);
        String message_username = "";
        if (!matcher_username.matches()) {
            if (username.length() < 4 || username.length() > 15)
                message_username = "Username must be at least 4 characters and at most 15 characters";
            else if (!isUpper(username))
                message_username = "Username must contain at least one upper case alphabet";
            else if (!isLower(username))
                message_username = "Username must contain at least one lower case alphabet";
            else if (!username.equals(username.replaceAll(" ", "")))
                message_username = "Username must not contain any white space";
            else
                message_username = "Username not valid";
            signupUsername.setError(message_username);
            return false;
        }

        // checks email using regex
        String regex_email = "^(.+)@(.+)$";
        Pattern pattern_email = Pattern.compile(regex_email);
        Matcher matcher_email = pattern_email.matcher(email);
        if (!matcher_email.matches()) {
            signupEmail.setError("Email not valid");
            return false;
        }

        // check password using regex
        String regex_password = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{8,20}$";
        // It contains at least 8 characters and at most 20 characters.
        // It contains at least one digit.
        // It contains at least one upper case alphabet.
        // It contains at least one lower case alphabet.
        // It doesn't contain any white space.
        Pattern pattern_password = Pattern.compile(regex_password);
        Matcher matcher_password = pattern_password.matcher(password);
        String message_password = "";
        if (!matcher_password.matches()) {
            if (password.length() < 8 || password.length() > 20)
                message_password = "Password must be at least 8 characters and at most 20 characters";
            else if (!isUpper(password))
                message_password = "Password must contain at least one upper case alphabet";
            else if (!isLower(password))
                message_password = "Password must contain at least one lower case alphabet";
            else if (!isDigit(password))
                message_password = "Password must contain at least one digit";
            else if (!password.equals(password.replaceAll(" ", "")))
                message_password = "Password must not contain any white space";
            else
                message_password = "Password not valid";
            signupPassword.setError(message_password);
            return false;
        }

        if (flag) {
            signupUsername.setError("User exist, choose different username");
            return false;
        }

        return true;
    }

    public boolean isUpper(String str) {
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (Character.isUpperCase(c)) {
                return true;
            }
        }
        return false;
    }

    public boolean isLower(String str) {
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (Character.isLowerCase(c)) {
                return true;
            }
        }
        return false;
    }

    public boolean isDigit(String str) {
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (Character.isDigit(c)) {
                return true;
            }
        }
        return false;
    }

    private String hashPassword(String password) {
        // receives password and encrypt it with hash
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }


}