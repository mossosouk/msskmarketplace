package com.mossosouk.app;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static com.mossosouk.app.R.id.toolbar;

public class DashboardActivity extends AppCompatActivity {


    Toolbar toolbar;
    RecyclerView dashboard_recyclerView;
    DashboardAdapter dashboard_recylcerAdapter;

    public static List<Commande> list = new ArrayList<Commande>();

    ProgressBar pb;

    SQLiteDatabase db;
    boolean itShouldLoadMore = true;
    boolean firstLoad = true;
    int page = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        toolbar = (Toolbar) findViewById(R.id.dashboard_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Dashboard commandes");

        pb = (ProgressBar) findViewById(R.id.pb);

        dashboard_recyclerView = (RecyclerView) findViewById(R.id.dashboard_recyclerView);
        dashboard_recylcerAdapter = new DashboardAdapter(getApplicationContext(), list);

        db = this.openOrCreateDatabase("Commandes", Context.MODE_PRIVATE, null);

        updateView(page);

    }

    public class GetTask extends AsyncTask<String, Void, String> {

        Boolean connected = true;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pb.setVisibility(View.VISIBLE);
            itShouldLoadMore = false;
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
                return result;

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

            if (firstLoad) {
                db.execSQL("CREATE TABLE IF NOT EXISTS commande (id INTEGER PRIMARY KEY, titre VARCHAR, quantite INT, prix INT, image VARCHAR, vendeurNom VARCHAR, vendeurTel VARCHAR, acheteurNom VARCHAR, acheteurTel VARCHAR, moyenpaiement VARCHAR)");

                page = 1;
                list.clear();

                if (connected) {
                    try {
                        JSONObject jsonObject = new JSONObject(s);
                        JSONArray jsonArray = jsonObject.getJSONArray("commande");

                        db.execSQL("DELETE FROM commande");

                        boolean noElements = true;

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject commande = jsonArray.getJSONObject(i);

                            noElements = false;

                            Commande cmd = new Commande();

                            cmd.id = commande.getInt("id");

                            cmd.titre = commande.getString("titre");

                            if (commande.isNull("prix")) {
                                cmd.prix = -1
                                ;
                            } else {
                                cmd.prix = commande.getInt("prix");
                            }

                            cmd.quantite = commande.getInt("quantite");

                            cmd.image = commande.getString("image");

                            //cmd.imageBig = "http://www.mossosouk.com/" + annonce.getString("image2");

                            cmd.vendeur = commande.getString("vendeurNom");
                            cmd.contactVendeur = commande.getString("vendeurTel");

                            cmd.acheteur = commande.getString("acheteurNom");
                            cmd.contactAcheteur = commande.getString("acheteurTel");

                            cmd.moyenLivraison = commande.getString("moyenpaiement");

                            String sql = "INSERT INTO commande(id, titre, quantite, prix, image, vendeurNom, vendeurTel, acheteurNom, acheteurTel, moyenpaiement) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                            SQLiteStatement statement = db.compileStatement(sql);
                            statement.bindLong(1, cmd.id);
                            statement.bindString(2, cmd.titre);
                            statement.bindLong(3, cmd.quantite);
                            statement.bindLong(4, cmd.prix);
                            statement.bindString(5, cmd.image);
                            //statement.bindBlob(4, byteArrayOutputStream.toByteArray());
                            statement.bindString(6, cmd.vendeur);
                            statement.bindString(7, cmd.contactVendeur);
                            statement.bindString(8, cmd.acheteur);
                            statement.bindString(9, cmd.contactAcheteur);
                            statement.bindString(10, cmd.moyenLivraison);

                            statement.execute();
                        }

                        if (noElements){
                            Toast.makeText(DashboardActivity.this, "Aucun élément", Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Erreur Internet", Toast.LENGTH_LONG).show();
                }

                Cursor c = db.rawQuery("SELECT * FROM commande ORDER BY id DESC", null);
                int idIndex = c.getColumnIndex("id");
                int nameIndex = c.getColumnIndex("titre");
                int qteIndex = c.getColumnIndex("quantite");
                int prixIndex = c.getColumnIndex("prix");
                int imageIndex = c.getColumnIndex("image");
                int vendeurIndex = c.getColumnIndex("vendeurNom");
                int contactVendIndex = c.getColumnIndex("vendeurTel");
                int acheteurIndex = c.getColumnIndex("acheteurNom");
                int contactAchetIndex = c.getColumnIndex("acheteurTel");
                int livraisonIndex = c.getColumnIndex("moyenpaiement");

                c.moveToFirst();
                while (!c.isAfterLast()) {
                    Commande cmd = new Commande();
                    cmd.id = c.getInt(idIndex);
                    cmd.titre = c.getString(nameIndex);
                    cmd.quantite = c.getInt(qteIndex);
                    cmd.prix = c.getInt(prixIndex);
                    //byte[] dataImage = c.getBlob(imageIndex);

                    //ann.image = BitmapFactory.decodeByteArray(dataImage, 0, dataImage.length);
                    cmd.image = c.getString(imageIndex);
                    //ann.imageBig = c.getString(imageBigIndex);
                    cmd.vendeur = c.getString(vendeurIndex);
                    cmd.contactVendeur = c.getString(contactVendIndex);
                    cmd.acheteur = c.getString(acheteurIndex);
                    cmd.contactAcheteur = c.getString(contactAchetIndex);
                    cmd.moyenLivraison = c.getString(livraisonIndex);

                    list.add(cmd);

                    c.moveToNext();
                }


                pb.setVisibility(View.INVISIBLE);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
                dashboard_recyclerView.setLayoutManager(linearLayoutManager);
                dashboard_recyclerView.setHasFixedSize(true);
                dashboard_recyclerView.setAdapter(new DashboardAdapter(getApplicationContext(), list));

                dashboard_recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                        super.onScrollStateChanged(recyclerView, newState);
                    }

                    @Override
                    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);

                        if (dy > 0) {
                            if (!recyclerView.canScrollVertically(RecyclerView.FOCUS_DOWN)) {

                                if (itShouldLoadMore && page != -1) {
                                    loadMore();
                                }
                            }
                        }
                    }
                });

                firstLoad = false;
            } else {

                try {
                    JSONObject jsonObject = new JSONObject(s);
                    JSONArray jsonArray = jsonObject.getJSONArray("commande");

                    boolean noElements = true;

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject commande = jsonArray.getJSONObject(i);

                        noElements = false;

                        Commande cmd = new Commande();
                        cmd.id = commande.getInt("id");

                        cmd.titre = commande.getString("titre");

                        if (commande.isNull("prix")) {
                            cmd.prix = -1 ;
                        } else {
                            cmd.prix = commande.getInt("prix");
                        }

                        cmd.quantite = commande.getInt("quantite");

                        cmd.image = commande.getString("image");

                        //cmd.imageBig = "http://www.mossosouk.com/" + annonce.getString("image2");

                        cmd.vendeur = commande.getString("vendeurNom");
                        cmd.contactVendeur = commande.getString("vendeurTel");

                        cmd.acheteur = commande.getString("acheteurNom");
                        cmd.contactAcheteur = commande.getString("acheteurTel");

                        cmd.moyenLivraison = commande.getString("moyenpaiement");

                        list.add(cmd);

                        DashboardAdapter dashboardAdapter  = (DashboardAdapter) dashboard_recyclerView.getAdapter();
                        dashboardAdapter.notifyDataSetChanged();

                        pb.setVisibility(View.INVISIBLE);
                    }

                    if(noElements){
                        pb.setVisibility(View.INVISIBLE);
                        page = -1;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            itShouldLoadMore = true;
        }
    }

    public void loadMore() {
            page++;
            updateView(page);
            itShouldLoadMore = false;
    }

    public void updateView (@Nullable Integer page) {
        GetTask task = new GetTask();
        task.execute("http://www.mossosouk.com/cmdMobile?page=" + page);
    }
}
