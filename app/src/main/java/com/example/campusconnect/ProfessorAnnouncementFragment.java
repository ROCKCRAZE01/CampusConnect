package com.example.campusconnect;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ProfessorAnnouncementFragment extends Fragment {
    TextView temp;
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_announcement, container, false);
        temp=view.findViewById(R.id.temp);
        temp.setText("Professor Announcement Fragment");
        return view;
    }
}
