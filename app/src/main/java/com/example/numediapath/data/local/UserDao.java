package com.example.numediapath.data.local;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import com.example.numediapath.data.model.UserProfile;

@Dao
public interface UserDao {

    // ✅ OnConflictStrategy.REPLACE : Si l'ID existe déjà, il remplace (Update).
    // Sinon, il crée (Insert). C'est la méthode la plus sûre.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void saveProfile(UserProfile profile);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(UserProfile profile);

    @Update
    void updateUserProfile(UserProfile profile);

    @Query("SELECT * FROM user_profile LIMIT 1")
    LiveData<UserProfile> getUserProfile();
}