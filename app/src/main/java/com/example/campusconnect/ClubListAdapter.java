package com.example.campusconnect;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ClubListAdapter extends RecyclerView.Adapter<ClubListAdapter.ClubViewHolder> {

    public interface OnClubClickListener {
        void onClubClick(String clubName);
    }

    private List<String> clubList;
    private OnClubClickListener listener;

    public ClubListAdapter(List<String> clubList, OnClubClickListener listener) {
        this.clubList = clubList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ClubViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
        return new ClubViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ClubViewHolder holder, int position) {
        String clubName = clubList.get(position);
        holder.textView.setText(clubName);
        holder.itemView.setOnClickListener(v -> listener.onClubClick(clubName));
    }

    @Override
    public int getItemCount() {
        return clubList.size();
    }

    static class ClubViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        public ClubViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(android.R.id.text1);
        }
    }
}
