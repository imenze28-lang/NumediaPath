package com.example.numediapath.data.repository;

import android.app.Application;
import androidx.lifecycle.LiveData;
import com.example.numediapath.data.local.AppDatabase;
import com.example.numediapath.data.local.UserDao;
import com.example.numediapath.data.model.UserProfile;

public class UserRepository {

    private final UserDao userDao;
    private final LiveData<UserProfile> userProfile;

    public UserRepository(Application application) {
        // On utilise la méthode de ton AppDatabase pour récupérer l'instance
        AppDatabase db = AppDatabase.getInstance(application);
        userDao = db.userDao();
        userProfile = userDao.getUserProfile();
    }

    public LiveData<UserProfile> getUserProfile() {
        return userProfile;
    }

    // Sauvegarder (Créer ou Remplacer) le profil
    public void saveProfile(UserProfile profile) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            userDao.saveProfile(profile);
        });
    }

    // Mettre à jour un profil existant
    public void update(UserProfile profile) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            userDao.updateUserProfile(profile);
        });
    }

    // Insérer un nouveau profil (si nécessaire séparément)
    public void insert(UserProfile profile) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            userDao.insert(profile);
        });
    }
}