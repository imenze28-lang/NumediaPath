package com.example.numediapath.data.remote;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AuthManager {
    private final FirebaseAuth mAuth;

    public AuthManager() {
        this.mAuth = FirebaseAuth.getInstance();
    }

    // Connexion classique
    public void login(String email, String password, AuthCallback callback) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) callback.onSuccess(mAuth.getCurrentUser());
                    else callback.onError(task.getException().getMessage());
                });
    }

    // Connexion ANONYME (Mode Invité)
    public void loginAnonymously(AuthCallback callback) {
        mAuth.signInAnonymously()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) callback.onSuccess(mAuth.getCurrentUser());
                    else callback.onError(task.getException().getMessage());
                });
    }

    public void register(String email, String password, AuthCallback callback) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) callback.onSuccess(mAuth.getCurrentUser());
                    else callback.onError(task.getException().getMessage());
                });
    }

    public FirebaseUser getCurrentUser() {
        return mAuth.getCurrentUser();
    }

    public void signOut() {
        mAuth.signOut();
    }

    public interface AuthCallback {
        void onSuccess(FirebaseUser user);
        void onError(String message);
    }
}