package com.mossosouk.app;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    Toolbar toolbar;
    ProgressBar progressBar;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    RecyclerView recyclerView;
    RecyclerAdapter recyclerAdapter;

    private CircleImageView headerUserImage;
    private TextView headerUsername, headerUserContacts;

    Uri selectImageUri;

    private ProgressDialog loadingBar;

    String countryCode = "+235";
    String numeroComplet;

    SQLiteDatabase db;
    boolean itShouldLoadMore = true;
    boolean firstLoad = true;
    String categorie = null;
    int page = 1;

    SharedPreferences sp;
    static String spFile = "file";

    public static List<Annonce> list = new ArrayList<Annonce>();

    Integer GALLERY_PICK = 1;
    Bitmap selectedImage;

    public class DownloadTask extends AsyncTask<String, Void, String> {

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
                db.execSQL("CREATE TABLE IF NOT EXISTS annonce (id INTEGER PRIMARY KEY, titre VARCHAR, description VARCHAR, prix INT, image VARCHAR, imageBig VARCHAR, vendeur VARCHAR, contact VARCHAR, categorieNom VARCHAR)");

                page = 1;
                list.clear();

                if (connected) {
                    try {
                        JSONObject jsonObject = new JSONObject(s);
                        JSONArray jsonArray = jsonObject.getJSONArray("annonce");

                        db.execSQL("DELETE FROM annonce");

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

                            String sql = "INSERT INTO annonce(id, titre, description, prix, image, imageBig, vendeur, contact, categorieNom) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)";
                            SQLiteStatement statement = db.compileStatement(sql);
                            statement.bindLong(1, ann.id);
                            statement.bindString(2, ann.titre);
                            statement.bindString(3, ann.description);
                            statement.bindLong(4, ann.prix);
                            statement.bindString(5, ann.image);
                            //statement.bindBlob(4, byteArrayOutputStream.toByteArray());
                            statement.bindString(6, ann.imageBig);
                            statement.bindString(7, ann.vendeur);
                            statement.bindString(8, ann.contact);
                            statement.bindString(9, ann.categorie);

                            statement.execute();
                        }

                        if (noElements){
                            Toast.makeText(MainActivity.this, "Aucun élément", Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Erreur Internet", Toast.LENGTH_LONG).show();
                }

                Cursor c = db.rawQuery("SELECT * FROM annonce ORDER BY id DESC", null);
                int idIndex = c.getColumnIndex("id");
                int nameIndex = c.getColumnIndex("titre");
                int descIndex = c.getColumnIndex("description");
                int prixIndex = c.getColumnIndex("prix");
                int imageIndex = c.getColumnIndex("image");
                int imageBigIndex = c.getColumnIndex("imageBig");
                int vendeurIndex = c.getColumnIndex("vendeur");
                int contactIndex = c.getColumnIndex("contact");
                int catIndex = c.getColumnIndex("categorieNom");

                c.moveToFirst();
                while (!c.isAfterLast()) {
                    Annonce ann = new Annonce();
                    ann.id = c.getInt(idIndex);
                    ann.titre = c.getString(nameIndex);
                    ann.description = c.getString(descIndex);
                    ann.prix = c.getInt(prixIndex);
                    //byte[] dataImage = c.getBlob(imageIndex);

                    //ann.image = BitmapFactory.decodeByteArray(dataImage, 0, dataImage.length);
                    ann.image = c.getString(imageIndex);
                    ann.imageBig = c.getString(imageBigIndex);
                    ann.vendeur = c.getString(vendeurIndex);
                    ann.contact = c.getString(contactIndex);
                    ann.categorie = c.getString(catIndex);

                    list.add(ann);

                    c.moveToNext();
                }


                progressBar.setVisibility(View.INVISIBLE);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
                recyclerView.setLayoutManager(linearLayoutManager);
                recyclerView.setHasFixedSize(true);
                recyclerView.setAdapter(new RecyclerAdapter(getApplicationContext(), list, "main"));

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

    public void loadMore() {
        page++;
        updateView(categorie, page);
        itShouldLoadMore = false;
    }

    public void updateView (@Nullable String categorie, @Nullable Integer page) {
        String result = "";
        DownloadTask task = new DownloadTask();
        if (categorie == null) {

            if (page == null) {
                task.execute("http://www.mossosouk.com/getJson");
            } else {
                task.execute("http://www.mossosouk.com/getJson?page=" + page);
            }

        } else {
            if (page == null) {
                task.execute("http://www.mossosouk.com/getJson" + "?nom=" + categorie);
            } else {
                task.execute("http://www.mossosouk.com/getJson" + "?nom=" + categorie + "&page=" + page);
            }

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.navigation_drawer);

        sp = getSharedPreferences(spFile, Context.MODE_PRIVATE);

        loadingBar = new ProgressDialog(this);

        SharedPreferences sharedPreferences = getSharedPreferences(LoginActivity.fileName, Context.MODE_PRIVATE);
        String userName = sharedPreferences.getString("username", "");
        String userContacts = sharedPreferences.getString("contact", "");

        recyclerAdapter = new RecyclerAdapter(getApplicationContext(), list, "main");

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Produits Récents");

        progressBar = (ProgressBar) findViewById(R.id.progressbar);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layour);
        navigationView = (NavigationView) findViewById(R.id.navigation_view);

        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);
        headerUsername = (TextView) headerView.findViewById(R.id.headerUserName);
        headerUserContacts = (TextView) headerView.findViewById(R.id.headerUserContacts);
        headerUserImage = (CircleImageView) headerView.findViewById(R.id.headerUserImage);

        numeroComplet = countryCode + userContacts;

        headerUsername.setText(userName);
        headerUserContacts.setText(numeroComplet);

        String image = sp.getString("image", "");
        if (!image.equals("")){
            //decode string to image
            String base = image;
            byte[] imageAsBytes = Base64.decode(base.getBytes(), Base64.DEFAULT);
            headerUserImage.setImageBitmap(BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length) );
        }

        headerUserImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_PICK);
            }
        });

        ActionBarDrawerToggle toogle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_drawer, R.string.close_drawer);
        drawerLayout.setDrawerListener(toogle);

        toogle.syncState();

        recyclerView = (RecyclerView) findViewById(R.id.recycler);

        FloatingActionButton fab = (FloatingActionButton)  findViewById(R.id.fabVendre);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), SellActivity.class);
                startActivity(intent);

            }
        });

        db = this.openOrCreateDatabase("Annonces", Context.MODE_PRIVATE, null);

        updateView(categorie, page);



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK){

            loadingBar.setTitle("En cours de chargement");
            loadingBar.setMessage("Veuillez patienter s'il vous plait...");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            selectImageUri = data.getData();

            try {
                selectedImage = MediaStore.Images.Media.getBitmap(MainActivity.this.getContentResolver(), selectImageUri);
            } catch (IOException e) {
                e.printStackTrace();
            }

            Picasso.with(MainActivity.this).load(selectImageUri).into(headerUserImage);

            String image = getStringImage(selectedImage);
            sp.edit().putString("image", image).apply();

            Toast.makeText(MainActivity.this, "Votre photo de profil est mise à jour avec succès", Toast.LENGTH_SHORT).show();

            loadingBar.dismiss();

        }

    }

    public String getStringImage(Bitmap bitmap){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();
        String temp = Base64.encodeToString(b, Base64.DEFAULT);

        return temp;
    }

    @Override
    public void onBackPressed() {

        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {

            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        switch (id) {

            case R.id.home :
                updateView(null, page);
                break;

            case R.id.immobilier :
                categorie = "Immobilier";
                break;

            case R.id.imageson :
                categorie = "Image-Son";
                break;

            case R.id.informatique :
                categorie = "Informatique";
                break;

            case R.id.telephonie :
                categorie = "Téléphonie";
                break;

            case R.id.emploi :
                categorie = "Emploi-Stage";
                break;

            case R.id.vehicule :
                categorie = "Véhicule-Groupe";
                break;

            case R.id.electromenager :
                categorie = "Electroménager";
                break;

            case R.id.mobilier :
                categorie = "Mobilier";
                break;

            case R.id.evenement :
                categorie = "Evènements";
                break;

            case R.id.mode :
                categorie = "Mode";
                break;

            case R.id.opp :
                categorie = "Opportunités";
                break;

            case R.id.sante :
                categorie = "Santé-Beauté";
                break;

            case R.id.aliment :
                categorie = "Aliments-Boissons";
                break;

            case R.id.livre :
                categorie = "Livres-Loisirs";
                break;

            case R.id.top :

                break;

            case R.id.faq :
                Intent faqIntent = new Intent(getApplicationContext(), FaqActivity.class);
                startActivity(faqIntent);
                break;

            case R.id.reglement :
                Intent reglementIntent = new Intent(getApplicationContext(), TermsAndConditionsActivity.class);
                startActivity(reglementIntent);
                break;

            case R.id.about :
                Intent intent = new Intent(getApplicationContext(), AboutActivity.class);
                startActivity(intent);
                break;
        }

        drawerLayout.closeDrawer(GravityCompat.START);

        updateView(categorie, 1);
        if (categorie == null){
            toolbar.setTitle("Produits Récents");
        }
        else{
            toolbar.setTitle(categorie);
        }
        firstLoad = true;

        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);

        if (numeroComplet.equals("+23566983404") || numeroComplet.equals("+23566030747") || numeroComplet.equals("+23568732098") || numeroComplet.equals("+23566713009") || numeroComplet.equals("+23563729357") || numeroComplet.equals("+23562353631") || numeroComplet.equals("+23565602569")){
            menu.findItem(R.id.dashboard).setVisible(true);
        }
        else menu.findItem(R.id.dashboard).setVisible(false);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.dashboard) {
            Intent dashboardIntent = new Intent(MainActivity.this, DashboardActivity.class);
            startActivity(dashboardIntent);

        }

        if (id == R.id.rechercher) {
            Intent findIntent = new Intent(MainActivity.this, FindActivity.class);
            startActivity(findIntent);
        }

        return super.onOptionsItemSelected(item);
    }

}
