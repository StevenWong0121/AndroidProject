package com.example.finalyearproject2;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class ChangePasswordActivity extends AppCompatActivity {
    ProgressBar PBChangePassword;
    EditText ETCurrentPassword, ETNewPassword, ETRetypeNewPassword;
    CustomTextInputLayout LayoutCurrentPassword, LayoutNewPassword, LayoutRetypeNewPassword;
    Button BtnSaveNewPassword;
    ImageView exit;
    FirebaseFirestore fStore;
    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        exit = findViewById(R.id.exitChangePassword);
        BtnSaveNewPassword = findViewById(R.id.ResetPasswordSaveButton);
        ETCurrentPassword = findViewById(R.id.CurrentPasswordTextField);
        ETNewPassword = findViewById(R.id.ResetPasswordTextField);
        ETRetypeNewPassword = findViewById(R.id.RetypeResetPasswordTextField);
        LayoutCurrentPassword = findViewById(R.id.CurrentPasswordTextLayout);
        LayoutNewPassword = findViewById(R.id.ResetPasswordTextLayout);
        LayoutRetypeNewPassword = findViewById(R.id.RetypeResetPasswordTextLayout);
        PBChangePassword = findViewById(R.id.progressBarChangePassword);
        PBChangePassword.setVisibility(View.GONE);

        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        BtnSaveNewPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fAuth = FirebaseAuth.getInstance();
                fStore = FirebaseFirestore.getInstance();
                String currentPassword = ETCurrentPassword.getText().toString().trim();
                String newPassword = ETNewPassword.getText().toString().trim();
                String retypeNewPassword = ETRetypeNewPassword.getText().toString().trim();
                int validChangePassword = 0;
                //Conditions for current password
                if(TextUtils.isEmpty(currentPassword)) {
                    LayoutCurrentPassword.setError("Current password is required");
                }
                else{
                    LayoutCurrentPassword.setError(null);
                    validChangePassword += 1;
                }
                //Conditions for new password
                if(TextUtils.isEmpty(newPassword)){
                    LayoutNewPassword.setError("New Password is required");
                }
                else{
                    LayoutNewPassword.setError(null);
                    validChangePassword += 1;
                }
                //Conditions for retype new password
                if(TextUtils.isEmpty(retypeNewPassword)){
                    LayoutRetypeNewPassword.setError("Re-enter password is required");
                }
                else if(!(newPassword.equals(retypeNewPassword))){
                    LayoutRetypeNewPassword.setError("Make sure both of the passwords are the same");
                }
                else{
                    LayoutRetypeNewPassword.setError(null);
                    validChangePassword += 1;
                }
                if(validChangePassword == 3) {
                    ETCurrentPassword.setFocusable(false);
                    ETNewPassword.setFocusable(false);
                    ETRetypeNewPassword.setFocusable(false);
                    FirebaseUser user = fAuth.getCurrentUser();
                    AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), currentPassword);
                    user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                PBChangePassword.setVisibility(View.VISIBLE);
                                user.updatePassword(newPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> tasks) {
                                        if (tasks.isSuccessful()) {
                                            if(!(newPassword.equals(currentPassword))){
                                                Toast.makeText(getBaseContext(), "Password update successfully, you are required to re-login", Toast.LENGTH_LONG).show();
                                                SharedPreferences preferences = getSharedPreferences("checkbox", MODE_PRIVATE);
                                                SharedPreferences.Editor editor = preferences.edit();
                                                editor.putString("remember","false");
                                                editor.apply();
                                                FirebaseAuth.getInstance().signOut();
                                                startActivity(new Intent(ChangePasswordActivity.this, LoginActivity.class));
                                            }
                                            else{
                                                Toast.makeText(getBaseContext(), "Password update unsuccessfully, you are required to enter a different new password", Toast.LENGTH_LONG).show();
                                                PBChangePassword.setVisibility(View.GONE);
                                            }

                                        } else {
                                            Toast.makeText(ChangePasswordActivity.this, "Password update unsuccessfully, something went wrong", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                            }
                            else{
                                LayoutCurrentPassword.setError("Current password is incorrect");
                            }
                        }
                    });
                }
                else{
                    Toast.makeText(ChangePasswordActivity.this, "Make sure everything entered correctly", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    public void closeKeyBoard(View view){
        view = ChangePasswordActivity.this.getCurrentFocus();
        if(view != null){
            InputMethodManager imm = (InputMethodManager) ChangePasswordActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

}