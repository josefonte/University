package com.example.braguia2.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.braguia2.R;
import com.example.braguia2.model.Trails.Trail;
import com.example.braguia2.model.User.User;
import com.example.braguia2.ui.TrailsRecyclerViewAdapter;
import com.example.braguia2.viewmodel.TrailsViewModel;
import com.example.braguia2.viewmodel.UserViewModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TrailHistoricoListFragment extends Fragment {


    private static final String ARG_COLUMN_COUNT = "column-count";

    private int mColumnCount = 1;

    private TrailsViewModel trailsViewModel;


     private List<Trail> trails = new ArrayList<>();

    private int id;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public TrailHistoricoListFragment() {
    }

    public static TrailHistoricoListFragment newInstance(int columnCount) {
        TrailHistoricoListFragment fragment = new TrailHistoricoListFragment();
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
        View view = inflater.inflate(R.layout.fragment_item_list, container, false);

        UserViewModel userviewModel = new ViewModelProvider(this).get(UserViewModel.class);

        trailsViewModel = new ViewModelProvider(this).get(TrailsViewModel.class);

        try {
            userviewModel.getUser().observe(getViewLifecycleOwner(), u -> {
                Log.d("NADA?","NADA?");
                if (u != null) {
                    trailsViewModel.getTrailsByIds(u.getHistorico_T()).observe(getViewLifecycleOwner(), y -> {
                        loadRecyclerView(view, y);
                    });
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return view;
    }

    private void loadRecyclerView(View view, List<Trail> trails){
        if (view instanceof RecyclerView && !trails.isEmpty()) {
            Log.d("TRAIL?",trails.get(0).getId());
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            recyclerView.setAdapter(new TrailsRecyclerViewAdapter(trails, requireActivity().getSupportFragmentManager()));
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

}