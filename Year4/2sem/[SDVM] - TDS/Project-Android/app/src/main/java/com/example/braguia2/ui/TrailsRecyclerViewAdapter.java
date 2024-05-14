package com.example.braguia2.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.braguia2.R;
import com.example.braguia2.model.Trails.Trail;
import com.example.braguia2.ui.fragments.trail;
import com.squareup.picasso.Picasso;

import java.util.List;

public class TrailsRecyclerViewAdapter extends RecyclerView.Adapter<TrailsRecyclerViewAdapter.ViewHolder> {

    private final List<Trail> mValues;
    private FragmentManager mFragmentManager;
    private OnItemClickListener listener;

    public TrailsRecyclerViewAdapter(List<Trail> items, FragmentManager fragmentManage) {
        mValues = items;
        this.mFragmentManager = fragmentManage;
    }

    public interface OnItemClickListener {
        void onItemClick(Trail trail);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        int idd = Integer.parseInt(mValues.get(position).getId());
        Trail traill = mValues.get(position);
        //holder.mIdView.setText(trail.getId());
        holder.titulo.setText(traill.getTrailName());
        holder.stat11.setText(Integer.toString(traill.getTrailDuration()));
        holder.stat12.setText(Double.toString(traill.getTrailDistance()));
        int edgesSize = traill.getEdges().size() + 1;
        holder.stat13.setText(Integer.toString(edgesSize));
        holder.stat14.setText(traill.getTrailDifficultyExtenso());
        Picasso.get()
                .load(mValues.get(position).getUrl())
                .into(holder.imageView);
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to the new fragment

                Bundle args = new Bundle();
                args.putInt("trailId", idd);

                Navigation.findNavController(holder.mView).navigate(R.id.action_explorar_fragment_to_trail_fragment,args);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdView;
        public final ImageButton imageView;
        public final TextView titulo;
        public final TextView stat11;
        public final TextView stat12;
        public final TextView stat13;
        public final TextView stat14;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = view.findViewById(R.id.item_number);
            imageView = view.findViewById(R.id.cardimage);
            titulo = view.findViewById(R.id.textView3);
            stat11 = view.findViewById(R.id.stat11);
            stat12 = view.findViewById(R.id.stat12);
            stat13 = view.findViewById(R.id.stat13);
            stat14 = view.findViewById(R.id.stat14);
        }



        @Override
        public String toString() {
            return super.toString() + mIdView;
        }
    }
}
