package com.example.campusconnect.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.campusconnect.R;
import com.example.campusconnect.adapters.ChatAdapter;
import com.example.campusconnect.DatabaseHelper;
import com.example.campusconnect.models.ChatMessage;

import java.util.List;

public class ClubChatFragment extends Fragment {

    private static final String ARG_CLUB_NAME = "club_name";
    private String clubName;

    private RecyclerView chatRecyclerView;
    private ChatAdapter chatAdapter;
    private EditText messageInput;
    private ImageButton sendButton;

    private DatabaseHelper dbHelper;

    public static ClubChatFragment newInstance(String clubName) {
        ClubChatFragment fragment = new ClubChatFragment();
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
        dbHelper = new DatabaseHelper(requireContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_club_chat, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        chatRecyclerView = view.findViewById(R.id.chatRecyclerView);
        messageInput = view.findViewById(R.id.messageInput);
        sendButton = view.findViewById(R.id.sendButton);

        chatRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        loadMessages();

        sendButton.setOnClickListener(v -> {
            String message = messageInput.getText().toString().trim();
            if (!TextUtils.isEmpty(message)) {
                SharedPreferences prefs = requireContext().getSharedPreferences("CampusConnectPrefs", Context.MODE_PRIVATE);
                int studentId = prefs.getInt("userID", -1);
                if (studentId != -1) {
                    dbHelper.insertClubChatMessage(clubName, studentId, message);
                    messageInput.setText("");
                    loadMessages();
                }
            }
        });
    }

    private void loadMessages() {
        List<ChatMessage> messages = dbHelper.getClubChatMessages(clubName);
        chatAdapter = new ChatAdapter(messages);
        chatRecyclerView.setAdapter(chatAdapter);
        chatRecyclerView.scrollToPosition(messages.size() - 1);
    }
}
