package com.example.finalyearproject2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import static android.os.Environment.DIRECTORY_DOWNLOADS;

public class DetailApprovalActivity extends AppCompatActivity {
    String Id, startDate, endDate, approvedLeaveType, updatedEvent, event;
    TextView TVName, TVAge, TVGender, TVLeaveType, TVStartDate, TVEndDate, TVApplyDate, TVDownloadLink;
    ImageView imgBack;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    StorageReference storageReference, file;
    ProgressBar PBDownload;
    Button btnApprove, btnDecline;
    long start_date_millisecond, end_date_millesecond;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_approval);
        Handler handler = new Handler();
        btnApprove = findViewById(R.id.buttonApprove);
        btnDecline = findViewById(R.id.buttonDecline);
        PBDownload = findViewById(R.id.progressBarDownload);
        imgBack = findViewById(R.id.imageQuitDetails);
        TVName = findViewById(R.id.textNameDetails);
        TVAge = findViewById(R.id.textAgeDetails);
        TVGender = findViewById(R.id.textGenderDetails);
        TVLeaveType = findViewById(R.id.textLeaveTypeDetails);
        TVStartDate = findViewById(R.id.textStartDateDetails);
        TVEndDate = findViewById(R.id.textEndDateDetails);
        TVApplyDate = findViewById(R.id.textApplyDateDetails);
        TVDownloadLink = findViewById(R.id.textDownloadLink);
        String mystring = new String("Download support document");
        SpannableString content = new SpannableString(mystring);
        content.setSpan(new UnderlineSpan(), 0, mystring.length(), 0);
        TVDownloadLink.setText(content);
        Id = getIntent().getStringExtra("LeaveID");
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        String UserId = Id.split("_")[0];
        DocumentReference dr = fStore.collection("Users").document(UserId);
        dr.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String name = documentSnapshot.getString("Name");
                String age = documentSnapshot.getString("Age");
                String gender = documentSnapshot.getString("Gender");
                TVName.setText("Name :     " + name);
                TVAge.setText("Age :     " + age);
                TVGender.setText("Gender :     " + gender);
            }
        });
        DocumentReference reference = fStore.collection("Pending Leave Application").document(Id);
        reference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String start = documentSnapshot.getString("Start Date");
                startDate = start;
                String end = documentSnapshot.getString("End Date");
                endDate = end;
                String leaveType = documentSnapshot.getString("Leave Type");
                approvedLeaveType = leaveType;
                String applyTime = documentSnapshot.getString("Apply Time");
                TVApplyDate.setText("Applied on " + applyTime);
                TVStartDate.setText("From   " + start);
                TVEndDate.setText("   to   " + end);
                TVLeaveType.setText(leaveType);
            }
        });

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        TVDownloadLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                storageReference = FirebaseStorage.getInstance().getReference();
                file = storageReference.child("leaveSuppDoc/" + Id + ".pdf");
                file.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String url = String.valueOf(uri);
                        downloadFile(DetailApprovalActivity.this, Id, DIRECTORY_DOWNLOADS ,url);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(DetailApprovalActivity.this, "Something went wrong, please try again later", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        btnApprove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PBDownload.setVisibility(View.VISIBLE);
                DocumentReference reference = fStore.collection("Pending Leave Application").document(Id);
                reference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Map<String, Object> UpdateLeave = new HashMap<>();
                        UpdateLeave.put("Status", "Approved");
                        reference.update(UpdateLeave);
                        DocumentReference dest = fStore.collection("Approved Leave").document(Id);
                        moveFirestoreDocument(reference, dest);
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                startActivity(new Intent(DetailApprovalActivity.this, NavigationAdminActivity.class));
                                PBDownload.setVisibility(View.GONE);
                                Toast.makeText(DetailApprovalActivity.this, "Application successfully approved", Toast.LENGTH_SHORT).show();
                            }
                        }, 3000);

                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                        try {
                            start_date_millisecond = sdf.parse(startDate).getTime();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        try {
                            end_date_millesecond = sdf.parse(endDate).getTime();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        long counter;
                        for(counter = start_date_millisecond; counter <= end_date_millesecond; counter += 86400000){
                            DocumentReference ref = fStore.collection("Users").document(UserId).collection("Calendar").document(String.valueOf(counter));
                            ref.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    event = documentSnapshot.getString("Events");
                                    if(event != null){
                                        Log.d("tag1", "not null");
                                        updatedEvent = event + ", " + approvedLeaveType;
                                        Map<String, Object> events = new HashMap<>();
                                        events.put("Events", updatedEvent);
                                        ref.update(events);
                                    }
                                    else{
                                        Log.d("tag1", "null");
                                        updatedEvent = approvedLeaveType;
                                        Map<String, Object> events = new HashMap<>();
                                        events.put("Events", updatedEvent);
                                        ref.set(events);
                                    }
                                }
                            });
                        }
                    }
                });
            }
        });

        btnDecline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PBDownload.setVisibility(View.VISIBLE);
                DocumentReference reference = fStore.collection("Pending Leave Application").document(Id);
                reference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Map<String, Object> UpdateLeave = new HashMap<>();
                        UpdateLeave.put("Status", "Declined");
                        reference.update(UpdateLeave);
                        DocumentReference dest = fStore.collection("Declined Leave").document(Id);
                        moveFirestoreDocument(reference, dest);

                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                startActivity(new Intent(DetailApprovalActivity.this, NavigationAdminActivity.class));
                                PBDownload.setVisibility(View.GONE);
                                Toast.makeText(DetailApprovalActivity.this, "Application successfully declined", Toast.LENGTH_SHORT).show();
                            }
                        }, 3000);
                    }
                });
            }
        });
    }

    private void downloadFile(Context context, String filename, String destination, String url) {
        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(url);
        DownloadManager.Request req = new DownloadManager.Request(uri);
        req.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        req.setDestinationInExternalPublicDir(destination, filename + ".pdf");
        downloadManager.enqueue(req);
        PBDownload.setVisibility(View.VISIBLE);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(DetailApprovalActivity.this, "The support document has been downloaded", Toast.LENGTH_SHORT).show();
                PBDownload.setVisibility(View.GONE);
            }
        }, 2500);
    }

    public void moveFirestoreDocument(DocumentReference fromPath, final DocumentReference toPath) {
        fromPath.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null) {
                        toPath.set(document.getData()).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                fromPath.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                    }
                                });
                            }
                        });
                    }
                }
            }
        });
    }
}