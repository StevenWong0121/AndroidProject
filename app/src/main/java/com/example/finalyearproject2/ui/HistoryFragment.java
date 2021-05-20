package com.example.finalyearproject2.ui;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.finalyearproject2.DetailApprovalActivity;
import com.example.finalyearproject2.DetailHistoryActivity;
import com.example.finalyearproject2.LeaveApplication;
import com.example.finalyearproject2.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class HistoryFragment extends Fragment {
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    TextView TVHistoryNotFound;
    ProgressBar PBHistory;
    ListView ListHistory;
    RelativeLayout layoutHistory;
    int total_history = 0;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_history, container, false);
        PBHistory = root.findViewById(R.id.progressBarHistoryList);
        TVHistoryNotFound = root.findViewById(R.id.textHistoryResultFound);
        ListHistory = root.findViewById(R.id.ListHistoryLeave);
        layoutHistory = root.findViewById(R.id.relativeLayoutHistory);
        ArrayList<LeaveApplication> HistoryList = new ArrayList<>();
        PBHistory.setVisibility(View.VISIBLE);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        fStore.collection("Pending Leave Application").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.getResult().size()>0) {
                    if(task.isSuccessful()){
                        for (QueryDocumentSnapshot doc : task.getResult()){
                            DocumentReference documentReference = fStore.collection("Pending Leave Application").document(doc.getId());
                            documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    String user_id = doc.getId().split("_")[0];
                                    if(fAuth.getCurrentUser().getUid().equals(user_id)){
                                        total_history = 1;
                                        String name = documentSnapshot.getString("Name");
                                        String startDate = documentSnapshot.getString("Start Date");
                                        String endDate = documentSnapshot.getString("End Date");
                                        String leaveType = documentSnapshot.getString("Leave Type");
                                        String applyTime = documentSnapshot.getString("Apply Time");
                                        String status = documentSnapshot.getString("Status");
                                        HistoryList.add(new LeaveApplication(doc.getId(),name, startDate, endDate, leaveType, applyTime, status));
                                        LeaveAdapter adapter = new LeaveAdapter(getContext(), R.layout.customlistview, HistoryList);
                                        ListHistory.setAdapter(adapter);
                                    }
                                }
                            });
                        }
                    }
                }
                fStore.collection("Approved Leave").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.getResult().size()>0) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot doc : task.getResult()) {
                                    DocumentReference documentReference = fStore.collection("Approved Leave").document(doc.getId());
                                    documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                            String user_id = doc.getId().split("_")[0];
                                            if(fAuth.getCurrentUser().getUid().equals(user_id)){
                                                total_history = 1;
                                                String name = documentSnapshot.getString("Name");
                                                String startDate = documentSnapshot.getString("Start Date");
                                                String endDate = documentSnapshot.getString("End Date");
                                                String leaveType = documentSnapshot.getString("Leave Type");
                                                String applyTime = documentSnapshot.getString("Apply Time");
                                                String status = documentSnapshot.getString("Status");
                                                HistoryList.add(new LeaveApplication(doc.getId(),name, startDate, endDate, leaveType, applyTime, status));
                                                LeaveAdapter adapter = new LeaveAdapter(getContext(), R.layout.customlistview, HistoryList);
                                                ListHistory.setAdapter(adapter);
                                            }
                                        }
                                    });
                                }
                            }
                        }
                        fStore.collection("Declined Leave").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if(task.getResult().size()>0) {
                                    if (task.isSuccessful()) {
                                        for (QueryDocumentSnapshot doc : task.getResult()) {
                                            DocumentReference documentReference = fStore.collection("Declined Leave").document(doc.getId());
                                            documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                @Override
                                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                    String user_id = doc.getId().split("_")[0];
                                                    if(fAuth.getCurrentUser().getUid().equals(user_id)){
                                                        total_history += 1;
                                                        String name = documentSnapshot.getString("Name");
                                                        String startDate = documentSnapshot.getString("Start Date");
                                                        String endDate = documentSnapshot.getString("End Date");
                                                        String leaveType = documentSnapshot.getString("Leave Type");
                                                        String applyTime = documentSnapshot.getString("Apply Time");
                                                        String status = documentSnapshot.getString("Status");
                                                        HistoryList.add(new LeaveApplication(doc.getId(),name, startDate, endDate, leaveType, applyTime, status));
                                                        LeaveAdapter adapter = new LeaveAdapter(getContext(), R.layout.customlistview, HistoryList);
                                                        ListHistory.setAdapter(adapter);
                                                    }
                                                }
                                            });
                                        }
                                    }
                                }
                                if(total_history == 0){
                                    layoutHistory.setVisibility(View.GONE);
                                    TVHistoryNotFound.setVisibility(View.VISIBLE);
                                }
                                PBHistory.setVisibility(View.GONE);
                            }
                        });
                    }
                });
            }
        });

        ListHistory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getContext(), DetailHistoryActivity.class);
                intent.putExtra("LeaveID", HistoryList.get(position).getId());
                intent.putExtra("Status", HistoryList.get(position).getStatus());
                startActivity(intent);
            }
        });
        return root;
    }

    public static class LeaveAdapter extends ArrayAdapter<LeaveApplication> {
        private Context mContext;
        private int mResources;

        public LeaveAdapter(@NonNull Context context, int resource, @NonNull ArrayList<LeaveApplication> objects) {
            super(context, resource, objects);
            this.mContext = context;
            this.mResources = resource;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            convertView = layoutInflater.inflate(mResources, parent, false);
            TextView TVname = convertView.findViewById(R.id.textPendingLeaveName);
            TextView TVleavetype = convertView.findViewById(R.id.textPendingLeaveType);
            TextView TVapplyTime = convertView.findViewById(R.id.textPendingTime);
            TextView TVstatus = convertView.findViewById(R.id.textStatus);
            TVname.setText(getItem(position).getName());
            TVleavetype.setText(getItem(position).getLeaveType());
            TVapplyTime.setText("Applied on\n" + getItem(position).getCurrentTime());
            TVstatus.setText(getItem(position).getStatus());
            return convertView;
        }
    }
}