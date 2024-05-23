package com.example.braguia2.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.braguia2.model.App.App;
import com.example.braguia2.model.App.Contacts;
import com.example.braguia2.model.App.Partners;
import com.example.braguia2.model.App.Socials;
import com.example.braguia2.repositories.AppRepository;

import java.util.List;

public class AppViewModel extends AndroidViewModel {

    private final AppRepository repository;
    public LiveData<App> app;

    public AppViewModel(@NonNull Application application) {
        super(application);
        repository= new AppRepository(application);
        app = repository.getApp();
    }

    public LiveData<App> getApp() {
        return app;
    }

    public LiveData<List<Contacts>> getContacts() {
        return repository.getContacts();
    }

    public LiveData<List<Socials>> getSocials(){
        return repository.getSocials();
    }

    public LiveData<List<Partners>> getPartners(){
        return repository.getPartners();
    }
}
