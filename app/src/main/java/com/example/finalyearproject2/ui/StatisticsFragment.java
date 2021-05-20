package com.example.finalyearproject2.ui;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.example.finalyearproject2.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;

public class StatisticsFragment extends Fragment {
    ProgressBar PBPieChart, PBBarChart;
    BarChart barChart;
    PieChart pieChart;
    FirebaseFirestore fStore;
    int pendingLeaveApplied = 0, approvedLeave = 0, declinedLeave = 0;
    double sick_pending = 0.01, sick_approved = 0.01, sick_declined = 0.01, maternity_pending = 0.01, maternity_approved = 0.01, maternity_declined = 0.01, annual_pending = 0.01, annual_approved = 0.01, annual_declined = 0.01, emergency_pending = 0.01, emergency_approved = 0.01, emergency_declined = 0.01, study_pending = 0.01, study_approved = 0.01, study_declined = 0.01, others_pending = 0.01, others_approved = 0.01, others_declined = 0.01;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_statistics, container, false);
        fStore = FirebaseFirestore.getInstance();
        barChart = root.findViewById(R.id.BarChart);
        pieChart = root.findViewById(R.id.PieChart);
        PBPieChart = root.findViewById(R.id.progressBarPieChart);
        PBBarChart = root.findViewById(R.id.progressBarBarChart);

        fStore.collection("Pending Leave Application").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                pendingLeaveApplied += task.getResult().size();
                for(QueryDocumentSnapshot documentSnapshot :task.getResult()){
                    DocumentReference doc = fStore.collection("Pending Leave Application").document(documentSnapshot.getId());
                    doc.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            String leaveType = documentSnapshot.getString("Leave Type");
                            switch (leaveType) {
                                case "Sick Leave":
                                    if(sick_pending == 0.01){
                                        sick_pending += 0.99;
                                    }
                                    else{
                                        sick_pending += 1;
                                    }
                                    break;
                                case "Maternity Leave":
                                    if(maternity_pending == 0.01){
                                        maternity_pending += 0.99;
                                    }
                                    else {
                                        maternity_pending += 1;
                                    }
                                    break;
                                case "Annual Leave":
                                    if(annual_pending == 0.01){
                                        annual_pending += 0.99;
                                    }
                                    else {
                                        annual_pending += 1;
                                    }
                                    break;
                                case "Emergency Leave":
                                    if(emergency_pending == 0.01){
                                        emergency_pending += 0.99;
                                    }
                                    else {
                                        emergency_pending += 1;
                                    }break;

                                case "Study Leave":
                                    if(study_pending == 0.01){
                                        study_pending += 0.99;
                                    }
                                    else {
                                        study_pending += 1;
                                    }
                                    break;
                                default:
                                    if(others_pending == 0.01){
                                        others_pending += 0.99;
                                    }
                                    else {
                                        others_pending += 1;
                                    }
                                    break;
                            }
                        }
                    });
                }

                fStore.collection("Approved Leave").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        approvedLeave += task.getResult().size();
                        for(QueryDocumentSnapshot documentSnapshot :task.getResult()){
                            DocumentReference doc = fStore.collection("Approved Leave").document(documentSnapshot.getId());
                            doc.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    String leaveType = documentSnapshot.getString("Leave Type");
                                    switch (leaveType) {
                                        case "Sick Leave":
                                            if(sick_approved == 0.01){
                                                sick_approved += 0.99;
                                            }
                                            else {
                                                sick_approved += 1;
                                            }
                                            break;
                                        case "Maternity Leave":
                                            if(maternity_approved == 0.01){
                                                maternity_approved += 0.99;
                                            }
                                            else {
                                                maternity_approved += 1;
                                            }
                                            break;
                                        case "Annual Leave":
                                            if(annual_approved == 0.01){
                                                annual_approved += 0.99;
                                            }
                                            else {
                                                annual_approved += 1;
                                            }
                                            break;
                                        case "Emergency Leave":
                                            if(emergency_approved == 0.01){
                                                emergency_approved += 0.99;
                                            }
                                            else {
                                                emergency_approved += 1;
                                            }break;

                                        case "Study Leave":
                                            if(study_approved == 0.01){
                                                study_approved += 0.99;
                                            }
                                            else {
                                                study_approved += 1;
                                            }
                                            break;
                                        default:
                                            if(others_approved == 0.01){
                                                others_approved += 0.99;
                                            }
                                            else {
                                                others_approved += 1;
                                            }
                                            break;
                                    }
                                }
                            });
                        }
                        fStore.collection("Declined Leave").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                declinedLeave += task.getResult().size();
                                for(QueryDocumentSnapshot documentSnapshot :task.getResult()){
                                    DocumentReference doc = fStore.collection("Declined Leave").document(documentSnapshot.getId());
                                    doc.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                            String leaveType = documentSnapshot.getString("Leave Type");
                                            switch(leaveType) {
                                                case "Sick Leave":
                                                    if(sick_declined == 0.01){
                                                        sick_declined += 0.99;
                                                    }
                                                    else{
                                                        sick_declined += 1;
                                                    }
                                                    break;
                                                case "Maternity Leave":
                                                    if(maternity_declined == 0.01){
                                                        maternity_declined += 0.99;
                                                    }
                                                    else{
                                                        maternity_declined += 1;
                                                    }
                                                    break;
                                                case "Annual Leave":
                                                    if(annual_declined == 0.01){
                                                        annual_declined += 0.99;
                                                    }
                                                    else{
                                                        annual_declined += 1;
                                                    }
                                                    break;
                                                case "Emergency Leave":
                                                    if(emergency_declined == 0.01){
                                                        emergency_declined += 0.99;
                                                    }
                                                    else{
                                                        emergency_declined += 1;
                                                    }
                                                    break;
                                                case "Study Leave":
                                                    if(study_declined == 0.01){
                                                        study_declined += 0.99;
                                                    }
                                                    else{
                                                        study_declined += 1;
                                                    }
                                                    break;
                                                case "Others":
                                                    if(others_declined == 0.01){
                                                        others_declined += 0.99;
                                                    }
                                                    else{
                                                        others_declined += 1;
                                                    }
                                                    break;
                                            }
                                        }
                                    });
                                }
                                ArrayList<PieEntry> pieInfo = new ArrayList<>();
                                pieInfo.add(new PieEntry(pendingLeaveApplied,"Total Pending Leave"));
                                pieInfo.add(new PieEntry(approvedLeave,"Total Leave Approved"));
                                pieInfo.add(new PieEntry(declinedLeave,"Total Leave Declined"));

                                ArrayList<Integer> colors = new ArrayList<>();
                                for(int color : ColorTemplate.MATERIAL_COLORS){
                                    colors.add(color);
                                }

                                for(int color : ColorTemplate.VORDIPLOM_COLORS){
                                    colors.add(color);
                                }
                                PieDataSet pieDataSet = new PieDataSet(pieInfo, "Leave Status");
                                pieDataSet.setColors(colors);
                                PieData pieData = new PieData(pieDataSet);
                                pieData.setDrawValues(true);
                                pieData.setValueFormatter(new PercentFormatter(pieChart));
                                pieData.setValueTextSize(12f);
                                pieData.setValueTextColor(Color.BLACK);
                                pieChartAppearance();
                                pieChart.setData(pieData);
                                pieChart.invalidate();
                                PBPieChart.setVisibility(View.GONE);
                                pieChart.setVisibility(View.VISIBLE);

                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        ArrayList<BarEntry> barSick = new ArrayList<>();
                                        barSick.add(new BarEntry(1, (float) sick_pending));
                                        barSick.add(new BarEntry(2, (float) sick_approved));
                                        barSick.add(new BarEntry(3, (float) sick_declined));

                                        ArrayList<BarEntry> barMaternity = new ArrayList<>();
                                        barMaternity.add(new BarEntry(1, (float) maternity_pending));
                                        barMaternity.add(new BarEntry(2, (float) maternity_approved));
                                        barMaternity.add(new BarEntry(3, (float) maternity_declined));

                                        ArrayList<BarEntry> barAnnual = new ArrayList<>();
                                        barAnnual.add(new BarEntry(1, (float) annual_pending));
                                        barAnnual.add(new BarEntry(2, (float) annual_approved));
                                        barAnnual.add(new BarEntry(3, (float) annual_declined));

                                        ArrayList<BarEntry> barEmergency = new ArrayList<>();
                                        barEmergency.add(new BarEntry(1, (float) emergency_pending));
                                        barEmergency.add(new BarEntry(2, (float) emergency_approved));
                                        barEmergency.add(new BarEntry(3, (float) emergency_declined));

                                        ArrayList<BarEntry> barStudy = new ArrayList<>();
                                        barStudy.add(new BarEntry(1, (float) study_pending));
                                        barStudy.add(new BarEntry(2, (float) study_approved));
                                        barStudy.add(new BarEntry(3, (float) study_declined));

                                        ArrayList<BarEntry> barOthers = new ArrayList<>();
                                        barOthers.add(new BarEntry(1, (float) others_pending));
                                        barOthers.add(new BarEntry(2, (float) others_approved));
                                        barOthers.add(new BarEntry(3, (float) others_declined));

                                        BarDataSet barSet1 = new BarDataSet(barSick, "Sick Leave");
                                        barSet1.setColor(Color.RED);
                                        BarDataSet barSet2 = new BarDataSet(barMaternity, "Maternity Leave");
                                        barSet2.setColor(Color.BLUE);
                                        BarDataSet barSet3 = new BarDataSet(barEmergency, "Annual Leave");
                                        barSet3.setColor(Color.GREEN);
                                        BarDataSet barSet4 = new BarDataSet(barStudy, "Emergency Leave");
                                        barSet4.setColor(Color.YELLOW);
                                        BarDataSet barSet5 = new BarDataSet(barAnnual, "Study Leave");
                                        barSet5.setColor(Color.MAGENTA);
                                        BarDataSet barSet6 = new BarDataSet(barOthers, "Others");
                                        barSet6.setColor(Color.CYAN);

                                        BarData data = new BarData(barSet1, barSet2, barSet3, barSet4, barSet5, barSet6);
                                        barChart.setData(data);
                                        data.setBarWidth(0.10f);
                                        data.setValueTextSize(7f);

                                        Legend leg = barChart.getLegend();
                                        leg.setTextColor(Color.WHITE);
                                        leg.setWordWrapEnabled(true);
                                        leg.setTextSize(10);

                                        String[] LeaveTypes = new String[]{"Pending Leave", "Approved Leave", "Declined Leave"};
                                        XAxis xAxis = barChart.getXAxis();
                                        xAxis.setValueFormatter(new IndexAxisValueFormatter(LeaveTypes));
                                        xAxis.setCenterAxisLabels(true);
                                        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                                        xAxis.setGranularity(1);
                                        xAxis.setGranularityEnabled(true);

                                        barChart.setDragEnabled(true);
                                        barChart.getXAxis().setAxisMinimum(0);
                                        barChart.groupBars(0,  0.4f, 0);
                                        barChart.setDrawGridBackground(false);
                                        barChart.getDescription().setEnabled(false);
                                        barChart.getData().setDrawValues(false);
                                        barChart.getAxisLeft().setAxisMinimum(0);
                                        barChart.getAxisLeft().setTextColor(Color.WHITE);
                                        barChart.getAxisRight().setTextColor(Color.WHITE);
                                        barChart.getXAxis().setTextColor(Color.WHITE);
                                        barChart.setVisibility(View.VISIBLE);
                                        PBBarChart.setVisibility(View.GONE);
                                        barChart.invalidate();

                                    }
                                }, 1500);
                            }
                        });
                    }
                });
            }
        });


        return root;
    }

    public void pieChartAppearance(){
       pieChart.setEntryLabelTextSize(18);
       pieChart.setEntryLabelColor(Color.BLACK);
       pieChart.setCenterText("Leave Status");
       pieChart.setCenterTextSize(24);
       pieChart.getDescription().setEnabled(false);

       Legend l = pieChart.getLegend();
       l.setTextColor(Color.WHITE);
       l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
       l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
       l.setOrientation(Legend.LegendOrientation.VERTICAL);
       l.setDrawInside(false);
       l.setEnabled(true);
    }
}