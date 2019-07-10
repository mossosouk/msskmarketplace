package com.mossosouk.app;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class SellActivity extends AppCompatActivity {

    Integer REQUEST_CAMERA = 1, SELECT_FILE = 0;
    ImageView addingImage;
    Spinner etatSpinner, categorieSpinner;
    AppCompatButton vendreBtn;

    Bitmap selectedImage, selectedBigImage;

    String mCurrentPhotoPath;

    EditText titre, quantite, description, prix;
    TextView voirCommission;

    //String etatc, categorie, title, desc, qte, price;

    ProgressDialog pd ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sell);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        pd = new ProgressDialog(SellActivity.this);

        titre = (EditText) findViewById(R.id.titreEdit);
        quantite = (EditText) findViewById(R.id.qteEdit);
        description = (EditText) findViewById(R.id.descEdit);
        prix = (EditText) findViewById(R.id.prixEdit);

        etatSpinner = (Spinner) findViewById(R.id.etatSpinner);
        categorieSpinner = (Spinner) findViewById(R.id.categorieSpinner);

        String[] etat = {"Neuf", "Occasion"};
        String[] cat = {"Immobilier", "Image-Son", "Informatique", "Téléphonie", "Emploi-Stage", "Véhicule-Groupe", "Electroménager", "Mobilier", "Evènements", "Mode", "Opportunités", "Santé-Beauté", "Aliments-Boissons", "Livres-Loisirs"};

        ArrayAdapter<String> etatAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, etat);
        etatSpinner.setAdapter(etatAdapter);

        ArrayAdapter<String> catAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, cat);
        categorieSpinner.setAdapter(catAdapter);

        addingImage = (ImageView) findViewById(R.id.addImage);
        addingImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectImage();
            }
        });

        SelectImage();

        SharedPreferences sp = getSharedPreferences(LoginActivity.fileName, Context.MODE_PRIVATE);
        final String contact = sp.getString("contact", "");

        vendreBtn = (AppCompatButton) findViewById(R.id.vendreBtn);
        voirCommission = (TextView) findViewById(R.id.voirCommission);

        voirCommission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(SellActivity.this, CommissionActivity.class);
                startActivity(i);

            }
        });

        vendreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String etat = etatSpinner.getSelectedItem().toString();
                final String categorie = categorieSpinner.getSelectedItem().toString();
                final String title = titre.getText().toString();
                final String qte = quantite.getText().toString();
                final String desc = description.getText().toString();
                final String price = prix.getText().toString();

                if (selectedImage == null){
                    Toast.makeText(SellActivity.this, "Désolé, l'image est obligatoire", Toast.LENGTH_SHORT).show();
                }

                else if (title.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Désolé, veuillez indiquer le nom du produit", Toast.LENGTH_LONG).show();
                }
                else if (qte.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Désolé, veuillez indiquer la quantité", Toast.LENGTH_LONG).show();
                }
                else if (desc.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Désolé, veuillez décrire le produit", Toast.LENGTH_LONG).show();
                }
                else if (price.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Désolé, veuillez mentionner le prix", Toast.LENGTH_LONG).show();
                }
                else {

                    AlertDialog.Builder builder = new AlertDialog.Builder(SellActivity.this);
                    builder.setTitle("Confirmation");
                    builder.setMessage("Etes-vous sûr de vouloir publier?");
                    //builder.setIcon(R.drawable.ic_check_circle_black_24dp);
                    builder.setCancelable(false);
                    builder.setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            pd.setMessage("Veuillez patienter s'il vous plait...");
                            pd.setCanceledOnTouchOutside(false);
                            pd.show();

                            String url = "http://www.mossosouk.com/vendreMobile";

                            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

                                @Override
                                public void onResponse(String response) {

                                    pd.dismiss();

                                    AlertDialog.Builder builder = new AlertDialog.Builder(SellActivity.this);
                                    builder.setTitle("Confirmation");
                                    builder.setMessage("Votre produit est bien enregistré mais est en attente de validation. Il sera publié dans un quart d'heure s'il respecte les normes de qualité. Merci pour votre confiance!");
                                    builder.setIcon(R.drawable.ic_check_circle_black_24dp);
                                    builder.setCancelable(false);
                                    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                            dialog.dismiss();
                                            Intent i = new Intent(SellActivity.this, MainActivity.class);
                                            startActivity(i);
                                        }
                                    });
                                    AlertDialog alertDialog = builder.create();
                                    alertDialog.show();
                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {

                                    pd.dismiss();

                                    AlertDialog.Builder builder = new AlertDialog.Builder(SellActivity.this);
                                    builder.setTitle("Problème de connexion");
                                    builder.setMessage("Vérifier votre connexion s'il vous plait...");
                                    builder.setIcon(R.drawable.ic_error_black_24dp);
                                    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                            dialog.dismiss();
                                        }
                                    });
                                    AlertDialog alertDialog = builder.create();
                                    alertDialog.show();
                                }
                            }) {
                                @Override
                                protected Map<String, String> getParams() throws AuthFailureError {
                                    Map<String, String> params = new HashMap<String, String>();
                                    params.put("etat", etat);
                                    params.put("categorie", categorie);
                                    params.put("titre", title);
                                    params.put("quantite", qte);
                                    params.put("description", desc);
                                    params.put("prix", price);
                                    params.put("contact", contact);

                                    String image = getStringImage(selectedImage);
                                    params.put("image", image);
                                    return params;
                                }
                            };

                            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                            requestQueue.add(stringRequest);
                        }
                    });

                    builder.setNegativeButton("Non", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();


                }

            }

        });

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

    }

    public void SelectImage(){

        final CharSequence[] items = {"Prendre une photo", "Ouvrir la galerie", "Annuler"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Ajout d'image");
        builder.setCancelable(false);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (items[which].equals("Prendre une photo")){

                    dispatchTakePictureIntent();

                }
                else if (items[which].equals("Ouvrir la galerie")){
                    /*Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image*//*");
                    startActivityForResult(Intent.createChooser(intent, "Selectionnez un fichier"), SELECT_FILE);*/

                    Intent galleryIntent = new Intent();
                    galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                    galleryIntent.setType("image/*");
                    startActivityForResult(galleryIntent, SELECT_FILE);

                }
                else {
                    dialog.dismiss();
                    SendUserToMainActivity();
                }
            }
        });

        builder.show();
    }

    public void SendUserToMainActivity(){

        Intent mainIntent = new Intent(SellActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK){

            if (requestCode == REQUEST_CAMERA){

                /*Bundle bundle = data.getExtras();
                //final Bitmap bitmap = (Bitmap) bundle.get("data");
                selectedImage = (Bitmap) bundle.get("data");
                addingImage.setImageBitmap(selectedImage);*/

                File file = new File(mCurrentPhotoPath);

                try {
                    selectedImage = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), Uri.fromFile(file));
                    if (selectedImage != null)
                        selectedImage = getResizedBitmap(selectedImage, 800);
                        addingImage.setImageBitmap(selectedImage);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
            else if (requestCode == SELECT_FILE){


                try {
                    Uri selectImageUri = data.getData();

                    InputStream imageStream = getContentResolver().openInputStream(selectImageUri);
                    selectedImage = BitmapFactory.decodeStream(imageStream);

                    selectedImage = getResizedBitmap(selectedImage, 800);// 400 is for example, replace with desired size

                    addingImage.setImageBitmap(selectedImage);

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                //Uri selectImageUri = data.getData();
                /*try {
                    selectedImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectImageUri);
                 } catch (IOException e) {
                    e.printStackTrace();
                }*/
                //addingImage.setImageBitmap(selectedImage);
                //addingImage.setImageURI(selectImageUri);
            }
        }
    }

    public String getStringImage(Bitmap bitmap){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();
        String temp = Base64.encodeToString(b, Base64.DEFAULT);

        return temp;
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.mossosouk.app",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_CAMERA);
            }
        }
    }

    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float)width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }
}