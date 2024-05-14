package com.example.braguia2.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.braguia2.R;
import com.example.braguia2.model.Trails.Conteudo;
import com.example.braguia2.ui.MediaRecyclerViewAdapter;
import com.example.braguia2.viewmodel.TrailsViewModel;

import java.util.List;

public class MediaListFragment extends Fragment {


    private static final String ARG_COLUMN_COUNT = "column-count";

    private int mColumnCount = 1;

    private TrailsViewModel trailsViewModel;

    private int trailId;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public MediaListFragment(int id) {
        this.trailId = id;
    }

    public static MediaListFragment newInstance(int columnCount, int trailId) {
        MediaListFragment fragment = new MediaListFragment(trailId);
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_media_list, container, false);

        trailsViewModel = new ViewModelProvider(this).get(TrailsViewModel.class);
        trailsViewModel.getAllContentTrail(trailId).observe(getViewLifecycleOwner(), x -> {
            loadRecyclerView(view, x);
        });
        return view;
    }

    private void loadRecyclerView(View view, List<Conteudo> conteudo){
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            recyclerView.setAdapter(new MediaRecyclerViewAdapter(getViewLifecycleOwner(), conteudo, requireActivity().getSupportFragmentManager(),context));
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

}