package com.example.numediapath.ui.view.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.example.numediapath.R;
import com.example.numediapath.data.remote.AuthManager;
import com.example.numediapath.ui.view.MainActivity;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnRegister;
    private ProgressBar progressBar;
    private AuthManager authManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        authManager = new AuthManager();
        initViews();

        btnRegister.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String pass = etPassword.getText().toString().trim();

            if (email.isEmpty() || pass.length() < 6) {
                Toast.makeText(this, "Email invalide ou mot de passe trop court (min 6 chars)", Toast.LENGTH_SHORT).show();
                return;
            }

            progressBar.setVisibility(View.VISIBLE);
            authManager.register(email, pass, new AuthManager.AuthCallback() {
                @Override
                public void onSuccess(FirebaseUser user) {
                    Toast.makeText(RegisterActivity.this, "Bienvenue " + email + " !", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                    finishAffinity(); // Ferme tout le flux d'auth
                }

                @Override
                public void onError(String message) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(RegisterActivity.this, "Erreur : " + message, Toast.LENGTH_LONG).show();
                }
            });
        });
    }

    private void initViews() {
        etEmail = findViewById(R.id.et_email_reg);
        etPassword = findViewById(R.id.et_password_reg);
        btnRegister = findViewById(R.id.btn_register_submit);
        progressBar = findViewById(R.id.progress_reg);
    }
}