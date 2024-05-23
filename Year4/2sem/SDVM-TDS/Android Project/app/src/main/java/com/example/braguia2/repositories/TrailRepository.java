package com.example.braguia2.repositories;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.example.braguia2.model.GuideDatabase;
import com.example.braguia2.model.Trails.Conteudo;
import com.example.braguia2.model.Trails.Edge;
import com.example.braguia2.model.Trails.Ponto;
import com.example.braguia2.model.Trails.Trail;
import com.example.braguia2.model.Trails.TrailAPI;
import com.example.braguia2.model.Trails.TrailDAO;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TrailRepository {

    public TrailDAO trailDAO;
    public MediatorLiveData<List<Trail>> allTrails;

    public TrailRepository(Application application){
        GuideDatabase database = GuideDatabase.getInstance(application);
        trailDAO = database.trailDAO();
        allTrails = new MediatorLiveData<>();
        allTrails.addSource(
                trailDAO.getTrails(), localTrails -> {
                    // TODO: ADD cache validation logic
                    if (localTrails != null && localTrails.size() > 0) {
                        allTrails.setValue(localTrails);
                    } else {
                        makeRequest();
                    }
                }
        );
    }

    public void insert(List<Trail> trails){
        new InsertAsyncTask(trailDAO).execute(trails);
    }

    private void makeRequest() {
        Retrofit retrofit=new Retrofit.Builder()
                .baseUrl("http://764f-193-137-92-72.ngrok-free.app/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        TrailAPI api = retrofit.create(TrailAPI.class);
        Call<List<Trail>> call = api.getTrails();
        call.enqueue(new retrofit2.Callback<List<Trail>>() {
            @Override
            public void onResponse(Call<List<Trail>> call, Response<List<Trail>> response) {
                if(response.isSuccessful()) {
                    insert(response.body());
                }
                else{
                    Log.e("main", "onFailure: "+response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<List<Trail>> call, Throwable t) {
                Log.e("main", "onFailure: " + t.getMessage());
            }
        });
    }

    public LiveData<List<Trail>> getAllTrails(){
        return allTrails;
    }

    public LiveData<Trail> getTrailById(int id){
        return trailDAO.getTrailById(id);
    }

    public LiveData<List<Ponto>> getAllPontosInTrails() {
        MediatorLiveData<List<Ponto>> allPontos = new MediatorLiveData<>();
        LiveData<List<Trail>> trails = getAllTrails();

        allPontos.addSource(trails, localTrails -> {
            Set<Ponto> uniquePontos = new HashSet<>();
            for (Trail trail : localTrails) {
                List<Edge> edges = trail.getEdges();
                for (Edge edge : edges) {
                    uniquePontos.add(edge.getEdgeStart());
                    uniquePontos.add(edge.getEdgeEnd());
                }
            }
            allPontos.setValue(new ArrayList<>(uniquePontos));
        });

        return allPontos;
    }

    public LiveData<List<Ponto>> getAllPontosOfTrail(int id) {
        MediatorLiveData<List<Ponto>> allPontos = new MediatorLiveData<>();
        LiveData<List<Trail>> trails = getAllTrails();

        allPontos.addSource(trails, localTrails -> {
            Set<String> pontoIds = new HashSet<>();
            Set<Ponto> uniquePontos = new HashSet<>();
            for (Trail trail : localTrails) {
                if (Integer.parseInt(trail.getId()) == id) {
                    List<Edge> edges = trail.getEdges();
                    for (Edge edge : edges) {
                        Ponto edgeStart = edge.getEdgeStart();
                        Ponto edgeEnd = edge.getEdgeEnd();
                        if (!pontoIds.contains(edgeStart.getId())) {
                            uniquePontos.add(edgeStart);
                            pontoIds.add(edgeStart.getId());
                        }
                        if (!pontoIds.contains(edgeEnd.getId())) {
                            uniquePontos.add(edgeEnd);
                            pontoIds.add(edgeEnd.getId());
                        }
                    }
                }
            }
            allPontos.setValue(new ArrayList<>(uniquePontos));
        });

        return allPontos;
    }

    public LiveData<List<Ponto>> getAllPontosComImagensInTrails(){
        MediatorLiveData<List<Ponto>> allPontos = new MediatorLiveData<>();
        LiveData<List<Trail>> trails = getAllTrails();

        allPontos.addSource(trails, localTrails -> {
            Set<String> pontoIds = new HashSet<>();
            List<Ponto> uniquePontos = new ArrayList<>();
            for (Trail trail : localTrails) {
                    List<Edge> edges = trail.getEdges();
                    for (Edge edge : edges) {
                        Ponto edgeStart = edge.getEdgeStart();
                        Ponto edgeEnd = edge.getEdgeEnd();
                        if (edgeStart.getSingleImage() != null && !pontoIds.contains(edgeStart.getId())) {
                            uniquePontos.add(edgeStart);
                            pontoIds.add(edgeStart.getId());
                        }
                        if (edgeEnd.getSingleImage() != null && !pontoIds.contains(edgeEnd.getId())) {
                            uniquePontos.add(edgeEnd);
                            pontoIds.add(edgeEnd.getId());
                        }
                    }
            }
            allPontos.setValue(uniquePontos);
        });

        return allPontos;
    }

    public LiveData<List<Ponto>> getAllPontosComImagensInATrail(int id){
        MediatorLiveData<List<Ponto>> allPontos = new MediatorLiveData<>();
        LiveData<List<Trail>> trails = getAllTrails();

        allPontos.addSource(trails, localTrails -> {
            Set<String> pontoIds = new HashSet<>();
            List<Ponto> uniquePontos = new ArrayList<>();
            for (Trail trail : localTrails) {
                if (Integer.parseInt(trail.getId()) == id) {
                    List<Edge> edges = trail.getEdges();
                    for (Edge edge : edges) {
                        Ponto edgeStart = edge.getEdgeStart();
                        Ponto edgeEnd = edge.getEdgeEnd();
                        if (edgeStart.getSingleImage() != null && !pontoIds.contains(edgeStart.getId())) {
                            uniquePontos.add(edgeStart);
                            pontoIds.add(edgeStart.getId());
                        }
                        if (edgeEnd.getSingleImage() != null && !pontoIds.contains(edgeEnd.getId())) {
                            uniquePontos.add(edgeEnd);
                            pontoIds.add(edgeEnd.getId());
                        }
                    }
                }
            }
            allPontos.setValue(uniquePontos);
        });

        return allPontos;
    }
    public LiveData<Ponto> getPontoById(int id) {
        MutableLiveData<Ponto> result = new MutableLiveData<>();

        LiveData<List<Ponto>> pontosLiveData = getAllPontosInTrails();
        pontosLiveData.observeForever(new Observer<List<Ponto>>() {
            @Override
            public void onChanged(List<Ponto> pontos) {
                for (Ponto ponto : pontos) {
                    if (Integer.parseInt(ponto.getId()) == id) {
                        result.setValue(ponto);
                        break;
                    }
                }
            }
        });
        return result;
    }

    public LiveData<List<Conteudo>> getAllContentTrail(int id){
        MediatorLiveData<List<Conteudo>> allCont = new MediatorLiveData<>();
        LiveData<List<Trail>> trails = getAllTrails();

        allCont.addSource(trails, localTrails -> {
            List<Conteudo> contents = new ArrayList<>();

            for (Trail trail : localTrails) {
                if (Integer.parseInt(trail.getId()) == id) {
                    List<Edge> edges = trail.getEdges();
                    for (Edge edge : edges) {
                        List<Conteudo> edgeStartC = edge.getEdgeStart().getConteudo();
                        List<Conteudo> edgeEndC = edge.getEdgeEnd().getConteudo();
                        if (edgeStartC != null){
                            contents.addAll(edgeStartC);
                        }
                        if (edgeEndC != null){
                            contents.addAll(edgeEndC);
                        }

                    }
                }
            }
            allCont.setValue(contents);
        });

        return allCont;
    }

    public LiveData<List<Conteudo>> getAllContentPonto(int id){
        MediatorLiveData<List<Conteudo>> allCont = new MediatorLiveData<>();
        LiveData<List<Trail>> trails = getAllTrails();


        allCont.addSource(trails, localTrails -> {
            List<Conteudo> contents = new ArrayList<>();
            int flag = -1;
            for (Trail trail : localTrails) {
                    if (flag == 1){break;}
                    List<Edge> edges = trail.getEdges();
                    for (Edge edge : edges) {
                        Ponto edgeStart = edge.getEdgeStart();
                        Ponto edgeEnd = edge.getEdgeEnd();
                        if (edgeStart != null && Integer.parseInt(edgeStart.getId()) == id){
                            contents.addAll(edgeStart.getConteudo());
                            flag = 1;
                            break;
                        }
                        if (edgeEnd != null && Integer.parseInt(edgeEnd.getId()) == id){
                            contents.addAll(edgeEnd.getConteudo());
                            flag = 1;
                            break;
                        }
                    }
            }
            allCont.setValue(contents);
        });

        return allCont;
    }

    public LiveData<List<Trail>> getTrailsByIds(List<Integer> ids) {
        MediatorLiveData<List<Trail>> histTrails = new MediatorLiveData<>();
        LiveData<List<Trail>> trails = getAllTrails();
        if (ids != null){
            histTrails.addSource(trails, localtrails -> {
                List<Trail> x = new ArrayList<>();
                for (Trail t : localtrails){
                    for (Integer id : ids){
                        if (Integer.parseInt(t.getId()) == id){
                            x.add(t);
                        }
                    }
                }
                if (!x.isEmpty()){
                    Log.d("LISTA CHECK",x.get(0).getId());
                }
                Log.d("?","?");
                histTrails.setValue(x);
            });
            return histTrails;
        }
        else {
            Log.d("HISTORICO","NADA DENTRO DO HISTORICO");
            return null;
        }
    }

    private static class InsertAsyncTask extends AsyncTask<List<Trail>,Void,Void> {
        private TrailDAO trailDAO;

        public InsertAsyncTask(TrailDAO catDao) {
            this.trailDAO=catDao;
        }

        @Override
        protected Void doInBackground(List<Trail>... lists) {
            trailDAO.insert(lists[0]);
            return null;
        }
    }

}
