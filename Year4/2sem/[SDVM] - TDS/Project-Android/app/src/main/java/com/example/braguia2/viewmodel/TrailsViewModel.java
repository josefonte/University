package com.example.braguia2.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.braguia2.model.Trails.Conteudo;
import com.example.braguia2.model.Trails.Ponto;
import com.example.braguia2.repositories.TrailRepository;
import com.example.braguia2.model.Trails.Trail;
import java.util.List;

public class TrailsViewModel extends AndroidViewModel {

    private TrailRepository repository;

    public LiveData<List<Trail>> trails;

    public TrailsViewModel(@NonNull Application application) {
        super(application);
        repository= new TrailRepository(application);
        trails = repository.getAllTrails();
    }

    public LiveData<Trail> getTrailById(int id) {
        return repository.getTrailById(id);
    }
    public LiveData<Ponto> getPontoById(int id) {return repository.getPontoById(id);}

    public LiveData<List<Trail>> getAllTrails() {
        return trails;
    }

    public LiveData<List<Ponto>> getAllPontos() {
        return repository.getAllPontosInTrails();
    }

    public LiveData<List<Ponto>> getAllPontosOfTrail(int id) {
        return repository.getAllPontosOfTrail(id);
    }

    public LiveData<List<Ponto>> getAllPontosComImagens() {
        return repository.getAllPontosComImagensInTrails();
    }
    public LiveData<List<Ponto>> getAllPontosComImagensInTrail(int id) {
        return repository.getAllPontosComImagensInATrail(id);
    }

    public LiveData<List<Conteudo>> getAllContentTrail(int id){
        return repository.getAllContentTrail(id);
    }

    public LiveData<List<Trail>> getTrailsByIds(List<Integer> ids){
        return repository.getTrailsByIds(ids);
    }

    public LiveData<List<Conteudo>> getAllContentPonto(int id) {
        return repository.getAllContentPonto(id);
    }
}