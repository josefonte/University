
package com.example.braguia2.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
        import android.view.ViewGroup;
        import android.widget.ImageView;
        import android.widget.TextView;

        import androidx.fragment.app.FragmentManager;
        import androidx.fragment.app.FragmentTransaction;
        import androidx.navigation.Navigation;
        import androidx.recyclerview.widget.RecyclerView;

        import com.example.braguia2.R;
        import com.example.braguia2.model.Trails.Ponto;
        import com.example.braguia2.ui.fragments.ponto;
        import com.squareup.picasso.Picasso;

        import java.util.List;

public class ConfigSelectRecyclerViewAdapter extends RecyclerView.Adapter<ConfigSelectRecyclerViewAdapter.ViewHolder> {

    private final List<Ponto> mValues;
    private FragmentManager mFragmentManager;

    public ConfigSelectRecyclerViewAdapter(List<Ponto> items, FragmentManager fragmentManage) {
        mValues = items;
        this.mFragmentManager = fragmentManage;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.config_select_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        int idd = Integer.parseInt(mValues.get(position).getId());
        holder.mIdView.setText(mValues.get(position).getPinName());
        holder.mIdView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to the new fragment


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

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = view.findViewById(R.id.item_number);
        }

        @Override
        public String toString() {
            return super.toString() + mIdView;
        }
    }
}
