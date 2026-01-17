package com.example.numediapath.ui.view.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.numediapath.R;
import com.example.numediapath.data.remote.AuthManager;
import com.example.numediapath.ui.view.MainActivity;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnLogin, btnAnonymous;
    private TextView tvRegister; // Déclaré ici
    private ProgressBar progressBar;
    private AuthManager authManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        authManager = new AuthManager();

        if (authManager.getCurrentUser() != null) {
            startMainActivity();
        }

        initViews();
        setupListeners();
    }

    private void initViews() {
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
        btnAnonymous = findViewById(R.id.btn_anonymous);
        tvRegister = findViewById(R.id.tv_goto_register); // Initialisé ici
        progressBar = findViewById(R.id.progress_bar);
    }

    private void setupListeners() {
        // 1. Connexion Classique
        btnLogin.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String pass = etPassword.getText().toString().trim();
            if (email.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
                return;
            }

            setLoading(true);
            authManager.login(email, pass, new AuthManager.AuthCallback() {
                @Override
                public void onSuccess(FirebaseUser user) { startMainActivity(); }

                @Override
                public void onError(String message) {
                    setLoading(false);
                    Toast.makeText(LoginActivity.this, "Échec : " + message, Toast.LENGTH_LONG).show();
                }
            });
        });

        // 2. Mode Invité
        btnAnonymous.setOnClickListener(v -> {
            setLoading(true);
            authManager.loginAnonymously(new AuthManager.AuthCallback() {
                @Override
                public void onSuccess(FirebaseUser user) {
                    Toast.makeText(LoginActivity.this, "Mode invité activé", Toast.LENGTH_SHORT).show();
                    startMainActivity();
                }

                @Override
                public void onError(String message) {
                    setLoading(false);
                    Toast.makeText(LoginActivity.this, "Erreur : " + message, Toast.LENGTH_SHORT).show();
                }
            });
        });

        // ✅ LOGIQUE AJOUTÉE : Navigation vers l'inscription
        tvRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    private void setLoading(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        btnLogin.setEnabled(!loading);
        btnAnonymous.setEnabled(!loading);
    }

    private void startMainActivity() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}