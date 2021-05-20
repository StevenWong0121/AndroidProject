package com.example.finalyearproject2.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.finalyearproject2.DetailApprovalActivity;
import com.example.finalyearproject2.LeaveApplication;
import com.example.finalyearproject2.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ApprovalFragment extends Fragment {
    ProgressBar PBList;
    ListView pendingList;
    TextView TVResult;
    RelativeLayout relativeLayout;
    FirebaseFirestore fStore;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_approval, container, false);
        PBList = root.findViewById(R.id.progressBarList);
        pendingList = root.findViewById(R.id.ListPendingLeave);
        TVResult = root.findViewById(R.id.textResultFound);
        relativeLayout = root.findViewById(R.id.relativeLayoutPending);
        ArrayList<LeaveApplication> arrayList = new ArrayList<>();
        PBList.setVisibility(View.VISIBLE);

        fStore = FirebaseFirestore.getInstance();
        fStore.collection("Pending Leave Application").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.getResult().size() == 0){
                    PBList.setVisibility(View.GONE);
                    relativeLayout.setVisibility(View.GONE);
                    TVResult.setVisibility(View.VISIBLE);
                }
                else{
                    PBList.setVisibility(View.GONE);
                    relativeLayout.setVisibility(View.VISIBLE);
                    TVResult.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            DocumentReference dr = fStore.collection("Pending Leave Application").document(doc.getId());
                            dr.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    String name = documentSnapshot.getString("Name");
                                    String startDate = documentSnapshot.getString("Start Date");
                                    String endDate = documentSnapshot.getString("End Date");
                                    String leaveType = documentSnapshot.getString("Leave Type");
                                    String applyTime = documentSnapshot.getString("Apply Time");
                                    String status = documentSnapshot.getString("Status");
                                    arrayList.add(new LeaveApplication(doc.getId(),name, startDate, endDate, leaveType, applyTime, status));
                                    PendingLeaveAdapter adapter = new PendingLeaveAdapter(getContext(), R.layout.customlistview, arrayList);
                                    pendingList.setAdapter(adapter);
                                }
                            });
                        }
                    }
                }
            }
        });

        pendingList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getContext(), DetailApprovalActivity.class);
                intent.putExtra("LeaveID", arrayList.get(position).getId());
                startActivity(intent);
            }
        });
        return root;
    }

    public static class PendingLeaveAdapter extends ArrayAdapter<LeaveApplication> {
        private Context mContext;
        private int mResources;

        public PendingLeaveAdapter(@NonNull Context context, int resource, @NonNull ArrayList<LeaveApplication> objects) {
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