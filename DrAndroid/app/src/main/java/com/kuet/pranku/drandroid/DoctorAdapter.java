package com.kuet.pranku.drandroid;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class DoctorAdapter extends RecyclerView.Adapter<DoctorAdapter.ViewHolder>{



    private ArrayList<UserProfile> userProfiles;
    private Activity activity;

    public DoctorAdapter(Activity activity,ArrayList<UserProfile> userProfiles) {
        this.userProfiles = userProfiles;
        this.activity = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.doctor_list_item,viewGroup,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {
        final UserProfile userProfile = userProfiles.get(i);

        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        StorageReference storageReference = firebaseStorage.getReference("profilepics");

        storageReference.child(userProfile.getUserUid()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).fit().centerCrop().into(viewHolder.imageView);
            }
        });
        try {
            viewHolder.headTextView.setText(userProfile.getUserName());
            viewHolder.addressTextView.setText("Address: "+userProfile.getUserAddress());
            viewHolder.ageTextView.setText("Age: "+userProfile.getUserAge());
            viewHolder.specialistAtTextView.setText("Specialist At: "+userProfile.getUserSpecialistAt());
        }catch (Exception e){
            Toast.makeText(activity,e.getMessage(),Toast.LENGTH_SHORT).show();
        }

        viewHolder.sendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String emailAddress = userProfile.getUserEmail();
               /* Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/email");
                intent.putExtra(Intent.EXTRA_EMAIL,new String[] {"tusharbd021@gmail.com"});
                intent.putExtra(Intent.EXTRA_SUBJECT,"Feedback From App.");
                intent.putExtra(Intent.EXTRA_TEXT,"Name: "+name+"\nMassage: "+message);
                startActivity(Intent.createChooser(intent,"Feedback with"));*/

                Intent intent = new Intent (Intent.ACTION_SEND);
                intent.setType("message/rfc822");
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{emailAddress});
                intent.putExtra(Intent.EXTRA_SUBJECT, "From Zogaan");
                intent.putExtra(Intent.EXTRA_TEXT,"Name:\n\nWork Detail: ");
                intent.setPackage("com.google.android.gm");
                if (intent.resolveActivity(activity.getPackageManager())!=null)
                    activity.startActivity(intent);
                else {
                    Toast.makeText(activity, "Gmail App is not installed", Toast.LENGTH_SHORT).show();
                    Intent intenta = new Intent(Intent.ACTION_SEND);
                    intenta.setType("text/email");
                    intenta.putExtra(Intent.EXTRA_EMAIL,new String[] {emailAddress});
                    intenta.putExtra(Intent.EXTRA_SUBJECT,"From Zogaan");
                    intenta.putExtra(Intent.EXTRA_TEXT,"Name:\n\nWork Detail: ");
                    activity.startActivity(Intent.createChooser(intenta,"Email With"));
                }
            }
        });
        viewHolder.phoneCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNumber = userProfile.getUserPhone().trim();
                Intent i= new Intent(Intent.ACTION_CALL);
                i.setData(Uri.parse("tel:"+phoneNumber));

                if(ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.CALL_PHONE)!= PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(activity,"please grant the permission to call",Toast.LENGTH_SHORT).show();
                    requestPermission();
                }
                else {
                    activity.startActivity(i);
                }
            }
        });
    }

    void requestPermission(){
        ActivityCompat.requestPermissions(activity,new String[]{Manifest.permission.CALL_PHONE},1);
    }

    @Override
    public int getItemCount() {
        return userProfiles.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        ImageView imageView;
        TextView headTextView,
                addressTextView,
                ageTextView,
                specialistAtTextView;

        ImageView sendEmail;
        ImageView phoneCall;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imageViewId);
            headTextView = itemView.findViewById(R.id.tvNameId);
            addressTextView = itemView.findViewById(R.id.tvAddressId);
            sendEmail = itemView.findViewById(R.id.sendEmailId);
            phoneCall= itemView.findViewById(R.id.phoneCallId);
            ageTextView = itemView.findViewById(R.id.tvAgeId);
            specialistAtTextView = itemView.findViewById(R.id.tvSpecialistAtId);
        }
    }
}
