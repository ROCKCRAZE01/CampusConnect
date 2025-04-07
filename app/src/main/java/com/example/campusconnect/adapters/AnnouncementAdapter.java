package com.example.campusconnect.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.campusconnect.R;

import java.util.List;

public class AnnouncementAdapter extends RecyclerView.Adapter<AnnouncementAdapter.ViewHolder> {

    private final List<String> announcementList;

    public AnnouncementAdapter(List<String> announcementList) {
        this.announcementList = announcementList;
    }

    @NonNull
    @Override
    public AnnouncementAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_announcement, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AnnouncementAdapter.ViewHolder holder, int position) {
        holder.announcementText.setText(announcementList.get(position));
    }

    @Override
    public int getItemCount() {
        return announcementList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView announcementText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            announcementText = itemView.findViewById(R.id.announcementText);
        }
    }
}
