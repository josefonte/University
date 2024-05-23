package com.example.braguia2.model;


import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.braguia2.model.App.App;
import com.example.braguia2.model.App.AppDAO;
import com.example.braguia2.model.App.Contacts;
import com.example.braguia2.model.App.Partners;
import com.example.braguia2.model.App.Socials;
import com.example.braguia2.model.Trails.Conteudo;
import com.example.braguia2.model.Trails.Edge;
import com.example.braguia2.model.Trails.Ponto;
import com.example.braguia2.model.Trails.RelPin;
import com.example.braguia2.model.Trails.Trail;
import com.example.braguia2.model.Trails.TrailDAO;
import com.example.braguia2.model.User.User;
import com.example.braguia2.model.User.UserDAO;


@Database(entities = {Trail.class, User.class, App.class, Contacts.class, Partners.class, Socials.class, Edge.class, Ponto.class, Conteudo.class, RelPin.class}, version = 970)
public abstract class GuideDatabase extends RoomDatabase {
    private static final String DATABASE_NAME = "BraGuide";

    public abstract TrailDAO trailDAO();
    public abstract UserDAO userDAO();
    public abstract AppDAO appDAO();

    public static volatile GuideDatabase INSTANCE = null;

    public static GuideDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (GuideDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context, GuideDatabase.class, DATABASE_NAME)
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    public static Callback callback = new Callback() {
        @Override
        public void onOpen(@NonNull SupportSQLiteDatabase db) {
            super.onOpen(db);
            new PopulateDbAsyn(INSTANCE);
        }
    };

    static class  PopulateDbAsyn extends AsyncTask<Void,Void,Void>{

        private TrailDAO traildao;
        private UserDAO userDAO;
        private AppDAO appDAO;



        public PopulateDbAsyn(GuideDatabase catDatabase) {
            traildao = catDatabase.trailDAO();
            userDAO = catDatabase.userDAO();
            appDAO = catDatabase.appDAO();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            traildao.deleteAll();
            appDAO.deleteAll();
            userDAO.deleteAll();
            return null;
        }
    }
}