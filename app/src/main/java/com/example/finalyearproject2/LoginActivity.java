package com.example.finalyearproject2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

    CheckBox Remember;
    CustomTextInputLayout Layoutusername, Layoutpassword;
    EditText ETusername, ETpassword;
    TextView linkedtextOnClick, ForgetPasswordText;
    Button LoginBtn;
    ProgressBar pbLogin;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z0-9]+\\.+[a-z]+";
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    boolean SignInValid = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        pbLogin = findViewById(R.id.progressBarSignIn);
        Layoutusername = findViewById(R.id.NewEventsTextLayout);
        Layoutpassword = findViewById(R.id.RetypeResetPasswordTextLayout);
        ETusername = findViewById(R.id.NewEventsTextField);
        ETpassword = findViewById(R.id.RetypeResetPasswordTextField);
        LoginBtn = findViewById(R.id.NewEventsButton);
        linkedtextOnClick = findViewById(R.id.LinkedTextToRegister);
        ForgetPasswordText = findViewById(R.id.ForgetPasswordText);
        Remember = findViewById(R.id.checkBoxRMBMe);
        String mystring = new String("Forget Password ?");
        SpannableString content = new SpannableString(mystring);
        content.setSpan(new UnderlineSpan(), 0, mystring.length(), 0);
        ForgetPasswordText.setText(content);
        ForgetPasswordText.setVisibility(View.INVISIBLE);

        SharedPreferences preferences = getSharedPreferences("checkbox",MODE_PRIVATE);
        String checked = preferences.getString("remember", "");
        if(checked.equals("true")) {
            ETusername.setFocusable(false);
            ETpassword.setFocusable(false);
            Remember.setChecked(true);
            pbLogin.setVisibility(View.VISIBLE);
            DocumentReference df = fStore.collection("Users").document(fAuth.getCurrentUser().getUid());
            df.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    String email = documentSnapshot.getString("Username");
                    ETusername.setText(email);
                    ETpassword.setText("***************");
                    Toast.makeText(LoginActivity.this, "Login Successfully.", Toast.LENGTH_LONG).show();
                }
            });
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    CheckUserPrivilege(fAuth.getCurrentUser().getUid());
                }
            }, 2500);
        }

        linkedtextOnClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        Remember.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(compoundButton.isChecked()){
                    SharedPreferences preferences = getSharedPreferences("checkbox", MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("remember","true");
                    editor.apply();
                }
                else if(!(compoundButton.isChecked())) {
                    SharedPreferences preferences = getSharedPreferences("checkbox", MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("remember", "false");
                    editor.apply();
                }
            }
        });

        ForgetPasswordText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText resetMail = new EditText(v.getContext());
                AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(v.getContext());
                passwordResetDialog.setTitle("Reset Password");
                passwordResetDialog.setMessage("Enter your Email to receive reset link.");
                passwordResetDialog.setView(resetMail);

                passwordResetDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String mail = resetMail.getText().toString().trim();
                        fAuth.sendPasswordResetEmail(mail).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(LoginActivity.this,"Reset link has been sent to your Email.", Toast.LENGTH_LONG).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(LoginActivity.this, "Error! Reset link is not sent. " + e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                });

                passwordResetDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                passwordResetDialog.create().show();
            }
        });

        LoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = ETusername.getText().toString();
                String password = ETpassword.getText().toString();
                //Conditions for username layout
                if(TextUtils.isEmpty(username)){
                    Layoutusername.setError("Email is required.");
                    SignInValid = false;
                }
                else if(!(username.matches(emailPattern))){
                    Layoutusername.setError("Invalid email.");
                    SignInValid = false;
                }
                else{
                    Layoutusername.setError(null);
                    SignInValid = true;
                }
                //Conditions for password layout
                if(TextUtils.isEmpty(password)) {
                    Layoutpassword.setError("Please enter your password.");
                    SignInValid = false;
                }
                else {
                    Layoutpassword.setError(null);
                    SignInValid = true;
                }
                ETusername.setFocusable(false);
                ETpassword.setFocusable(false);

                if(SignInValid){
                    pbLogin.setVisibility(View.VISIBLE);
                    fAuth.signInWithEmailAndPassword(username, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            CheckUserPrivilege(authResult.getUser().getUid());
                            Toast.makeText(LoginActivity.this, "Login Successfully.", Toast.LENGTH_LONG).show();
                            ForgetPasswordText.setVisibility(View.INVISIBLE);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            pbLogin.setVisibility(View.GONE);
                            ETpassword.setText(null);
                            Toast.makeText(LoginActivity.this, "Login Failed. "+e.getMessage(), Toast.LENGTH_LONG).show();
                            ETusername.setFocusableInTouchMode(true);
                            ETpassword.setFocusableInTouchMode(true);
                            ETusername.setFocusable(true);
                            ETpassword.setFocusable(true);
                            ForgetPasswordText.setVisibility(View.VISIBLE);

                        }
                    });
                }
            }
        });
    }

    private void CheckUserPrivilege(String uid) {
        DocumentReference df = fStore.collection("Users").document(uid);
        df.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.getString("Privilege").equals("Administrator")){
                    startActivity(new Intent(LoginActivity.this, NavigationAdminActivity.class));
                }
                else if(documentSnapshot.getString("Privilege").equals("Employee")){
                    startActivity(new Intent(LoginActivity.this, NavigationActivity.class));
                }
                finish();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onBackPressed()
    {
    }

    public void closeKeyBoard(View view){
        view = LoginActivity.this.getCurrentFocus();
        if(view != null){
            InputMethodManager imm = (InputMethodManager) LoginActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}