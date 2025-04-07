package com.example.campusconnect;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class Clubs_for_students extends Fragment {

    private RecyclerView recyclerView;
    private ClubListAdapter adapter;
    private ArrayList<String> clubList;
    private DatabaseHelper dbHelper;

    public Clubs_for_students() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_clubs_for_students, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewClubs);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("CampusConnectPrefs", Context.MODE_PRIVATE);
        int studentId = sharedPreferences.getInt("userID", -1);

        dbHelper = new DatabaseHelper(getContext());
        clubList = (ArrayList<String>) dbHelper.getClubsForStudent(studentId);

        if (clubList.isEmpty()) {
            Toast.makeText(getContext(), "No clubs found for student", Toast.LENGTH_SHORT).show();
        }

        // Initialize adapter BEFORE setting it
        adapter = new ClubListAdapter(clubList, clubName -> {
            Intent intent = new Intent(getContext(), ClubDetailActivity.class);
            intent.putExtra("club_name", clubName);
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);

        return view;
    }

}
