package com.mossosouk.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


public class UserProfileActivity extends AppCompatActivity {

    private TextView nomVendeur, num, produits;
    private CircleImageView userProfileImage;

    private FirebaseAuth mAuth;
    private DatabaseReference RootRef;
    private StorageReference storageReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mAuth = FirebaseAuth.getInstance();
        String currentUserID = mAuth.getCurrentUser().getUid();
        RootRef = FirebaseDatabase.getInstance().getReference();

        Intent intent = getIntent();
        String vendeur = intent.getStringExtra("vendeur");
        String contact = intent.getStringExtra("contact");

        nomVendeur = (TextView) findViewById(R.id.sellerName);
        num = (TextView) findViewById(R.id.sellerContact);
        produits = (TextView) findViewById(R.id.sellerProducts);
        userProfileImage = (CircleImageView) findViewById(R.id.userImage);

        nomVendeur.setText(vendeur);
        num.setText(contact);
        produits.setText("En attente...");



        //Picasso.with(UserProfileActivity.this).load()
    }

}
