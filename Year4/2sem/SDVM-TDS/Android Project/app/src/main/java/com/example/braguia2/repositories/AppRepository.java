package com.example.braguia2.repositories;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import com.example.braguia2.model.App.App;
import com.example.braguia2.model.App.AppAPI;
import com.example.braguia2.model.App.AppDAO;
import com.example.braguia2.model.App.Contacts;
import com.example.braguia2.model.App.Partners;
import com.example.braguia2.model.App.Socials;
import com.example.braguia2.model.GuideDatabase;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AppRepository {

    public AppDAO appDAO;
    public MediatorLiveData<App>app;
    private final GuideDatabase database;

    public AppRepository(Application application){
        database = GuideDatabase.getInstance(application);
        appDAO = database.appDAO();
        app = new MediatorLiveData<>();
        app.addSource(
                appDAO.getApp(), localapp -> {
                    if (localapp != null) {
                        app.setValue(localapp);
                    } else {
                        try {
                            makeRequest();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
        );
    }


    public void insert(App app){
        new InsertAsyncTask(appDAO).execute(app);
    }
    private void makeRequest() throws IOException {
        Retrofit retrofit=new Retrofit.Builder()
                .baseUrl("http://764f-193-137-92-72.ngrok-free.app/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        AppAPI api = retrofit.create(AppAPI.class);
        Call<List<App>> call = api.getApp();
        call.enqueue(new retrofit2.Callback<List<App>>() {
            @Override
            public void onResponse(Call<List<App>> call, Response<List<App>> response) {
                if(response.isSuccessful()) {
                    insert(response.body().get(0));
                }
                else{
                    Log.e("main", "onFailure: "+response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<List<App>> call, Throwable t) {
                Log.e("main", "onFailure: " + t.getMessage());
                Log.e("main", "message: "+ t.getCause());
            }
        });
    }

    public LiveData<App> getApp(){
        return app;
    }

    public LiveData<List<Contacts>> getContacts(){
        return appDAO.getContacts("BraGuia");
    }
    public LiveData<List<Partners>> getPartners(){
        return appDAO.getPartners("BraGuia");
    }

    public LiveData<List<Socials>> getSocials(){
        return appDAO.getSocials("BraGuia");
    }


    private static class InsertAsyncTask extends AsyncTask<App,Void,Void> {
        private final AppDAO appDAO;

        public InsertAsyncTask(AppDAO catDao) {
            this.appDAO=catDao;
        }

        @Override
        protected Void doInBackground(App... apps) { //TODO isto não está certo acho
            appDAO.insert(apps[0]);
            return null;
        }
    }

}
