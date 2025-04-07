package com.example.campusconnect.fragments;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.campusconnect.DatabaseHelper;
import com.example.campusconnect.R;

import java.util.List;

public class ClubInfoFragment extends Fragment {

    private static final String ARG_CLUB_NAME = "club_name";
    private String clubName;

    public static ClubInfoFragment newInstance(String clubName) {
        ClubInfoFragment fragment = new ClubInfoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_CLUB_NAME, clubName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            clubName = getArguments().getString(ARG_CLUB_NAME);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_club_info, container, false);
        TextView textView = view.findViewById(R.id.clubInfoTextView);

        DatabaseHelper dbHelper = new DatabaseHelper(requireContext());

        StringBuilder info = new StringBuilder();

        // Club Basic Info
        String clubInfo = dbHelper.getClubInfo(clubName);
        info.append(clubInfo).append("\n\n");

        // Faculty Advisors
        List<String> advisors = dbHelper.getClubFacultyAdvisors(clubName);
        info.append("Faculty Advisors:\n");
        if (advisors.isEmpty()) {
            info.append(" - None assigned\n");
        } else {
            for (String advisor : advisors) {
                info.append(" - ").append(advisor).append("\n");
            }
        }

        // Club Members
        List<String> members = dbHelper.getClubMembersWithRoles(clubName);
        info.append("\nMembers:\n");
        if (members.isEmpty()) {
            info.append(" - No members yet\n");
        } else {
            for (String member : members) {
                info.append(" - ").append(member).append("\n");
            }
        }

        textView.setText(info.toString());

        return view;
    }


}
