package com.example.campusconnect;





import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class CreateDepartmentFragment extends Fragment {

    private Spinner spinnerEntity, spinnerOperation;
    private LinearLayout layoutDepartment, layoutClub;
    private EditText etDeptCode, etDeptName, etDirector, etClubName, etFacultyAdvisor, etClubCode;
    private Button btnCreate, btnModify, btnDelete;
    private int selectedEntityPosition =0;
    private int selectedOperationPosition =0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_department, container, false);

        // Initialize UI elements
        spinnerEntity = view.findViewById(R.id.spinnerEntity);
        spinnerOperation = view.findViewById(R.id.spinnerOperation);
        layoutDepartment = view.findViewById(R.id.layoutDepartment);
        layoutClub = view.findViewById(R.id.layoutClub);

        etDeptCode = view.findViewById(R.id.etDeptCode);
        etDeptName = view.findViewById(R.id.etDeptName);
        etDirector = view.findViewById(R.id.etDirector);
        etClubName = view.findViewById(R.id.etClubName);
        etClubCode = view.findViewById(R.id.etClubCode);
        etFacultyAdvisor = view.findViewById(R.id.etFacultyAdvisor);

        btnCreate = view.findViewById(R.id.btnCreate);
        btnModify = view.findViewById(R.id.btnModify);
        btnDelete = view.findViewById(R.id.btnDelete);

        // Set up the spinners
        ArrayAdapter<CharSequence> entityAdapter = ArrayAdapter.createFromResource(
                getContext(), R.array.entity_array, R.layout.spinner_item); // use your layout
        entityAdapter.setDropDownViewResource(R.layout.spinner_item); // for dropdown too
        spinnerEntity.setAdapter(entityAdapter);

        ArrayAdapter<CharSequence> operationAdapter = ArrayAdapter.createFromResource(
                getContext(), R.array.operations_array, R.layout.spinner_item);
        operationAdapter.setDropDownViewResource(R.layout.spinner_item);
        spinnerOperation.setAdapter(operationAdapter);

        // Handle entity selection
        selectedEntityPosition = 0;
        selectedOperationPosition = 0;

        spinnerEntity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedEntityPosition = position;
                updateEntityUI(position);          // Update UI on entity change
                updateOperationUI(selectedOperationPosition); // Re-apply operation UI to match new entity
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        spinnerOperation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedOperationPosition = position;
                updateOperationUI(position);       // Update UI on operation change
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        DatabaseHelper dbHelper = new DatabaseHelper(getContext());

        // Handle Create button
        btnCreate.setOnClickListener(v -> {
            String selectedEntity = spinnerEntity.getSelectedItem().toString();

            if (selectedEntity.equals("Department")) {
                String deptName = etDeptName.getText().toString().trim();
                String deptCode = etDeptCode.getText().toString().trim();
                String directorIdStr = etDirector.getText().toString().trim();

                if (deptName.isEmpty() || deptCode.isEmpty() || directorIdStr.isEmpty()) {
                    Toast.makeText(getContext(), "Fill all department fields!", Toast.LENGTH_SHORT).show();
                    return;
                }

                int directorId;
                try {
                    directorId = Integer.parseInt(directorIdStr);
                } catch (NumberFormatException e) {
                    Toast.makeText(getContext(), "Director ID must be a number", Toast.LENGTH_SHORT).show();
                    return;
                }

                boolean success = dbHelper.createDepartmentWithDirector(deptName, deptCode, directorId, 0);
                Toast.makeText(getContext(), success ? "Department Created!" : "Director must be a Professor!", Toast.LENGTH_SHORT).show();

            } else if (selectedEntity.equals("Club")) {
                String clubName = etClubName.getText().toString().trim();
                String facultyAdvisorStr = etFacultyAdvisor.getText().toString().trim();
                String clubCode = etClubCode.getText().toString().trim();

                if (clubName.isEmpty() || facultyAdvisorStr.isEmpty()) {
                    Toast.makeText(getContext(), "Fill all club fields!", Toast.LENGTH_SHORT).show();
                    return;
                }

                int facultyAdvisorId;
                try {
                    facultyAdvisorId = Integer.parseInt(facultyAdvisorStr);
                } catch (NumberFormatException e) {
                    Toast.makeText(getContext(), "Faculty Advisor ID must be a number", Toast.LENGTH_SHORT).show();
                    return;
                }

                boolean success = dbHelper.createClub(clubName,clubCode ,facultyAdvisorId);
                Toast.makeText(getContext(), success ? "Club Created!" : "Advisor must be a Faculty!", Toast.LENGTH_SHORT).show();
            }
        });


        return view;
    }

    private void updateEntityUI(int position) {
        if (position == 1) { // Department
            layoutDepartment.setVisibility(View.VISIBLE);
            layoutClub.setVisibility(View.GONE);
        } else if (position == 2) { // Club
            layoutDepartment.setVisibility(View.GONE);
            layoutClub.setVisibility(View.VISIBLE);
        }
    }


    private void updateOperationUI(int position) {
        boolean isDepartment = (selectedEntityPosition == 1);
        boolean isClub = (selectedEntityPosition == 2);

        // Hide all fields by default
        etDeptCode.setVisibility(View.GONE);
        etDeptName.setVisibility(View.GONE);
        etDirector.setVisibility(View.GONE);
        etClubCode.setVisibility(View.GONE);
        etClubName.setVisibility(View.GONE);
        etFacultyAdvisor.setVisibility(View.GONE);

        // Reset buttons
        btnCreate.setVisibility(View.GONE);
        btnModify.setVisibility(View.GONE);
        btnDelete.setVisibility(View.GONE);

        if (position == 1) { // Create
            if (isDepartment) {
                etDeptCode.setVisibility(View.VISIBLE);
                etDeptName.setVisibility(View.VISIBLE);
                etDirector.setVisibility(View.VISIBLE);
            } else if (isClub) {
                etClubCode.setVisibility(View.VISIBLE);
                etClubName.setVisibility(View.VISIBLE);
                etFacultyAdvisor.setVisibility(View.VISIBLE);
            }
            btnCreate.setVisibility(View.VISIBLE);

        } else if (position == 2) { // Modify
            if (isDepartment) {
                etDeptCode.setVisibility(View.VISIBLE);
                etDirector.setVisibility(View.VISIBLE);
            } else if (isClub) {
                etClubCode.setVisibility(View.VISIBLE);
                etFacultyAdvisor.setVisibility(View.VISIBLE);
            }
            btnModify.setVisibility(View.VISIBLE);

        } else if (position == 3) { // Delete
            if (isDepartment) {
                etDeptCode.setVisibility(View.VISIBLE);
            } else if (isClub) {
                etClubCode.setVisibility(View.VISIBLE);
            }
            btnDelete.setVisibility(View.VISIBLE);
        }
    }



}
