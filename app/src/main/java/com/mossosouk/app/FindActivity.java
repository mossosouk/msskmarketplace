package com.mossosouk.app;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

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

public class FindActivity extends AppCompatActivity {

    EditText searchInput;
    Button searchBtn;

    public static List<Annonce> list = new ArrayList<Annonce>();
    RecyclerView recyclerView;

    ProgressBar progressBar;

    boolean itShouldLoadMore = true;
    boolean firstLoad = true;
    int page = 1;
    String motcle;


    public class FindTask extends AsyncTask<String, Void, String> {

        Boolean connected = true;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
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

                page = 1;
                list.clear();

                if (connected) {
                    try {
                        JSONObject jsonObject = new JSONObject(s);
                        JSONArray jsonArray = jsonObject.getJSONArray("annonce");

                        boolean noElements = true;

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject annonce = jsonArray.getJSONObject(i);

                            noElements = false;

                            Annonce ann = new Annonce();
                            ann.titre = annonce.getString("titre");

                            if (annonce.isNull("prix")) {
                                ann.prix = -1
                                ;
                            } else {
                                ann.prix = annonce.getInt("prix");
                            }

                            ann.description = annonce.getString("description");

                            ann.image = annonce.getString("image");

                            ann.imageBig = "http://www.mossosouk.com/" + annonce.getString("image2");

                            ann.vendeur = annonce.getString("vendeurNom");

                            ann.id = annonce.getInt("id");

                            ann.categorie = annonce.getString("categorieNom");

                            ann.contact = annonce.getString("contact");

                            list.add(ann);
                        }

                        if (noElements){
                            Toast.makeText(FindActivity.this, "Aucun élément", Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Erreur Internet", Toast.LENGTH_LONG).show();
                }

                progressBar.setVisibility(View.INVISIBLE);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
                recyclerView.setLayoutManager(linearLayoutManager);
                recyclerView.setHasFixedSize(true);
                recyclerView.setAdapter(new RecyclerAdapter(getApplicationContext(), list, "find"));

                recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
                    JSONArray jsonArray = jsonObject.getJSONArray("annonce");

                    boolean noElements = true;

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject annonce = jsonArray.getJSONObject(i);

                        noElements = false;

                        Annonce ann = new Annonce();
                        ann.id = annonce.getInt("id");
                        ann.titre = annonce.getString("titre");

                        if (annonce.isNull("prix")) {
                            ann.prix = -1;
                        } else {
                            ann.prix = annonce.getInt("prix");
                        }

                        ann.description = annonce.getString("description");
                        ann.image = annonce.getString("image");
                        ann.imageBig = "http://www.mossosouk.com/" + annonce.getString("image2");
                        ann.vendeur = annonce.getString("vendeurNom");
                        ann.contact = annonce.getString("contact");
                        ann.categorie = annonce.getString("categorieNom");

                        list.add(ann);

                        RecyclerAdapter recyclerAdapter  = (RecyclerAdapter) recyclerView.getAdapter();
                        recyclerAdapter.notifyDataSetChanged();

                        progressBar.setVisibility(View.INVISIBLE);
                    }

                    if(noElements){
                        progressBar.setVisibility(View.INVISIBLE);
                        page = -1;
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            itShouldLoadMore = true;
        }
    }

    public  void loadMore(){
        page++;
        updateView();
        itShouldLoadMore = false;
    }

    public void updateView() {

        FindTask task = new FindTask();
        String url = "http://www.mossosouk.com/findMobile?mc=" + motcle + "&page=" + page;

        task.execute(url);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find);

        progressBar = (ProgressBar) findViewById(R.id.pb);
        recyclerView = (RecyclerView) findViewById(R.id.searchRV);

        //searchSpinner = (Spinner) findViewById(R.id.searchSpinner);
        searchInput = (EditText) findViewById(R.id.searchInput);
        searchBtn = (Button) findViewById(R.id.searchBtn);

        /*String[] catSpinner = {"Mode", "Image-Son", "Immobilier", "Informatique", "Telephonie", "Emploi-Stage", "Vehicule-Groupe", "Mode", "Opportunites"};
        ArrayAdapter<String> catSpinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, catSpinner);
        searchSpinner.setAdapter(catSpinnerAdapter);*/

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                motcle = searchInput.getText().toString();

                    if (motcle.isEmpty())

                        Toast.makeText(FindActivity.this, "Au moins un mot à rechercher...", Toast.LENGTH_SHORT).show();

                    else {

                        page = 1;
                        progressBar.setVisibility(View.VISIBLE);
                        list.clear();
                        firstLoad = true;
                        updateView();

                    }

            }
        });

    }

}
