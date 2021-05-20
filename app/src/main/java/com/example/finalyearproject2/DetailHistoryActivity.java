package com.example.finalyearproject2;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static android.os.Environment.DIRECTORY_DOWNLOADS;

public class DetailHistoryActivity extends AppCompatActivity {
    TextView TVName, TVLeaveType, TVStart, TVEnd, TVStatus, TVDownload, TVUpdate;
    CustomTextInputLayout LayoutName, LayoutLeaveType, LayoutStart, LayoutEnd;
    EditText ETName, ETLeaveType, ETStart, ETEnd;
    ImageView imageBack, imageEdit, imageSave, imageDelete;
    FirebaseFirestore fStore;
    FirebaseAuth fAuth;
    StorageReference storageReference, file;
    String leave_id, status;
    ProgressBar PBDownloadHistory;
    DatePickerDialog.OnDateSetListener dateStartSetListener;
    DatePickerDialog.OnDateSetListener dateEndSetListener;
    private String[] leave_types= {"Sick Leave", "Maternity Leave", "Annual Leave", "Emergency Leave", "Study Leave","Others"};
    public static final int UPDATE_PDF = 4;
    Uri uri;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_history);
        long current_time = System.currentTimeMillis();
        PBDownloadHistory = findViewById(R.id.progressBarHistoryDownload);
        TVName = findViewById(R.id.textNameHistory);
        TVLeaveType = findViewById(R.id.textLeaveTypeHistory);
        TVStart = findViewById(R.id.textStartHistory);
        TVEnd = findViewById(R.id.textEndHistory);
        TVStatus = findViewById(R.id.textStatusHistory);
        TVDownload = findViewById(R.id.textDownloadSupDocHistory);
        TVUpdate = findViewById(R.id.textUpdateSupDocHistory);
        LayoutName = findViewById(R.id.EditNameHistoryTextLayout);
        LayoutLeaveType = findViewById(R.id.EditLeaveTypeHistoryTextLayout);
        LayoutStart = findViewById(R.id.EditStartHistoryTextLayout);
        LayoutEnd = findViewById(R.id.EditEndHistoryTextLayout);
        ETName = findViewById(R.id.EditNameHistory);
        ETLeaveType = findViewById(R.id.EditLeaveTypeHistory);
        ETLeaveType.setFocusable(false);
        ETLeaveType.setClickable(true);
        final ArrayAdapter<String> leaveTypesSelection = new  ArrayAdapter<String>(DetailHistoryActivity.this,android.R.layout.simple_spinner_dropdown_item, leave_types);
        ETStart = findViewById(R.id.EditStartHistory);
        ETStart.setFocusable(false);
        ETStart.setClickable(true);
        ETEnd = findViewById(R.id.EditEndHistory);
        ETEnd.setFocusable(false);
        ETEnd.setClickable(true);
        imageBack = findViewById(R.id.imageQuitHistory);
        imageEdit = findViewById(R.id.imageEditHistory);
        imageSave = findViewById(R.id.imageSaveHistory);
        imageDelete = findViewById(R.id.imageDeleteHistory);
        String download = new String("Download support document");
        SpannableString content_download = new SpannableString(download);
        content_download.setSpan(new UnderlineSpan(), 0, download.length(), 0);
        TVDownload.setText(content_download);
        String update = new String("Update support document");
        SpannableString content_update = new SpannableString(update);
        content_update.setSpan(new UnderlineSpan(), 0, update.length(), 0);
        TVUpdate.setText(content_update);
        leave_id = getIntent().getStringExtra("LeaveID");
        status = getIntent().getStringExtra("Status");
        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();

        ETStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                final int year = calendar.get(Calendar.YEAR);
                final int month = calendar.get(Calendar.MONTH);
                final int day = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog dialog= new DatePickerDialog(DetailHistoryActivity.this, dateStartSetListener, year, month, day);
                dialog.show();
            }
        });

        ETEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                final int year = calendar.get(Calendar.YEAR);
                final int month = calendar.get(Calendar.MONTH);
                final int day = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog dialog= new DatePickerDialog(DetailHistoryActivity.this, dateEndSetListener, year, month, day);
                dialog.show();
            }
        });

        ETLeaveType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(DetailHistoryActivity.this).setAdapter(leaveTypesSelection, new DialogInterface.OnClickListener() {
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
                ETStart.setText(date);
            }
        };

        dateEndSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                month = month + 1;
                String date = day + "/" + month + "/" + year;
                ETEnd.setText(date);
            }
        };

        switch (status) {
            case "Approved": {
                DocumentReference ref = fStore.collection("Approved Leave").document(leave_id);
                ref.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String name = documentSnapshot.getString("Name");
                        String leaveType = documentSnapshot.getString("Leave Type");
                        String start = documentSnapshot.getString("Start Date");
                        String end = documentSnapshot.getString("End Date");
                        String status = documentSnapshot.getString("Status");
                        TVName.setText("Name :      " + name);
                        TVLeaveType.setText("Leave Type :      " + leaveType);
                        TVStart.setText("Start Date :      " + start);
                        TVEnd.setText("End Date :      " + end);
                        TVStatus.setText("Status :      " + status);
                    }
                });
                break;
            }
            case "Declined": {
                DocumentReference ref = fStore.collection("Declined Leave").document(leave_id);
                ref.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String name = documentSnapshot.getString("Name");
                        String leaveType = documentSnapshot.getString("Leave Type");
                        String start = documentSnapshot.getString("Start Date");
                        String end = documentSnapshot.getString("End Date");
                        String status = documentSnapshot.getString("Status");
                        TVName.setText("Name :      " + name);
                        TVLeaveType.setText("Leave Type :      " + leaveType);
                        TVStart.setText("Start Date :      " + start);
                        TVEnd.setText("End Date :      " + end);
                        TVStatus.setText("Status :      " + status);
                    }
                });
                break;
            }
            case "Pending": {
                DocumentReference ref = fStore.collection("Pending Leave Application").document(leave_id);
                ref.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String name = documentSnapshot.getString("Name");
                        String leaveType = documentSnapshot.getString("Leave Type");
                        String start = documentSnapshot.getString("Start Date");
                        String end = documentSnapshot.getString("End Date");
                        String status = documentSnapshot.getString("Status");
                        TVName.setText("Name :      " + name);
                        TVLeaveType.setText("Leave Type :      " + leaveType);
                        TVStart.setText("Start Date :      " + start);
                        TVEnd.setText("End Date :      " + end);
                        TVStatus.setText("Status :      " + status);
                    }
                });
                imageEdit.setVisibility(View.VISIBLE);
                imageDelete.setVisibility(View.VISIBLE);
                break;
            }
        }

        imageDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(DetailHistoryActivity.this);
                builder.setTitle("Leave application deletion");
                builder.setMessage("Are you sure to delete this leave application?");
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DocumentReference documentReference = fStore.collection("Pending Leave Application").document(leave_id);
                        documentReference.delete();
                        Toast.makeText(DetailHistoryActivity.this,"Leave application deleted successfully", Toast.LENGTH_LONG).show();
                        DocumentReference ref = fStore.collection("users").document(fAuth.getCurrentUser().getUid());
                        ref.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                String privilege = documentSnapshot.getString("Privilege");
                                Intent intent;
                                if(privilege.equals("Administrator")){
                                    intent = new Intent(DetailHistoryActivity.this, NavigationAdminActivity.class);
                                }
                                else{
                                    intent = new Intent(DetailHistoryActivity.this, NavigationActivity.class);
                                }
                                startActivity(intent);
                            }
                        });
                    }
                });
                builder.create().show();
            }
        });

        imageEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TVName.setVisibility(View.GONE);
                TVLeaveType.setVisibility(View.GONE);
                TVStart.setVisibility(View.GONE);
                TVEnd.setVisibility(View.GONE);
                TVStatus.setVisibility(View.GONE);
                TVDownload.setVisibility(View.GONE);
                LayoutName.setVisibility(View.VISIBLE);
                LayoutLeaveType.setVisibility(View.VISIBLE);
                LayoutStart.setVisibility(View.VISIBLE);
                LayoutEnd.setVisibility(View.VISIBLE);
                TVUpdate.setVisibility(View.VISIBLE);
                imageEdit.setVisibility(View.GONE);
                imageSave.setVisibility(View.VISIBLE);
                DocumentReference ref = fStore.collection("Pending Leave Application").document(leave_id);
                ref.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String name = documentSnapshot.getString("Name");
                        String leaveType = documentSnapshot.getString("Leave Type");
                        String start = documentSnapshot.getString("Start Date");
                        String end = documentSnapshot.getString("End Date");
                        ETName.setText(name);
                        ETLeaveType.setText(leaveType);
                        ETStart.setText(start);
                        ETEnd.setText(end);
                    }
                });
            }
        });

        imageSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long start_date_millisecond = 0 , end_date_millisecond = 0;
                String name = ETName.getText().toString().trim();
                String leaveType = ETLeaveType.getText().toString().trim();
                String start = ETStart.getText().toString().trim();
                String end = ETEnd.getText().toString().trim();
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                try {
                    start_date_millisecond = sdf.parse(start).getTime();
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                try {
                    end_date_millisecond = sdf.parse(end).getTime();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                int validUpdate = 0;

                if(TextUtils.isEmpty(name)){
                    LayoutName.setError("Name is required");
                }
                else{
                    LayoutName.setError(null);
                    validUpdate += 1;
                }
                if(TextUtils.isEmpty(leaveType)){
                    LayoutLeaveType.setError("Leave type is required");
                }
                else{
                    LayoutLeaveType.setError(null);
                    validUpdate += 1;
                }
                if(TextUtils.isEmpty(start)){
                    LayoutStart.setError("Start date is required");
                }
                else if(start_date_millisecond < current_time){
                    LayoutStart.setError("Start date is invalid");
                }
                else{
                    LayoutStart.setError(null);
                    validUpdate += 1;
                }
                if(TextUtils.isEmpty(end)){
                    LayoutEnd.setError("End date is required");
                }
                else if(end_date_millisecond < start_date_millisecond){
                    LayoutEnd.setError("End date is invalid");
                }
                else{
                    LayoutEnd.setError(null);
                    validUpdate += 1;
                }

                if(validUpdate == 4){
                    closeKeyBoard();
                    imageSave.setVisibility(View.GONE);
                    imageEdit.setVisibility(View.VISIBLE);
                    TVName.setVisibility(View.VISIBLE);
                    TVLeaveType.setVisibility(View.VISIBLE);
                    TVStart.setVisibility(View.VISIBLE);
                    TVEnd.setVisibility(View.VISIBLE);
                    TVStatus.setVisibility(View.VISIBLE);
                    TVDownload.setVisibility(View.VISIBLE);
                    LayoutName.setVisibility(View.GONE);
                    LayoutLeaveType.setVisibility(View.GONE);
                    LayoutStart.setVisibility(View.GONE);
                    LayoutEnd.setVisibility(View.GONE);
                    TVUpdate.setVisibility(View.GONE);

                    TVName.setText("Name :      " + name);
                    TVLeaveType.setText("Leave Type :      " + leaveType);
                    TVStart.setText("Start Date :      " + start);
                    TVEnd.setText("End Date :      " + end);

                    DocumentReference ref = fStore.collection("Pending Leave Application").document(leave_id);
                    ref.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            Map<String, Object> leaveUpdate = new HashMap<>();
                            leaveUpdate.put("Name", name);
                            leaveUpdate.put("Leave Type", leaveType);
                            leaveUpdate.put("Start Date", start);
                            leaveUpdate.put("End Date", end);
                            ref.update(leaveUpdate);
                            if(uri != null){
                                StorageReference storage = FirebaseStorage.getInstance().getReference().child("leaveSuppDoc").child(leave_id + ".pdf");
                                storage.putFile(uri);
                            }
                            Toast.makeText(DetailHistoryActivity.this, "Leave application updated successfully", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else{
                    Toast.makeText(DetailHistoryActivity.this,"Please complete all of the fields above", Toast.LENGTH_LONG).show();
                }
            }
        });

        imageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        TVDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                storageReference = FirebaseStorage.getInstance().getReference();
                file = storageReference.child("leaveSuppDoc/" + leave_id + ".pdf");
                file.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String url = String.valueOf(uri);
                        downloadFile(DetailHistoryActivity.this, leave_id, DIRECTORY_DOWNLOADS ,url);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(DetailHistoryActivity.this, "Something went wrong, please try again later", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        TVUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("application/pdf");
                startActivityForResult(intent, UPDATE_PDF);
                Toast.makeText(DetailHistoryActivity.this, "Only PDF files are allowed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void closeKeyBoard(){
        View view = DetailHistoryActivity.this.getCurrentFocus();
        if(view != null){
            InputMethodManager imm = (InputMethodManager) DetailHistoryActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void downloadFile(Context context, String filename, String destination, String url) {
        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(url);
        DownloadManager.Request req = new DownloadManager.Request(uri);
        req.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        req.setDestinationInExternalPublicDir(destination, filename + ".pdf");
        downloadManager.enqueue(req);
        PBDownloadHistory.setVisibility(View.VISIBLE);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(DetailHistoryActivity.this, "The support document has been downloaded", Toast.LENGTH_SHORT).show();
                PBDownloadHistory.setVisibility(View.GONE);
            }
        }, 2500);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == UPDATE_PDF){
            if(resultCode == RESULT_OK){
                uri = data.getData();
                Toast.makeText(DetailHistoryActivity.this, "File selected", Toast.LENGTH_SHORT).show();
            }
            else if(resultCode == RESULT_CANCELED){
                Toast.makeText(DetailHistoryActivity.this, "Gallery closed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void closeKeyBoard(View view){
        view = DetailHistoryActivity.this.getCurrentFocus();
        if(view != null){
            InputMethodManager imm = (InputMethodManager) DetailHistoryActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
