package com.example.braguia2.model.App;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface AppDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(App app);

    @Query("SELECT DISTINCT * FROM app")
    LiveData<App> getApp();

    @Query("SELECT * FROM contacts WHERE contact_app = :appName")
    LiveData<List<Contacts>> getContacts(String appName);

    @Query("SELECT * FROM partners WHERE partner_app = :appName")
    LiveData<List<Partners>> getPartners(String appName);

    @Query("SELECT * FROM socials WHERE social_app = :appName")
    LiveData<List<Socials>> getSocials(String appName);

    @Query("DELETE FROM app")
    void deleteAll();
}