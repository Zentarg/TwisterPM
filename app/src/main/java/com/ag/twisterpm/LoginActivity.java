package com.ag.twisterpm;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import static com.ag.twisterpm.utils.Constants.*;

import androidx.annotation.NonNull;

import com.ag.twisterpm.utils.UtilMethods;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private EditText email;
    private EditText password;

    private EditText registerEmail;
    private EditText registerPassword;
    private EditText registerDisplayName;

    private LinearLayout loginForm;
    private LinearLayout registerForm;

    private TextView loginViewBTN;
    private TextView registerViewBTN;

    private TextView errorTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);

        registerEmail = findViewById(R.id.registerEmail);
        registerPassword = findViewById(R.id.registerPassword);
        registerDisplayName = findViewById(R.id.displayName);

        loginForm = findViewById(R.id.loginForm);
        registerForm = findViewById(R.id.registerForm);

        loginViewBTN = findViewById(R.id.loginViewBTN);
        registerViewBTN = findViewById(R.id.registerViewBTN);

        errorTextView = findViewById(R.id.errorTextView);

        mAuth = FirebaseAuth.getInstance();

        loginViewBTN.setOnClickListener(v -> {
            registerForm.setVisibility(View.GONE);
            loginForm.setVisibility(View.VISIBLE);
            ClearErrorText();
        });

        registerViewBTN.setOnClickListener(v -> {
            loginForm.setVisibility(View.GONE);
            registerForm.setVisibility(View.VISIBLE);
            ClearErrorText();
        });

    }

    private void ClearErrorText() {
        errorTextView.setVisibility(View.GONE);
        errorTextView.setText("");
    }

    private void SetErrorText(String error) {
        errorTextView.setVisibility(View.VISIBLE);
        errorTextView.setText(error);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (mAuth.getCurrentUser() != null) {
            HomeIntent();
        }

    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {

        outState.putString(STATE_EMAIL, email.toString());
        outState.putString(STATE_PASSWORD, password.toString());

        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        email.setText(savedInstanceState.getString(STATE_EMAIL));
        password.setText(savedInstanceState.getString(STATE_PASSWORD));
        super.onRestoreInstanceState(savedInstanceState);
    }

    private void HomeIntent() {
        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        this.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    public void Login(View view) {
        if (!Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches()) {
            email.setError("Please enter a valid email.");
            return;
        }
        if (password.getText().length() == 0) {
            password.setError("Please enter a password.");
            return;
        }
        mAuth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                .addOnSuccessListener(this, authResult -> {
                    Log.d(LOGTAG, "signInWithEmail : success");
                    HomeIntent();
                }).addOnFailureListener(this, e -> {
                    Log.w(LOGTAG, "signInWithEmail : failure", e);
                    Toast.makeText(LoginActivity.this, "Authentication Failed.", Toast.LENGTH_SHORT).show();
                    SetErrorText("Authentication Failed: \n" + e.getMessage());
                });
    }

    public void Register(View view) {

        if (!Patterns.EMAIL_ADDRESS.matcher(registerEmail.getText().toString()).matches()) {
            registerEmail.setError("Please enter a valid email.");
            return;
        }
        if (registerDisplayName.getText().length() < 3) {
            registerDisplayName.setError("Please enter a valid email.");
            return;
        }
        if (registerPassword.getText().length() < 6) {
            registerPassword.setError("Password should be atleast 6 characters.");
            return;
        }

        mAuth.createUserWithEmailAndPassword(registerEmail.getText().toString(), registerPassword.getText().toString())
                .addOnSuccessListener(this, authResult -> {
                    Log.d(LOGTAG, "createUserWIthEmail : success");
                    Toast.makeText(LoginActivity.this, "Successfully Created User.", Toast.LENGTH_SHORT).show();
                    FirebaseUser u = authResult.getUser();

                    UserProfileChangeRequest request = new UserProfileChangeRequest.Builder().setDisplayName(registerDisplayName.getText().toString()).build();

                    u.updateProfile(request);
                }).addOnFailureListener(this, e -> {
                    Log.w(LOGTAG, "createUserWithEmail : failure", e);
                    Toast.makeText(LoginActivity.this, "Registration Failed.", Toast.LENGTH_SHORT).show();
                    SetErrorText("Registration error: \n" + e.getMessage());
                });
    }
}