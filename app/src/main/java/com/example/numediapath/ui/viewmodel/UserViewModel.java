package com.example.numediapath.ui.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.numediapath.data.model.UserProfile;
import com.example.numediapath.data.repository.UserRepository;
import com.google.firebase.auth.FirebaseAuth;

public class UserViewModel extends AndroidViewModel {

    private final UserRepository repository;
    private final LiveData<UserProfile> userProfile;

    public UserViewModel(@NonNull Application application) {
        super(application);
        repository = new UserRepository(application);
        userProfile = repository.getUserProfile();
    }

    public LiveData<UserProfile> getUserProfile() {
        return userProfile;
    }

    // ✅ Sauvegarde ou mise à jour complète
    public void saveProfile(UserProfile profile) {
        repository.saveProfile(profile);
    }

    // ✅ Mise à jour spécifique de l'image de profil
    public void updateProfileImage(String newImageUrl) {
        UserProfile currentProfile = userProfile.getValue();

        if (currentProfile != null) {
            // Cas 1 : Le profil existe déjà dans Room
            currentProfile.setProfileImageUrl(newImageUrl);
            repository.update(currentProfile); // Correction du nom : repository au lieu de userRepository
        } else {
            // Cas 2 : La base est vide (ex: premier lancement)
            // On crée un profil minimal avec l'ID Firebase pour pouvoir sauvegarder l'image
            String uid = FirebaseAuth.getInstance().getUid();
            if (uid != null) {
                UserProfile newProfile = new UserProfile();
                newProfile.setId(uid);
                newProfile.setName("Explorateur"); // Nom par défaut
                newProfile.setProfileImageUrl(newImageUrl);
                repository.saveProfile(newProfile);
            }
        }
    }
}