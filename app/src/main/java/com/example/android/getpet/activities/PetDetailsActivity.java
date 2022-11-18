package com.example.android.getpet.activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import com.bumptech.glide.Glide;
import com.example.android.getpet.models.Pets;
import com.example.android.getpet.R;
import com.example.android.getpet.utils.APIService;
import com.example.android.getpet.utils.Client;
import com.example.android.getpet.models.Data;
import com.example.android.getpet.utils.MyResponse;
import com.example.android.getpet.models.NotificationSender;
import com.example.android.getpet.models.Token;
import com.example.android.getpet.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class PetDetailsActivity extends AppCompatActivity {

    private TextView animal_details_et;
    private TextView breed_details_et;
    private TextView age_details_et;
    private TextView size_details_et;
    private TextView gender_details_et;
    private TextView animalType;
    private CardView Adopt_CardView;

    private TextView loc_add;
    private TextView loc_icon;

    //Initializing api service
    private APIService apiService;
    private TextView adoptPet;
    private TextView ownerName_tv;
    private TextView desc;
    private ImageView pic_details_et;
    private ImageView ownerPic_tv;
    private String imageUrl;
    private TextView chat;
    private TextView favourites;
    private String key, ownerName, ownerEmail, ownerPic, ownerKey, petLat, petLong, petDescription;
    private String petKey ,petName, breed, age, size, gender, animal, addLoc;
    private String senderName, senderEmail, senderPic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pet_details);

        //Passing the value of client class to the api service
        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);

        adoptPet = findViewById(R.id.adopt_et);

        animalType = findViewById(R.id.animalType_et);
        animal_details_et = findViewById(R.id.animal_details_et);
        breed_details_et = findViewById(R.id.breed_details_et);
        age_details_et = findViewById(R.id.age_details_et);
        size_details_et = findViewById(R.id.size_details_et);
        gender_details_et = findViewById(R.id.gender_details_et);
        pic_details_et = findViewById(R.id.pic_details_iv);
        desc = findViewById(R.id.desc_details_tv);

        ownerName_tv = findViewById(R.id.ownerName_details_et);
        ownerPic_tv = findViewById(R.id.ownerPic_details);

        loc_add = findViewById(R.id.loc_details_add);
        loc_icon = findViewById(R.id.loc_details);
        chat = findViewById(R.id.message_tv);
        favourites = findViewById(R.id.fav_tv);

        Adopt_CardView = findViewById(R.id.adoptPetCard);

        /*Getting data from petList Activity or FavouritesActivity or userPetListActivity
        so that data can be shown in various text boxes.*/

        Intent intent = getIntent();
        key = intent.getStringExtra("keyData");
        animal = intent.getStringExtra("animal");
        petName = intent.getStringExtra(getResources().getString(R.string.PetDetailsActivity_intent_petName));
        breed = intent.getStringExtra(getResources().getString(R.string.PetDetailsActivity_intent_breed));
        age = intent.getStringExtra(getResources().getString(R.string.PetDetailsActivity_intent_age));
        size = intent.getStringExtra(getResources().getString(R.string.PetDetailsActivity_intent_size));
        gender = intent.getStringExtra(getResources().getString(R.string.PetDetailsActivity_intent_gender));
        imageUrl = intent.getStringExtra(getResources().getString(R.string.PetDetailsActivity_intent_pic));
        ownerKey = intent.getStringExtra(getResources().getString(R.string.PetDetailsActivity_intent_OwnerProfileKey));
        ownerName = intent.getStringExtra(getResources().getString(R.string.PetDetailsActivity_intent_OwnerName));
        ownerEmail = intent.getStringExtra(getResources().getString(R.string.PetDetailsActivity_intent_OwnerEmail));
        ownerPic = intent.getStringExtra(getResources().getString(R.string.PetDetailsActivity_intent_OwnerProfilePic));
        petKey = intent.getStringExtra(getResources().getString(R.string.PetDetailsActivity_intent_PetKey));
        petLat = intent.getStringExtra(getResources().getString(R.string.PetDetailsActivity_intent_PetLat));
        petLong = intent.getStringExtra(getResources().getString(R.string.PetDetailsActivity_intent_PetLong));
        addLoc = intent.getStringExtra("Address_Location");
        petDescription = intent.getStringExtra("pet_description");

        //Setting data in different TextViews that we received from Intents.
        animalType.setText(animal);
        animal_details_et.setText(petName);
        breed_details_et.setText(breed);
        age_details_et.setText(age);
        size_details_et.setText(size);
        gender_details_et.setText(gender);
        desc.setText(petDescription);
        ownerName_tv.setText(ownerName);
        loc_add.setText(addLoc);

        //Sending notification to the pet owner.
        adoptPet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Fetching the token of pet owner using his UUID which is saved in ownerKey.
                FirebaseDatabase.getInstance().getReference().child("Tokens").child(ownerKey)
                        .child("token")
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                String usertoken=snapshot.getValue(String.class);
                                sendNotifications(usertoken,"Request for pet adoption!",
                                        senderName + " wants to adopt "+petName);
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
            }
        });

        //Updating the user tokens so that notification can be sent using the token.
        UpdateToken();

        try {
            final Pets[] pet = {null};
            //Getting the data if the pet is favourites of the user from database.
            FirebaseDatabase.getInstance().getReference("favourites/"
                    + FirebaseAuth.getInstance().getUid() + "/" + key)
                    .addListenerForSingleValueEvent(new ValueEventListener() {

                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    pet[0] = snapshot.getValue(Pets.class);
                    if (pet[0] == null) {
                        //Getting null value means the current pet is not favourites of the user so set background accordingly.
                        favourites.setBackground(ContextCompat.getDrawable(getApplicationContext(),
                                R.drawable.fav_empty));
                    } else {
                       favourites.setBackground(ContextCompat.getDrawable(getApplicationContext(),
                               R.drawable.fav_back));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        catch (Exception e){
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Slow Internet Connection",
                    Toast.LENGTH_SHORT).show();
        }

        try {
            //Getting data about user from database.
            FirebaseDatabase.getInstance().getReference("users/" +
                    FirebaseAuth.getInstance().getUid())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    senderName = snapshot.getValue(User.class).getName();
                    senderEmail = snapshot.getValue(User.class).getEmail();
                    senderPic = snapshot.getValue(User.class).getProfilePic();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        catch (Exception e){
            e.printStackTrace();
            Toast.makeText(getApplicationContext(),
                    "Slow Internet Connection", Toast.LENGTH_SHORT).show();
        }

        CircularProgressDrawable circularProgressDrawable =
                new CircularProgressDrawable(PetDetailsActivity.this);

        circularProgressDrawable.setStrokeWidth(5f);
        circularProgressDrawable.setCenterRadius(30f);
        circularProgressDrawable.start();

        //Using Glide library to put image of pet in imageView.
        if (imageUrl.isEmpty()) {
            Glide.with(getApplicationContext()).load(R.drawable.account_img)
                    .error(R.drawable.account_img)
                    .placeholder(circularProgressDrawable)
                    .into(pic_details_et);
        } else {


            Glide.with(getApplicationContext()).load(imageUrl)
                    .error(R.drawable.account_img)
                    .placeholder(circularProgressDrawable)
                    .into(pic_details_et);
        }

        if(imageUrl.isEmpty()){
            ownerPic_tv.setVisibility(View.GONE);
        }
        else{

            Glide.with(getApplicationContext()).load(ownerPic)
                    .error(R.drawable.account_img)
                    .placeholder(circularProgressDrawable)
                    .into(ownerPic_tv);
        }

        //Chat option will only be available if it is someone else's pet not user's.
        if (ownerEmail.equals(FirebaseAuth.getInstance().getCurrentUser().getEmail())) {
            chat.setVisibility(View.INVISIBLE);
            adoptPet.setVisibility(View.INVISIBLE);
            Adopt_CardView.setVisibility(View.INVISIBLE);
        }

        //Sending data to ChatActivity.
        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(PetDetailsActivity.this, ChatActivity.class);
                intent1.putExtra("petName_ChatActivity",petName);
                intent1.putExtra(getResources().getString(R.string.ChatActivity_intent_petKey),petKey);
                intent1.putExtra(getResources().getString(R.string.ChatActivity_intent_receiverName),ownerName);
                intent1.putExtra(getResources().getString(R.string.ChatActivity_intent_receiverEmail),ownerEmail);
                intent1.putExtra(getResources().getString(R.string.ChatActivity_intent_receiverPic),ownerPic);
                intent1.putExtra(getResources().getString(R.string.ChatActivity_intent_receiverKey),ownerKey);
                intent1.putExtra(getResources().getString(R.string.ChatActivity_intent_senderName),senderName);
                intent1.putExtra(getResources().getString(R.string.ChatActivity_intent_senderEmail),senderEmail);
                intent1.putExtra(getResources().getString(R.string.ChatActivity_intent_senderPic),senderPic);
                intent1.putExtra(getResources().getString(R.string.ChatActivity_intent_senderKey),FirebaseAuth.getInstance().getUid());
                startActivity(intent1);
            }
        });

        favourites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    final Pets[] pet = {null};
                    //Getting the data if the pet is favourites of the user from database.
                    FirebaseDatabase.getInstance().getReference("favourites/"
                            + FirebaseAuth.getInstance().getUid() + "/" + key)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            pet[0] = snapshot.getValue(Pets.class);
                            if (pet[0] == null) { //Getting null value means the current pet is not favourites of the user.
                                FirebaseDatabase.getInstance().getReference("favourites/"
                                        + FirebaseAuth.getInstance().getUid() + "/" + key)
                                        .setValue(new Pets(petKey, key, petName, animal, breed, age, size, gender,petDescription, imageUrl,
                                                ownerKey, ownerName, ownerEmail, ownerPic, petLat, petLong,addLoc));
                                favourites.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.fav_back));

                            } else { //Removing the pet from favourites if it is already the favourites of the user.
                                FirebaseDatabase.getInstance().getReference("favourites/"
                                        + FirebaseAuth.getInstance().getUid() + "/").child(key).removeValue();
                                favourites.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.fav_empty));
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
                catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Slow Internet Connection",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

//        Sending data to LocationActivity, Checking for permissions.
        loc_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Checking if we have permission for location.
                if(ContextCompat.checkSelfPermission(PetDetailsActivity.this,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED){
                    sendIntentToLocationActivity();
                }
                else{
                    getPermissions();
                }
            }
        });

        loc_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Checking if we have permission for location.
                if(ContextCompat.checkSelfPermission(PetDetailsActivity.this,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED){
                    sendIntentToLocationActivity();
                }
                else{
                    getPermissions();
                }
            }
        });
    }

    //Updating the user tokens to send notifications
    private void UpdateToken() {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if(!task.isSuccessful()){
                            Toast.makeText(PetDetailsActivity.this,
                                    "New token failed", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        String token = task.getResult();
                        updateToken(token);
                    }
                });
    }

    private void updateToken(String token) {
        Token token1 = new Token(token);
        //Adding the token to the database so that it can be retrieved to send notifications to the user.
        FirebaseDatabase.getInstance().getReference("Tokens")
                .child(FirebaseAuth.getInstance().getUid()).setValue(token1);
    }

    //This Method Sends the notifications combining all class of
    //SendNotificationPack Package work together
    public void sendNotifications(String usertoken, String title, String message) {
        Data data = new Data(title, message);
        NotificationSender sender = new NotificationSender(data, usertoken);
        apiService.sendNotifcation(sender).enqueue(new Callback<MyResponse>() {
            @Override
            public void onResponse(Call<MyResponse> call,
                                   Response<MyResponse> response) {
                if (response.code() == 200) {
                    assert response.body() != null;
                    if (response.body().success != 1) {
                        Toast.makeText(PetDetailsActivity.this,
                                "Sorry Pet owner could not be informed. Please try again later.",
                                Toast.LENGTH_LONG).show();
                    }else {
                        Toast.makeText(getApplicationContext(),
                                "Pet owner of this pet has been informed.",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }
            @Override
            public void onFailure(Call<MyResponse> call, Throwable t) {
            }
        });
    }
    
    
    //Getting location permission using Dexter Api.
    private void getPermissions(){
        Dexter.withContext(PetDetailsActivity.this).withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        //After getting permission send intent to location activity.
                        sendIntentToLocationActivity();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                        //Making an alert dialog box to ask for permissions.
                        if(permissionDeniedResponse.isPermanentlyDenied()){
                            AlertDialog.Builder builder = new AlertDialog.Builder(PetDetailsActivity.this);
                            builder.setTitle("Permission Denied")
                                    .setMessage("Permission to access device location is permanently denied." +
                                            "You need to allow the permission from settings")
                                    .setNegativeButton("Cancel",null)
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            Intent intent = new Intent();
                                            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                            intent.setData(Uri.fromParts("package",getPackageName(),null));
                                        }
                                    }).show();
                        }
                        else{
                            Toast.makeText(getApplicationContext(),"Permission Denied",Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest,
                                                                   PermissionToken permissionToken) {
                        //Continuing permission request
                        permissionToken.continuePermissionRequest();
                    }
                }).check();
    }

    //Sending data to LocationActivity
    private void sendIntentToLocationActivity(){
        Intent intent2 = new Intent(PetDetailsActivity.this, LocationActivity.class);
        intent2.putExtra(getResources().getString(R.string.flag_Val),true);
        intent2.putExtra(getResources().getString(R.string.LocationActivity_intent_latitudeData),petLat);
        intent2.putExtra(getResources().getString(R.string.LocationActivity_intent_longitudeData),petLong);
        intent2.putExtra(getResources().getString(R.string.LocationActivity_intent_PetNameData),petName);
        startActivity(intent2);
    }
}
