package com.example.finalyearproject2.ui;
import android.app.usage.UsageEvents;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.applandeo.materialcalendarview.EventDay;
import com.applandeo.materialcalendarview.listeners.OnDayClickListener;
import com.example.finalyearproject2.NavigationActivity;
import com.example.finalyearproject2.NavigationAdminActivity;
import com.example.finalyearproject2.NewEventActivity;
import com.example.finalyearproject2.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.applandeo.materialcalendarview.CalendarView;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CalendarFragment extends Fragment {
    CalendarView calendar;
    Calendar date = Calendar.getInstance();
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    String selectedDate = dateFormat.format(date.getTime());
    FirebaseFirestore fStore;
    FirebaseAuth fAuth;
    long dateToID = 0;
    List<String> stringList;
    List<EventDay> events = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_calendar, container, false);
        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        calendar = root.findViewById(R.id.CalendarView);

        fStore.collection("Users").document(fAuth.getCurrentUser().getUid()).collection("Calendar").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for (QueryDocumentSnapshot doc : task.getResult()) {
                        DocumentReference dr = fStore.collection("Users").document(fAuth.getCurrentUser().getUid()).collection("Calendar").document(doc.getId());
                        dr.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                Calendar cal= GregorianCalendar.getInstance();
                                Long date = Long.parseLong(doc.getId());
                                cal.setTimeInMillis(date);
                                events.add(new EventDay(cal, R.drawable.highlight_events));
                                calendar.setEvents(events);
                            }
                        });
                    }
                }
            }
        });

        calendar.setOnDayClickListener(new OnDayClickListener() {
            @Override
            public void onDayClick(EventDay eventDay) {
                Calendar clickedDayCalendar = eventDay.getCalendar();
                final int year = clickedDayCalendar.get(Calendar.YEAR);
                int month = clickedDayCalendar.get(Calendar.MONTH) + 1;
                final int day = clickedDayCalendar.get(Calendar.DAY_OF_MONTH);
                selectedDate = day + "/" + month + "/" + year;
                try {
                    dateToID = dateFormat.parse(selectedDate).getTime();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                LayoutInflater layoutInflater = getActivity().getLayoutInflater();
                View EventLists = layoutInflater.inflate(R.layout.calendar_event, null);
                ListView list = EventLists.findViewById(R.id.EventList);
                TextView dateText = EventLists.findViewById(R.id.textCalendarEventDate);
                TextView NotFound = EventLists.findViewById(R.id.EventNotFoundText);

                DocumentReference ref = fStore.collection("Users").document(fAuth.getCurrentUser().getUid()).collection("Calendar").document(String.valueOf(dateToID));
                ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setView(EventLists);
                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                        builder.setPositiveButton("Add new Event", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(getActivity(), NewEventActivity.class);
                                intent.putExtra("Id", String.valueOf(dateToID));
                                startActivity(intent);
                            }
                        });
                        dateText.setText(selectedDate);
                        if (task.isSuccessful()) {
                            String events = task.getResult().getString("Events");
                            if(events != null) {
                                String[] eventsArray = events.split(", ");
                                stringList = Arrays.asList(eventsArray);
                                ArrayAdapter<String> adapter = new ArrayAdapter(getContext(), android.R.layout.simple_list_item_1, eventsArray);
                                list.setAdapter(adapter);
                            }
                            else{
                                list.setVisibility(View.GONE);
                                NotFound.setVisibility(View.VISIBLE);
                            }

                        }
                        builder.create().show();
                        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                String selectedEvent = stringList.get(position);
                                DocumentReference docRef = fStore.collection("Users").document(fAuth.getCurrentUser().getUid()).collection("Calendar").document(String.valueOf(dateToID));
                                docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        String events = documentSnapshot.getString("Events");
                                        if(events != null){
                                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                            builder.setTitle("Deletion of events");
                                            builder.setMessage("Are you sure to delete the event?");
                                            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                }
                                            });
                                            builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    if(events.contains(", ")){
                                                        String latestEvents = events.replace(selectedEvent + ", ", "");
                                                        Map<String, Object> updatedEvent = new HashMap<>();
                                                        updatedEvent.put("Events", latestEvents);
                                                        docRef.update(updatedEvent);
                                                    }
                                                    else{
                                                        docRef.delete();
                                                    }

                                                    DocumentReference documentReference = fStore.collection("Users").document(fAuth.getCurrentUser().getUid());
                                                    documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                        @Override
                                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                            Intent intent;
                                                            String privilege = documentSnapshot.getString("Privilege");
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
                                            });
                                            builder.create().show();
                                        }
                                    }
                                });
                            }
                        });
                    }
                });

            }
        });
        return root;
    }
}