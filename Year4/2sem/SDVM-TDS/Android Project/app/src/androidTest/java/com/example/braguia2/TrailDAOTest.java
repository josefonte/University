package com.example.braguia2;


import static androidx.test.internal.runner.junit4.statement.UiThreadStatement.runOnUiThread;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.braguia2.model.Trails.TrailDAO;
import com.example.braguia2.repositories.TrailRepository;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(AndroidJUnit4.class)
public class TrailDAOTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();
    private TrailRepository trailRepository;
    private TrailDAO trailDAO;

    //Este before é útil porque assim evita termos que criar este pedaço de código em todos os testes que vamos fazer
    //Cada vez que corremos um teste, este before corre antes
    @Before
    public void setup() throws Throwable {
        runOnUiThread(() -> {
            trailRepository = new TrailRepository(ApplicationProvider.getApplicationContext());
            trailDAO = trailRepository.trailDAO;
        });
    }

    @Test
    public void getTrailsTeste(){

    }






}
