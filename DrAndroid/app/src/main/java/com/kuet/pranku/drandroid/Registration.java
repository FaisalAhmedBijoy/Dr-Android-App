package com.kuet.pranku.drandroid;


import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;

public class Registration extends AppCompatActivity{

    private EditText userName, userPassword, userEmail, userAge,userPhone,userAddress;
    private Button regButton;
    private Spinner spinnerSpecialist;
    private TextView userLogin;
    private FirebaseAuth firebaseAuth;
    private ImageView userProfilePic;
    private CheckBox doctorCheckBox,userCheckBox;
    String email, name, age, password,phone,address,specialistAt;
    private FirebaseStorage firebaseStorage;
    private static int PICK_IMAGE = 123;
    Uri imagePath;
    private StorageReference storageReference;
    String userUid;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null){
            imagePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imagePath);
                userProfilePic.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        setupUIViews();

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();

        storageReference = firebaseStorage.getReference("profilepics");

        userProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE);
            }
        });

        userLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Registration.this, MainActivity.class));
            }
        });

    }

    public void registration(View view) {
        if (isOnline()) {
            if (validate()) {

                //Upload data to the database
                String user_email = userEmail.getText().toString().trim();
                String user_password = userPassword.getText().toString().trim();

                firebaseAuth.createUserWithEmailAndPassword(user_email, user_password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            //sendEmailVerification();
                            setProfilePic();
                            if (userCheckBox.isChecked() || doctorCheckBox.isChecked()) {
                                sendUserDataToFireBase();
                            }
                            if (doctorCheckBox.isChecked()) {
                                sendClientDataToFireBase();
                            }
                            firebaseAuth.signOut();
                            Toast.makeText(Registration.this, "Successfully Registered, Upload complete!", Toast.LENGTH_SHORT).show();
                            finish();
                            startActivity(new Intent(Registration.this, Second.class));
                        } else {
                            if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                Toast.makeText(getApplicationContext(), "This Email Id already registered", Toast.LENGTH_SHORT).show();

                            } else {
                                Toast.makeText(getApplicationContext(), "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }

                        }

                    }
                });

            }
        }else {
            Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
        }
    }

    public void setProfilePic(){
        final StorageReference imageReference = storageReference.child(firebaseAuth.getUid());//User id/Images/Profile Pic.jpg
        UploadTask uploadTask = imageReference.putFile(imagePath);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("knhoyna",""+e);
                Toast.makeText(Registration.this, "Upload failed!", Toast.LENGTH_SHORT).show();
            }
        }).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                Toast.makeText(Registration.this, "Upload successful!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendUserDataToFireBase(){
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference myRef = firebaseDatabase.getReference("Users").child(firebaseAuth.getUid());
        userUid = firebaseAuth.getUid();

        UserProfile userProfile = new UserProfile(name,email,age,phone,address,specialistAt,userUid);
        myRef.setValue(userProfile);
    }
    private void sendClientDataToFireBase(){
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference myRef = firebaseDatabase.getReference("Doctors").child(firebaseAuth.getUid());
        userUid = firebaseAuth.getUid();

        UserProfile userProfile = new UserProfile(name,email,age,phone,address,specialistAt,userUid);
        myRef.setValue(userProfile);

    }

    private void setupUIViews(){
        userName = findViewById(R.id.etUserName);
        userPassword = findViewById(R.id.etUserPassword);
        userEmail = findViewById(R.id.etUserEmail);
        regButton = findViewById(R.id.btnRegister);
        userLogin = findViewById(R.id.tvUserLogin);
        userAge = findViewById(R.id.etAge);
        userProfilePic = findViewById(R.id.ivProfile);
        userPhone = findViewById(R.id.etPhone);
        userAddress = findViewById(R.id.etAddress);
        spinnerSpecialist = findViewById(R.id.spinner);
        doctorCheckBox = findViewById(R.id.clientCheckboxId);
        userCheckBox = findViewById(R.id.userCheckBoxId);
    }

    private Boolean validate(){
        Boolean result = false;

        name = userName.getText().toString();
        password = userPassword.getText().toString();
        email = userEmail.getText().toString();
        age = userAge.getText().toString();
        phone = userPhone.getText().toString();
        address = userAddress.getText().toString();
        specialistAt = spinnerSpecialist.getSelectedItem().toString();

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            userEmail.setError("Please enter a valid email");
            userEmail.requestFocus();
            return false;
        }
        if (password.length() < 6) {
            userPassword.setError("Minimum lenght of password should be 6");
            userPassword.requestFocus();
            return false;
        }
        if(!doctorCheckBox.isChecked() && !userCheckBox.isChecked()){
            Toast.makeText(getApplicationContext(),"Checked either Client or User",Toast.LENGTH_SHORT).show();
            return false;
        }

        if(name.isEmpty() || password.isEmpty() || email.isEmpty() || age.isEmpty() || phone.isEmpty()|| address.isEmpty()|| imagePath == null){
            if(name.isEmpty() ){
                userName.setError("Field can't be Empty");
            }else if(password.isEmpty()){
                userPassword.setError("Field can't be Empty");
            }else if(email.isEmpty()){
                userEmail.setError("Field can't be Empty");
            }else if(age.isEmpty()){
                userAge.setError("Field can't be Empty");
            }else if(phone.isEmpty()){
                userPhone.setError("Field can't be Empty");
            }else if(age.isEmpty()){
                userAddress.setError("Field can't be Empty");
            }else if(imagePath==null){
                Toast.makeText(getApplicationContext(),"Please Select your profile pic",Toast.LENGTH_SHORT).show();
            }
        }else{
            result = true;
        }

        return result;
    }


    private void sendEmailVerification(){
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if(firebaseUser!=null){
            firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        //sendDataToFirebase();
                        Toast.makeText(Registration.this, "Successfully Registered, Verification mail sent!", Toast.LENGTH_SHORT).show();
                        firebaseAuth.signOut();
                        finish();
                        startActivity(new Intent(Registration.this, MainActivity.class));
                    }else{
                        Toast.makeText(Registration.this, "Verification mail has'nt been sent!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }


    private boolean isOnline(){
        ConnectivityManager connectivityManager= (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo=connectivityManager.getActiveNetworkInfo();
        return networkInfo !=null && networkInfo.isConnected();
    }
}
