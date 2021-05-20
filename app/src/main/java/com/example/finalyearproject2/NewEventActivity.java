package com.example.finalyearproject2;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class NewEventActivity extends AppCompatActivity {
    EditText ETNewEvents;
    CustomTextInputLayout LayoutNewEvents;
    Button NewEventButton;
    ImageView BackImage;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String updatedEvent, ID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_event);
        ETNewEvents = findViewById(R.id.NewEventsTextField);
        LayoutNewEvents = findViewById(R.id.NewEventsTextLayout);
        NewEventButton = findViewById(R.id.NewEventsButton);
        BackImage = findViewById(R.id.imageQuitAddNewEvents);
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        ID = getIntent().getStringExtra("Id");

        NewEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int validAddNewEvent = 0;
                String newEvent = ETNewEvents.getText().toString().trim();
                if(TextUtils.isEmpty(newEvent)){
                    LayoutNewEvents.setError("New event is required");
                }
                else{
                    LayoutNewEvents.setError(null);
                    validAddNewEvent += 1;
                }

                if(validAddNewEvent == 1){
                    DocumentReference ref = fStore.collection("Users").document(fAuth.getCurrentUser().getUid()).collection("Calendar").document(ID);
                    ref.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            String event = documentSnapshot.getString("Events");
                            if(event != null){
                                Log.d("tag1", "not null");
                                updatedEvent = event + ", " + newEvent;
                                Map<String, Object> events = new HashMap<>();
                                events.put("Events", updatedEvent);
                                ref.update(events);
                            }
                            else{
                                Log.d("tag1", "null");
                                updatedEvent = newEvent;
                                Map<String, Object> events = new HashMap<>();
                                events.put("Events", updatedEvent);
                                ref.set(events);
                            }
                            Toast.makeText(NewEventActivity.this,"New event added successfully",Toast.LENGTH_LONG).show();
                            DocumentReference docRef = fStore.collection("Users").document(fAuth.getCurrentUser().getUid());
                            docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    Intent intent;
                                    String privilege = documentSnapshot.getString("Privilege");
                                    if(privilege.equals("Administrator")){
                                        intent = new Intent(NewEventActivity.this, NavigationAdminActivity.class);
                                    }
                                    else{
                                        intent = new Intent(NewEventActivity.this, NavigationActivity.class);
                                    }
                                    startActivity(intent);
                                }
                            });
                        }
                    });
                }
            }
        });

        BackImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void closeKeyBoard(View view){
        view = this.getCurrentFocus();
        if(view != null){
            InputMethodManager imm = (InputMethodManager) NewEventActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
