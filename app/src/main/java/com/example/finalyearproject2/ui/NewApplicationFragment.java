package com.example.finalyearproject2.ui;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.finalyearproject2.CustomTextInputLayout;
import com.example.finalyearproject2.NavigationActivity;
import com.example.finalyearproject2.NavigationAdminActivity;
import com.example.finalyearproject2.R;
import com.example.finalyearproject2.RegisterActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class NewApplicationFragment extends Fragment {
    ProgressBar PBNewApp;
    Button BtnSubmit;
    TextView TVSupDoc;
    CustomTextInputLayout LayoutName, LayoutEndDate, LayoutStartDate, LayoutLeaveType;
    EditText ETStartDate, ETEndDate, ETLeaveType, ETName;
    DatePickerDialog.OnDateSetListener dateStartSetListener;
    DatePickerDialog.OnDateSetListener dateEndSetListener;
    private String[] leave_types= {"Sick Leave", "Maternity Leave", "Annual Leave", "Emergency Leave", "Study Leave","Others"};
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    public static final int SELECT_PDF = 3;
    Uri uri;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_new_application, container, false);
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String formattedDate = df.format(c);
        PBNewApp = root.findViewById(R.id.progressBarNewApp);
        BtnSubmit = root.findViewById(R.id.NewEventsButton);
        ETName = root.findViewById(R.id.NewApplicationNameTextField);
        ETStartDate = root.findViewById(R.id.NewApplicationStartDateTextField);
        ETStartDate.setFocusable(false);
        ETStartDate.setClickable(true);
        ETEndDate = root.findViewById(R.id.NewApplicationEndDateTextField);
        ETEndDate.setFocusable(false);
        ETEndDate.setClickable(true);
        ETLeaveType = root.findViewById(R.id.LeaveTypeSpinner);
        ETLeaveType.setFocusable(false);
        ETLeaveType.setClickable(true);
        TVSupDoc = root.findViewById(R.id.textSuppDoc);
        LayoutName = root.findViewById(R.id.NewApplicationNameTextLayout);
        LayoutStartDate = root.findViewById(R.id.NewApplicationStartDateTextLayout);
        LayoutEndDate = root.findViewById(R.id.NewApplicationEndDateTextLayout);
        LayoutLeaveType = root.findViewById(R.id.NewApplicationLeaveTypeTextLayout);
        final ArrayAdapter<String> leaveTypesSelection = new  ArrayAdapter<String>(getContext(),android.R.layout.simple_spinner_dropdown_item, leave_types);
        String mystring = new String("Add support document");
        SpannableString content = new SpannableString(mystring);
        content.setSpan(new UnderlineSpan(), 0, mystring.length(), 0);
        TVSupDoc.setText(content);
        long current_time = System.currentTimeMillis();
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        DocumentReference reference = fStore.collection("Users").document(fAuth.getCurrentUser().getUid());
        reference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String name = documentSnapshot.getString("Name");
                ETName.setText(name);
            }
        });

        TVSupDoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("application/pdf");
                startActivityForResult(intent, SELECT_PDF);
                Toast.makeText(getContext(), "Only PDF files are allowed", Toast.LENGTH_SHORT).show();
            }
        });

        ETStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                final int year = calendar.get(Calendar.YEAR);
                final int month = calendar.get(Calendar.MONTH);
                final int day = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog dialog= new DatePickerDialog(getContext(), dateStartSetListener, year, month, day);
                dialog.show();
            }
        });

        ETEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                final int year = calendar.get(Calendar.YEAR);
                final int month = calendar.get(Calendar.MONTH);
                final int day = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog dialog= new DatePickerDialog(getContext(), dateEndSetListener, year, month, day);
                dialog.show();
            }
        });

        ETLeaveType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(getContext()).setAdapter(leaveTypesSelection, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ETLeaveType.setText(leave_types[which].toString());
                        dialog.dismiss();
                    }
                }).create().show();
            }
        });


        dateStartSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                month = month + 1;
                String date = day + "/" + month + "/" + year;
                ETStartDate.setText(date);
            }
        };

        dateEndSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                month = month + 1;
                String date = day + "/" + month + "/" + year;
                ETEndDate.setText(date);
            }
        };

        BtnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PBNewApp.setVisibility(View.VISIBLE);
                int validSubmit = 0;
                long start_date_millisecond = 0, end_date_millisecond = 0;
                String name = ETName.getText().toString().trim();
                String startDate = ETStartDate.getText().toString().trim();
                String endDate = ETEndDate.getText().toString().trim();
                String leaveType = ETLeaveType.getText().toString().trim();
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                try {
                    start_date_millisecond = sdf.parse(startDate).getTime();
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                try {
                    end_date_millisecond = sdf.parse(endDate).getTime();
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                if(TextUtils.isEmpty(name)){
                    LayoutName.setError("Name is required");
                }
                else{
                    LayoutName.setError(null);
                    validSubmit += 1;
                }

                if(TextUtils.isEmpty(startDate)){
                    LayoutStartDate.setError("Start date is required");
                }
                else if(start_date_millisecond < current_time){
                    LayoutStartDate.setError("Start date is not valid");
                }
                else{
                    LayoutStartDate.setError(null);
                    validSubmit += 1;
                }

                if(TextUtils.isEmpty(endDate)){
                    LayoutEndDate.setError("End date is required");
                }
                else if(end_date_millisecond < start_date_millisecond){
                    LayoutEndDate.setError("End date is not valid");
                }
                else{
                    LayoutEndDate.setError(null);
                    validSubmit += 1;
                }

                if(TextUtils.isEmpty(leaveType)){
                    LayoutLeaveType.setError("Leave type is required");
                }
                else{
                    LayoutLeaveType.setError(null);
                    validSubmit += 1;
                }

                if(validSubmit == 4){
                    DocumentReference document = fStore.collection("Pending Leave Application").document(fAuth.getCurrentUser().getUid() + "_" + current_time);
                    document.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            StorageReference storage = FirebaseStorage.getInstance().getReference().child("leaveSuppDoc").child(fAuth.getCurrentUser().getUid() + "_" + current_time + ".pdf");
                            if(uri != null){
                                storage.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        PBNewApp.setVisibility(View.GONE);
                                        Map<String, Object> NewApplication = new HashMap<>();
                                        NewApplication.put("Name", name);
                                        NewApplication.put("Start Date", startDate);
                                        NewApplication.put("End Date", endDate);
                                        NewApplication.put("Leave Type", leaveType);
                                        NewApplication.put("Apply Time", formattedDate);
                                        NewApplication.put("Status", "Pending");
                                        document.set(NewApplication);
                                        Toast.makeText(getContext(), "New leave application successfully submitted", Toast.LENGTH_SHORT).show();
                                        DocumentReference doc = fStore.collection("Users").document(fAuth.getCurrentUser().getUid());
                                        doc.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                            @Override
                                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                String privilege = documentSnapshot.getString("Privilege");
                                                Intent intent;
                                                if(privilege.equals("Administrator")){
                                                    intent = new Intent(getActivity(), NavigationAdminActivity.class);
                                                }
                                                else{
                                                    intent = new Intent(getActivity(), NavigationActivity.class);
                                                }
                                                startActivity(intent);
                                            }
                                        });

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        PBNewApp.setVisibility(View.GONE);
                                        Toast.makeText(getContext(), "Something went wrong. Please try again later.", Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                            else{
                                PBNewApp.setVisibility(View.GONE);
                                Toast.makeText(getContext(), "A support document must be selected", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            PBNewApp.setVisibility(View.GONE);
                            Toast.makeText(getContext(), "Something went wrong. Please try again later.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else{
                    PBNewApp.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Please complete all of the fields above", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return root;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
            if(requestCode == SELECT_PDF){
                if(resultCode == RESULT_OK){
                    uri = data.getData();
                    Toast.makeText(getContext(), "File selected", Toast.LENGTH_SHORT).show();
                }
                else if(resultCode == RESULT_CANCELED){
                    Toast.makeText(getContext(), "Gallery closed", Toast.LENGTH_SHORT).show();
                }
            }
    }
    public void closeKeyBoard(View view){
        view = getActivity().getCurrentFocus();
        if(view != null){
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}