package com.example.finalyearproject2.ui;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.finalyearproject2.ChangePasswordActivity;
import com.example.finalyearproject2.CustomTextInputLayout;
import com.example.finalyearproject2.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;

public class ProfileFragment extends Fragment {
    ImageView imageProfile, imageCamera;
    ProgressBar PBEditProfile;
    View oriDiv, editDiv;
    Button BtnResetPassword;
    EditText ETName, ETPhone, ETAge, ETMail, ETGender;
    TextView TVProfileName, TVPhoneNo, TVEmail, TVEdit, TVSaveEdit, TVAge, TVGender;
    CustomTextInputLayout LayoutName, LayoutPhoneNumber, LayoutAge, LayoutMail, LayoutGender;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z0-9]+\\.+[a-z]+";
    Bitmap profile_picture;
    private String[] genders= {"Male", "Female"};
    public static final int CAMERA_ACTION_CODE = 1;
    public static final int GALLERY_ACTION_CODE = 2;


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_profile, container, false);
        imageCamera = root.findViewById(R.id.imageCaptureImage);
        imageProfile = root.findViewById(R.id.imageProfile);
        PBEditProfile = root.findViewById(R.id.progressBarEditProfile);
        oriDiv = root.findViewById(R.id.OriDivider);
        editDiv = root.findViewById(R.id.editTextDivider);
        ETAge = root.findViewById(R.id.EditAge);
        ETName = root.findViewById(R.id.EditName);
        ETPhone = root.findViewById(R.id.EditPhone);
        ETMail = root.findViewById(R.id.EditEmail);
        ETGender = root.findViewById(R.id.EditGender);
        BtnResetPassword = root.findViewById(R.id.resetPasswordButton);
        TVSaveEdit = root.findViewById(R.id.textSaveEdit);
        TVEdit = root.findViewById(R.id.textEdit);
        TVAge = root.findViewById(R.id.textAge);
        TVProfileName = root.findViewById(R.id.textProfileName);
        TVPhoneNo = root.findViewById(R.id.textPhoneNo);
        TVEmail = root.findViewById(R.id.textEmail);
        TVGender = root.findViewById(R.id.textGender);
        LayoutAge = root.findViewById(R.id.EditAgeTextLayout);
        LayoutName = root.findViewById(R.id.EditNameTextLayout);
        LayoutPhoneNumber = root.findViewById(R.id.EditPhoneTextLayout);
        LayoutMail = root.findViewById(R.id.EditEmailTextLayout);
        LayoutGender = root.findViewById(R.id.EditGenderTextLayout);
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        changeText(fAuth.getCurrentUser().getUid());
        final ArrayAdapter<String> genderSelection = new  ArrayAdapter<String>(getContext(),android.R.layout.simple_spinner_dropdown_item, genders);
        ETGender.setFocusable(false);
        ETGender.setClickable(true);

        imageCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CharSequence[] items = {"Take new photo", "Choose from library", "Cancel"};
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Edit profile picture");
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if(items[which].equals("Take new photo")){
                            Intent cInt = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(cInt,CAMERA_ACTION_CODE);
                        }
                        else if(items[which].equals("Choose from library")){
                            Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            intent.putExtra("crop", "true");
                            startActivityForResult(intent, GALLERY_ACTION_CODE);
                        }
                        else{
                            dialog.dismiss();
                        }
                    }
                });
                builder.show();
            }
        });

        ETGender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new android.app.AlertDialog.Builder(getContext()).setAdapter(genderSelection, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ETGender.setText(genders[which].toString());
                        dialog.dismiss();
                    }
                }).create().show();
            }
        });

        BtnResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ChangePasswordActivity.class);
                startActivity(intent);
            }
        });

        TVEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageCamera.setVisibility(View.VISIBLE);
                LayoutName.setVisibility(View.VISIBLE);
                LayoutPhoneNumber.setVisibility(View.VISIBLE);
                LayoutAge.setVisibility(View.VISIBLE);
                LayoutMail.setVisibility(View.VISIBLE);
                LayoutGender.setVisibility(View.VISIBLE);
                TVSaveEdit.setVisibility(View.VISIBLE);
                editDiv.setVisibility(View.VISIBLE);
                BtnResetPassword.setVisibility(View.GONE);
                TVEdit.setVisibility(View.GONE);
                TVPhoneNo.setVisibility(View.GONE);
                TVProfileName.setVisibility(View.GONE);
                TVEmail.setVisibility(View.GONE);
                TVAge.setVisibility(View.GONE);
                TVGender.setVisibility(View.GONE);
                oriDiv.setVisibility(View.GONE);
                changeTextEdit(fAuth.getCurrentUser().getUid());
            }
        });

        TVSaveEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeKeyBoard(v);
                String phone = ETPhone.getText().toString().trim();
                String age = ETAge.getText().toString().trim();
                String name = ETName.getText().toString().trim();
                String emailEdited = ETMail.getText().toString().trim();
                String gender = ETGender.getText().toString().trim();

                //Conditions for name
                int validEdit = 0;
                if(TextUtils.isEmpty(name)){
                    LayoutName.setError("Name is required");
                }
                else{
                    LayoutName.setError(null);
                    validEdit += 1;
                }
                //Conditions for age
                if(TextUtils.isEmpty(age)){
                    LayoutAge.setError("Age is required");
                }
                else if(Integer.parseInt(age) <= 0 || Integer.parseInt(age) > 100 )
                {
                    LayoutAge.setError("Invalid age");
                }
                else{
                    LayoutAge.setError(null);
                    validEdit += 1;
                }
                //Condition for gender
                if(TextUtils.isEmpty(gender)){
                    LayoutGender.setError("Gender is required");
                }
                else{
                    LayoutGender.setError(null);
                    validEdit += 1;
                }

                //Conditions for phone
                if(TextUtils.isEmpty(phone)){
                    LayoutPhoneNumber.setError("Phone number is required");
                }
                else{
                    LayoutPhoneNumber.setError(null);
                    validEdit += 1;
                }

                //Conditions for mail
                if(TextUtils.isEmpty(emailEdited)){
                    LayoutMail.setError("E-Mail is required");
                }
                else if(!(emailEdited.matches(emailPattern))){
                    LayoutMail.setError("Invalid format for E-Mail");
                }
                else{
                    LayoutMail.setError(null);
                    validEdit += 1;
                }

                if(validEdit == 5){
                    if(!(emailEdited.equals(fAuth.getCurrentUser().getEmail()))){
                        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
                        View updateMail = layoutInflater.inflate(R.layout.updatemail, null);
                        AlertDialog.Builder mailUpdateDialog = new AlertDialog.Builder(getContext());
                        mailUpdateDialog.setTitle("Update Email");
                        mailUpdateDialog.setMessage("Enter your password to update your E-Mail address.");
                        mailUpdateDialog.setView(updateMail);
                        mailUpdateDialog.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                PBEditProfile.setVisibility(View.VISIBLE);
                                EditText ETPasswordDialog = updateMail.findViewById(R.id.NewEventAddingTextField);
                                String password = ETPasswordDialog.getText().toString().trim();
                                FirebaseUser user = fAuth.getCurrentUser();
                                AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), password);
                                user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            Toast.makeText(getContext(),"Profile update successfully", Toast.LENGTH_LONG).show();
                                            PBEditProfile.setVisibility(View.GONE);
                                            user.updateEmail(emailEdited);
                                            DocumentReference drf = fStore.collection("Users").document(fAuth.getCurrentUser().getUid());
                                            drf.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                @Override
                                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                    if(profile_picture != null){
                                                        updateProfilePicture(profile_picture);
                                                    }
                                                    String privilege = documentSnapshot.getString("Privilege");
                                                    Map<String, Object> UserInfo = new HashMap<>();
                                                    UserInfo.put("Gender", gender);
                                                    UserInfo.put("Phone", phone);
                                                    UserInfo.put("Age", age);
                                                    UserInfo.put("Name", name);
                                                    UserInfo.put("Username", emailEdited);
                                                    UserInfo.put("Privilege", privilege);
                                                    drf.update(UserInfo);
                                                    transitionOnSuccess(phone, age, name, emailEdited, gender);
                                                }
                                            });
                                        }
                                        else{
                                            PBEditProfile.setVisibility(View.GONE);
                                            Toast.makeText(getContext(),"Password entered is incorrect", Toast.LENGTH_LONG).show();
                                            transitionOnSuccess(phone, age, name, user.getEmail(), gender);
                                        }
                                    }
                                });
                            }
                        });
                        mailUpdateDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                        mailUpdateDialog.create().show();
                    }

                    else{
                        DocumentReference drf = fStore.collection("Users").document(fAuth.getCurrentUser().getUid());
                        drf.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                if(profile_picture != null){
                                    updateProfilePicture(profile_picture);
                                }
                                String email = documentSnapshot.getString("Username");
                                String privilege = documentSnapshot.getString("Privilege");
                                Map<String, Object> UserInfo = new HashMap<>();
                                UserInfo.put("Gender", gender);
                                UserInfo.put("Phone", phone);
                                UserInfo.put("Age", age);
                                UserInfo.put("Name", name);
                                UserInfo.put("Username", email);
                                UserInfo.put("Privilege", privilege);
                                drf.update(UserInfo);
                                Toast.makeText(getContext(),"Profile update successfully", Toast.LENGTH_LONG).show();
                                transitionOnSuccess(phone, age, name, emailEdited, gender);
                            }
                        });
                    }
                }
                else{
                    Toast.makeText(getContext(),"Please complete all of the fields above", Toast.LENGTH_LONG).show();
                }
            }
        });

        return root;
    }

    private void updateProfilePicture(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

        StorageReference reference = FirebaseStorage.getInstance().getReference()
                .child("profileImages")
                .child(fAuth.getCurrentUser().getUid() + ".jpeg");
        reference.putBytes(baos.toByteArray()).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                getDownloadURL(reference);
            }
        });
    }

    private void getDownloadURL(StorageReference reference) {
        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                setUserProfileUrl(uri);
            }
        });
    }

    private void setUserProfileUrl(Uri uri){
        FirebaseUser user = fAuth.getCurrentUser();
        UserProfileChangeRequest request = new UserProfileChangeRequest.Builder().setPhotoUri(uri).build();
        user.updateProfile(request);
    }


    public void changeText(String UID){
        DocumentReference df = fStore.collection("Users").document(UID);
        df.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String name = documentSnapshot.getString("Name");
                String email = documentSnapshot.getString("Username");
                String phone = documentSnapshot.getString("Phone");
                String age = documentSnapshot.getString("Age");
                String gender = documentSnapshot.getString("Gender");
                if(phone != null)
                {
                    TVPhoneNo.setText(phone);
                }
                if(age != null)
                {
                    TVAge.setText(age + " years old");
                }
                if(gender != null){
                    TVGender.setText(gender);
                }
                TVProfileName.setText(name);
                TVEmail.setText(email);
            }
        });
        if(!String.valueOf(fAuth.getCurrentUser().getPhotoUrl()).equals("null")){
            Glide.with(getContext()).load(Objects.requireNonNull(fAuth.getCurrentUser()).getPhotoUrl()).into(imageProfile);
        }
        else{
            imageProfile.setImageResource(R.drawable.ic_profile);
        }
    }

    public void changeTextEdit(String UID){
        DocumentReference df = fStore.collection("Users").document(UID);
        df.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String name = documentSnapshot.getString("Name");
                String email = documentSnapshot.getString("Username");
                String phone = documentSnapshot.getString("Phone");
                String age = documentSnapshot.getString("Age");
                String gender = documentSnapshot.getString("Gender");
                if(phone != null)
                {
                    ETPhone.setText(phone);
                }
                if(age != null)
                {
                    ETAge.setText(age);
                }
                if(gender != null)
                {
                    ETGender.setText(gender);
                }
                ETName.setText(name);
                ETMail.setText(email);
            }
        });
    }

    public void transitionOnSuccess(String phone, String age, String name, String emailEdited, String gender){
        imageCamera.setVisibility(View.GONE);
        LayoutName.setVisibility(View.GONE);
        LayoutPhoneNumber.setVisibility(View.GONE);
        LayoutAge.setVisibility(View.GONE);
        LayoutMail.setVisibility(View.GONE);
        LayoutGender.setVisibility(View.GONE);
        TVSaveEdit.setVisibility(View.GONE);
        editDiv.setVisibility(View.GONE);
        BtnResetPassword.setVisibility(View.VISIBLE);
        TVEdit.setVisibility(View.VISIBLE);
        TVPhoneNo.setVisibility(View.VISIBLE);
        TVProfileName.setVisibility(View.VISIBLE);
        TVEmail.setVisibility(View.VISIBLE);
        TVAge.setVisibility(View.VISIBLE);
        TVGender.setVisibility(View.VISIBLE);
        oriDiv.setVisibility(View.VISIBLE);
        TVGender.setText(gender);
        TVPhoneNo.setText(phone);
        TVAge.setText(age + " years old");
        TVProfileName.setText(name);
        TVEmail.setText(emailEdited);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bitmap bitmap = null;
        if (requestCode == CAMERA_ACTION_CODE) {
            if (resultCode == RESULT_OK) {
                bitmap = (Bitmap) data.getExtras().get("data");
                profile_picture = bitmap;
                imageProfile.setImageBitmap(bitmap);
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(getContext(), "Camera closed", Toast.LENGTH_LONG).show();
            }
        }
        else if(requestCode == GALLERY_ACTION_CODE){
            if (resultCode == RESULT_OK) {
                Uri bp = data.getData();
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), bp);
                    profile_picture = bitmap;
                    imageProfile.setImageBitmap(bitmap);
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(getContext(), "Gallery closed", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void closeKeyBoard(View view){
        view = getActivity().getCurrentFocus();
        if(view != null){
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}