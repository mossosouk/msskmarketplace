package com.mossosouk.app;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.mossosouk.app.Annonce;
import com.mossosouk.app.MainActivity;
import com.mossosouk.app.R;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class BuyActivity extends AppCompatActivity {

    ImageView imageDetails;
    TextView nomUser, vendeurText, nomProduit;
    EditText qteProduit, adresseLivrasion;
    LinearLayout linearModeLivraison;
    Spinner modePaiement;
    String[] modePaiementTab = {"Cash à la livraison", "Airtel Money", "Tigo Cash"};
    ArrayAdapter<String> modePaiementAdapter;
    AppCompatButton commanderBtn;

    private ProgressDialog loadingBar;

    public class BuyTask extends AsyncTask<String, Void, String> {

        boolean connected = true;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            loadingBar.setTitle("Envoi de la commande");
            loadingBar.setMessage("Veuillez patienter s'il vous plait");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

        }

        @Override
        protected String doInBackground(String... urls) {

            String result = "";
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                InputStream in = connection.getInputStream();

                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();

                while (data != -1) {
                    result += (char) data;
                    data = reader.read();
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                connected = false;
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (connected) {

                loadingBar.dismiss();

                AlertDialog.Builder builder = new AlertDialog.Builder(BuyActivity.this);
                builder.setTitle("Confirmation");
                builder.setMessage("Votre commande est en cours. Nous vous contacterons dans un instant. Merci pour votre confiance!");
                builder.setIcon(R.drawable.ic_check_circle_black_24dp);
                builder.setCancelable(false);
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Intent i = new Intent(BuyActivity.this, MainActivity.class);
                        startActivity(i);
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            } else {

                loadingBar.dismiss();

                AlertDialog.Builder builder = new AlertDialog.Builder(BuyActivity.this);
                builder.setTitle("Problème de connexion");
                builder.setMessage("Vérifier votre connexion s'il vous plait!");
                builder.setIcon(R.drawable.ic_error_black_24dp);
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                                    /*Intent i = new Intent(BuyActivity.this, MainActivity.class);
                                    startActivity(i);*/
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        loadingBar = new ProgressDialog(this);

        imageDetails = (ImageView) findViewById(R.id.imageDetails);
        commanderBtn = (AppCompatButton) findViewById(R.id.commanderBtn);

        linearModeLivraison = (LinearLayout) findViewById(R.id.linear9);

        nomUser = (TextView) findViewById(R.id.nomUser);
        vendeurText = (TextView) findViewById(R.id.vendeurText);
        nomProduit = (TextView) findViewById(R.id.nomProduit);
        qteProduit = (EditText) findViewById(R.id.qteProduit);
        adresseLivrasion = (EditText) findViewById(R.id.adresseLivraison);

        final int position = getIntent().getIntExtra("position", -1);
        final String origine = getIntent().getStringExtra("origine");

        Annonce annonce;
        if(origine.equals("main")) {
            annonce = MainActivity.list.get(position);
        } else {
            annonce = FindActivity.list.get(position);
        }


        modePaiement = (Spinner) findViewById(R.id.modePaiement);

        SharedPreferences sp = getSharedPreferences(LoginActivity.fileName, Context.MODE_PRIVATE);
        final String name = sp.getString("username", "");
        final String tel = sp.getString("contact", "");

        nomUser.setText(name);
        Picasso.with(this).load(annonce.imageBig).into(imageDetails);
        vendeurText.setText(annonce.vendeur);
        nomProduit.setText(annonce.titre);
        //montant.setText(annonce.prix.toString() + " FCFA");

        modePaiementAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, modePaiementTab);
        modePaiement.setAdapter(modePaiementAdapter);

        commanderBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //COMMANDER

                    int idOrigine = -1;
                    if (origine.equals("main")) {
                        idOrigine = MainActivity.list.get(position).id;
                    } else {
                        idOrigine = FindActivity.list.get(position).id;
                    }
                    final int id = idOrigine;

                    final String quantite = qteProduit.getText().toString();
                    final String adresse = adresseLivrasion.getText().toString();
                    final String mode = modePaiement.getSelectedItem().toString();

                    if (quantite.isEmpty()){
                        Toast.makeText(getApplicationContext(), "Veuillez indiquer la quantité démandée", Toast.LENGTH_LONG).show();
                    }
                    else if (adresse.isEmpty()){
                        Toast.makeText(getApplicationContext(), "Veuillez indiquer votre adresse complète", Toast.LENGTH_LONG).show();
                    }
                    else {

                        String url = "http://www.mossosouk.com/acheterNewMobile?id=" + id + "&nom=" + name + "&prenom= &quantite=" + quantite + "&moyenpaiement=" + mode + "&livraison= &ligne1=" + adresse + "&tel1=" + tel;

                        BuyTask buyTask = new BuyTask();
                        buyTask.execute(url);

                    }
                }
            });

    }

}
