package com.mossosouk.app;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.Locale;

public class DetailsActivity extends AppCompatActivity {

    AppCompatButton acheterBtn, supprimerBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R. layout.activity_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SharedPreferences sp = getSharedPreferences(LoginActivity.fileName, Context.MODE_PRIVATE);
        final String name = sp.getString("username", "");

        final int position = getIntent().getIntExtra("position", -1);
        final String origine = getIntent().getStringExtra("origine");

        final Annonce annonce;

        if (origine.equals("main")) {
            annonce = MainActivity.list.get(position);
        } else {
            annonce = FindActivity.list.get(position);
        }

        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressbarImg);

        ImageView imageView = (ImageView) findViewById(R.id.imageDesc);
        Picasso.with(this).load(annonce.imageBig).into(imageView, new com.squareup.picasso.Callback() {

            @Override
            public void onSuccess() {
                if (progressBar != null) {
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onError() {

            }
        });

        TextView details_title = (TextView) findViewById(R.id.details_title);
        TextView details_desc = (TextView) findViewById(R.id.details_desc);
        TextView details_price = (TextView) findViewById(R.id.details_price);
        TextView details_seller = (TextView) findViewById(R.id.details_seller);

        details_title.setText(annonce.titre);
        details_desc.setText(annonce.description);
        details_seller.setText(annonce.vendeur);

        if (annonce.prix != -1) {
            String s = NumberFormat.getInstance(Locale.FRENCH).format(annonce.prix);
            details_price.setText(s + " FCFA");
        }

        acheterBtn = (AppCompatButton) findViewById(R.id.acheter);
        supprimerBtn = (AppCompatButton) findViewById(R.id.suppimerBtn);

        if (annonce.categorie.equals("Emploi-Stage") || annonce.categorie.equals("Opportunités") || annonce.categorie.equals("Evènements")){

            acheterBtn.setVisibility(View.INVISIBLE);
        }

        if (annonce.vendeur.equals(name)){

            acheterBtn.setVisibility(View.INVISIBLE);
            supprimerBtn.setVisibility(View.VISIBLE);

            supprimerBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    AlertDialog.Builder supprimeAlert = new AlertDialog.Builder(DetailsActivity.this);
                    supprimeAlert.setTitle("Suppression du produit");
                    supprimeAlert.setMessage("Êtes-vous sûr de vouloir supprimer?");
                    supprimeAlert.setIcon(R.drawable.ic_error_black_24dp);
                    supprimeAlert.setCancelable(false);
                    supprimeAlert.setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            String url = "http://www.mossosouk.com/supprimerMobile?id=" + annonce.id;
                            StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    Toast.makeText(DetailsActivity.this, "Le produit est supprimé avec succès", Toast.LENGTH_SHORT).show();
                                    SendUserToMainActivity();
                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Toast.makeText(DetailsActivity.this, "ERROR...", Toast.LENGTH_SHORT).show();
                                }
                            });

                            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                            requestQueue.add(request);

                        }
                    });

                    supprimeAlert.setNegativeButton("Non", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });

                    AlertDialog alertDialog = supprimeAlert.create();
                    alertDialog.show();

                }
            });

        }
        else {

            supprimerBtn.setVisibility(View.INVISIBLE);

            acheterBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(getApplicationContext(), BuyActivity.class);
                    i.putExtra("position", position);
                    i.putExtra("origine", origine);
                    startActivity(i);
                }
            });
        }

    }

    public void SendUserToMainActivity(){

        Intent mainIntent = new Intent(DetailsActivity.this, MainActivity.class);
        startActivity(mainIntent);

    }

}
