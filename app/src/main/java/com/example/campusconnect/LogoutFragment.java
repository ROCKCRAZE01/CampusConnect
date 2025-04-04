package com.example.campusconnect;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class LogoutFragment extends Fragment {

    public LogoutFragment() {
       // super(R.layout.fragment_logout); // Optional layout if needed
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Perform logout as soon as the fragment is loaded
        performLogout();
    }

    private void performLogout() {
        // Clear SharedPreferences
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("CampusConnectPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear(); // or editor.remove("userID");
        editor.apply();

        // Redirect to LoginActivity and clear activity stack
        Intent intent = new Intent(requireContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

        // Finish the current activity
        requireActivity().finish();
    }
}
