package com.example.campusconnect;



import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class CreateDepartmentFragment extends Fragment {

    private EditText etDeptId, etDeptName, etUsername, etRole;
    private Button btnAddDept, btnModifyDept, btnDeleteDept, btnAssignDirector, btnAddUser;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_department, container, false);

        etDeptId = view.findViewById(R.id.etDeptId);
        etDeptName = view.findViewById(R.id.etDeptName);
        btnAddDept = view.findViewById(R.id.btnAddDept);
        btnModifyDept = view.findViewById(R.id.btnModifyDept);
        btnDeleteDept = view.findViewById(R.id.btnDeleteDept);
        etUsername = view.findViewById(R.id.etUsername);
        etRole = view.findViewById(R.id.etRole);
        btnAssignDirector = view.findViewById(R.id.btnAssignDirector);
        btnAddUser = view.findViewById(R.id.btnAddUser);

        btnAddDept.setOnClickListener(v -> Toast.makeText(getContext(), "Add Department Clicked", Toast.LENGTH_SHORT).show());
        btnModifyDept.setOnClickListener(v -> Toast.makeText(getContext(), "Modify Department Clicked", Toast.LENGTH_SHORT).show());
        btnDeleteDept.setOnClickListener(v -> Toast.makeText(getContext(), "Delete Department Clicked", Toast.LENGTH_SHORT).show());
        btnAssignDirector.setOnClickListener(v -> Toast.makeText(getContext(), "Assign Director Clicked", Toast.LENGTH_SHORT).show());
        btnAddUser.setOnClickListener(v -> Toast.makeText(getContext(), "Add User Clicked", Toast.LENGTH_SHORT).show());

        return view;
    }
}
