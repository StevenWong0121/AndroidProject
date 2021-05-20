package com.example.finalyearproject2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    CustomTextInputLayout LayoutUsername, LayoutPrivilege, LayoutPassword, LayoutRetypePassword, LayoutName;
    EditText ETPrivilegeType, ETUsername, ETPassword, ETRetypePassword, ETName;
    Button BtnRegister;
    TextView linkedtextOnClick;
    ProgressBar pbRegister;
    private final String[] privilegesType= {"Administrator", "Employee"};
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z0-9]+\\.+[a-z]+";
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ETPrivilegeType = findViewById(R.id.PrivilegesTextField);
        ETPrivilegeType.setFocusable(false);
        ETPrivilegeType.setClickable(true);
        final ArrayAdapter<String> privilegeTypesSelection = new  ArrayAdapter<String>(RegisterActivity.this,android.R.layout.simple_spinner_dropdown_item, privilegesType);
        linkedtextOnClick = findViewById(R.id.LinkedTextToLogIn);
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        linkedtextOnClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        ETPrivilegeType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(RegisterActivity.this).setAdapter(privilegeTypesSelection, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ETPrivilegeType.setText(privilegesType[which].toString());
                        dialog.dismiss();
                    }
                }).create().show();
            }
        });

        ETUsername = findViewById(R.id.UserNameTextField);
        ETName = findViewById(R.id.NameTextField);
        ETPassword = findViewById(R.id.PasswordTextField);
        ETRetypePassword = findViewById(R.id.ConfirmPasswordTextField);
        BtnRegister = findViewById(R.id.RegisterButton);
        LayoutUsername = findViewById(R.id.SignUpUserNameTextLayout);
        LayoutName = findViewById(R.id.SignUpNameTextLayout);
        LayoutPrivilege = findViewById(R.id.PrivilegesTextLayout);
        LayoutPassword = findViewById(R.id.SignUpPasswordTextLayout);
        LayoutRetypePassword = findViewById(R.id.ConfirmPasswordLayout);
        pbRegister = findViewById(R.id.progressBarRegister);

        BtnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int RegistrationValid = 0;
                String email = ETUsername.getText().toString().trim();
                String name = ETName.getText().toString().trim();
                String privilege = ETPrivilegeType.getText().toString().trim();
                String password = ETPassword.getText().toString().trim();
                String retypePassword = ETRetypePassword.getText().toString().trim();
                //Conditions for username layout
                if(TextUtils.isEmpty(email)){
                    LayoutUsername.setError("Email is required.");
                }
                else if (!(email.matches(emailPattern))){
                    LayoutUsername.setError("Invalid email.");
                }
                else{
                    LayoutUsername.setError(null);
                    RegistrationValid += 1;
                }
                //Conditions for name layout
                if(TextUtils.isEmpty(name)){
                    LayoutName.setError("Name is required.");
                }
                else{
                    LayoutName.setError(null);
                    RegistrationValid += 1;
                }
                //Conditions for privilege layout
                if(TextUtils.isEmpty(privilege)){
                    LayoutPrivilege.setError("Please select your privilege.");
                }
                else{
                    LayoutPrivilege.setError(null);
                    RegistrationValid += 1;
                }
                //Conditions for password layout
                if(TextUtils.isEmpty(password)){
                    LayoutPassword.setError("Please enter your password.");
                }
                else{
                    LayoutPassword.setError(null);
                    RegistrationValid += 1;
                }
                //Conditions for retype-password layout
                if(TextUtils.isEmpty(retypePassword)){
                    LayoutRetypePassword.setError("Please re-type your password.");
                }
                else if(!(password.equals(retypePassword))) {
                    LayoutRetypePassword.setError("Make sure both of your passwords are the same.");
                }
                else{
                    LayoutRetypePassword.setError(null);
                    RegistrationValid += 1;
                }

                if(RegistrationValid == 5) {
                    pbRegister.setVisibility(View.VISIBLE);
                    BtnRegister.setFocusable(false);
                    ETUsername.setFocusable(false);
                    ETName.setFocusable(false);
                    ETPrivilegeType.setClickable(false);
                    ETPassword.setFocusable(false);
                    ETRetypePassword.setFocusable(false);
                    fAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                pbRegister.setVisibility(View.GONE);
                                FirebaseUser user = fAuth.getCurrentUser();
                                Toast.makeText(RegisterActivity.this, "This account has been successfully registered. Please proceed to Login.", Toast.LENGTH_LONG).show();

                                DocumentReference df = fStore.collection("Users").document(user.getUid());
                                Map<String, Object> userInfo = new HashMap<>();
                                userInfo.put("Username", email);
                                userInfo.put("Name", name);
                                userInfo.put("Privilege", privilege);
                                df.set(userInfo);

                                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                startActivity(intent);
                            } else {
                                pbRegister.setVisibility(View.GONE);
                                Toast.makeText(RegisterActivity.this, "Fail to register the account, " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }

                        }
                    });
                }
                else{
                    Toast.makeText(RegisterActivity.this, "Please complete all of the fields above", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    @Override
    public void onBackPressed()
    {
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    public void closeKeyBoard(View view){
        view = RegisterActivity.this.getCurrentFocus();
        if(view != null){
            InputMethodManager imm = (InputMethodManager) RegisterActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}