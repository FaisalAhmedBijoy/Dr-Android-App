package com.kuet.pranku.drandroid;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Second extends AppCompatActivity {


    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private ArrayList<UserProfile> userProfiles;
    private FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        recyclerView = findViewById(R.id.recyclerViewId);
        firebaseAuth = FirebaseAuth.getInstance();

        userProfiles = new ArrayList<>();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference =firebaseDatabase.getReference("Doctors");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userProfiles.clear();
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    UserProfile userProfile = userSnapshot.getValue(UserProfile.class);

                    userProfiles.add(userProfile);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        DoctorAdapter adapter = new DoctorAdapter(this,userProfiles);
        recyclerView.setAdapter(adapter);
    }
    private void Logout(){
        firebaseAuth.signOut();
        finish();
        startActivity(new Intent(Second.this, MainActivity.class));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.profile_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()){
            case R.id.logoutMenu:{
                Logout();
                break;
            }
            case R.id.refreshMenu:{
                this.recreate();
                break;
            }

            case R.id.profileMenu: {
              //  startActivity(new Intent(Second.this, ProfileActivity.class));
                break;
            }

        }
        return super.onOptionsItemSelected(item);
    }
}
