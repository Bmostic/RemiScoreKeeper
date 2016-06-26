package com.brankomostic.remiscorekeeper.utils;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.brankomostic.remiscorekeeper.R;

public class GridAdapter extends RecyclerView.Adapter<GridAdapter.ViewHolder> {
    private String [] data;

    public GridAdapter(String [] d) {
        data = d;
    }

    @Override
    public GridAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View contactView = inflater.inflate(R.layout.score_display, parent, false);

        return new ViewHolder(contactView);
    }

    @Override
    public void onBindViewHolder(GridAdapter.ViewHolder holder, int position) {
        holder.textView.setText(data[position]);
    }

    @Override
    public int getItemCount() {
        return data.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;

        public ViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.score_display);
        }
    }
}
