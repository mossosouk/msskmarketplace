package com.mossosouk.app;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.telephony.PhoneNumberUtils;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;

public class LoginActivity extends AppCompatActivity {

    //Integer REQUEST_CAMERA = 1, SELECT_FILE = 0;

    private static final int GalleryPick = 1;
    //CircleImageView imageProfile;
    Bitmap selectedImage;

    static SharedPreferences sharedPreferences;
    public static String fileName = "com.mossosouk.app";

    private AppCompatButton signInBtn, verifyBtn, tryAgainBtn;
    private EditText userNameInput, phoneNumInput, verifCodeInput;
    private LinearLayout linearPhoneNum;

    TextView termsText, verifCodeTextMsg;
    CheckBox termsCheck;
    LinearLayout linearTerms;

    ProgressBar progressBar;

    //String username, phoneNum;

    /*public class VerifyTask extends AsyncTask<String, Void, String> {
        boolean connected = true;

        @Override
        protected String doInBackground(String... urls) {
            String result = "";

            try {
                URL url = new URL(urls[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = connection.getInputStream();
                InputStreamReader reader = new InputStreamReader(inputStream);

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
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    JSONArray array = jsonObject.getJSONArray("reponse");

                    JSONObject reponse = array.getJSONObject(0);
                    String message = reponse.getString("message");

                    progressBar.setVisibility(View.INVISIBLE);

                    if (message.equals("ERROR")) {
                        Toast.makeText(LoginActivity.this, "Erreur. Le code de vérification est incorrect.", Toast.LENGTH_LONG).show();
                    } else if (message.equals("DONE")){
                        Toast.makeText(LoginActivity.this, "Merci pour votre inscription.", Toast.LENGTH_SHORT).show();

                        sharedPreferences = getSharedPreferences(fileName, Context.MODE_PRIVATE);
                        sharedPreferences.edit().putString("username", username).apply();
                        sharedPreferences.edit().putString("contact", phoneNum).apply();
                        sharedPreferences.edit().putBoolean("codeSent", false).apply();

                        SendUserToMainActivity();



                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(LoginActivity.this, "Erreur Connexion", Toast.LENGTH_SHORT).show();
            }
        }
    }*/

    /*public class LoginTask extends AsyncTask<String, Void, String> {

        boolean connected = true;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... urls) {

            String result = "";

            try {

                URL url = new URL(urls[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = connection.getInputStream();
                InputStreamReader reader = new InputStreamReader(inputStream);

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

            progressBar.setVisibility(View.INVISIBLE);
            if (connected) {
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    JSONArray array = jsonObject.getJSONArray("reponse");

                    JSONObject reponse = array.getJSONObject(0);
                    String message = reponse.getString("message");
                    String sms = reponse.getString("sms");

                    sharedPreferences = getSharedPreferences(fileName, Context.MODE_PRIVATE);
                    if (message.equals("EXIST")) {
                        Toast.makeText(LoginActivity.this, "Un sms vous a été envoyé avec un code pour verifier votre identité. Merci de taper le code reçu.", Toast.LENGTH_LONG).show();
                        sharedPreferences.edit().putBoolean("codeSent", true).apply();
                    } else if (message.equals("DONE")){
                        Toast.makeText(LoginActivity.this, "Un sms vous a été envoyé avec un code pour valider votre inscription. Merci de taper le code reçu.", Toast.LENGTH_SHORT).show();
                        sharedPreferences.edit().putBoolean("codeSent", true).apply();
                    }



                    signInBtn.setVisibility(View.INVISIBLE);
                    linearPhoneNum.setVisibility(View.INVISIBLE);
                    userNameInput.setVisibility(View.INVISIBLE);
                    linearTerms.setVisibility(View.INVISIBLE);

                    verifCodeInput.setVisibility(View.VISIBLE);
                    verifCodeTextMsg.setVisibility(View.VISIBLE);
                    verifyBtn.setVisibility(View.VISIBLE);
                    tryAgainBtn.setVisibility(View.VISIBLE);


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(LoginActivity.this, "Erreur Connexion", Toast.LENGTH_SHORT).show();
            }
        }
    }*/


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        termsCheck = (CheckBox) findViewById(R.id.termsCheck);
        termsText = (TextView) findViewById(R.id.termsText);
        //verifCodeTextMsg = (TextView) findViewById(R.id.verif_code_text_msg);
        linearTerms = (LinearLayout) findViewById(R.id.linearTerms);

        termsText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SendUserToTermsAndConditionsActivity();

            }
        });

        userNameInput = (EditText) findViewById(R.id.username);
        phoneNumInput = (EditText) findViewById(R.id.phoneNum);
        //verifCodeInput = (EditText) findViewById(R.id.verif_code);

        //linearPhoneNum = (LinearLayout) findViewById(R.id.linear_phone_num);

        signInBtn = (AppCompatButton) findViewById(R.id.signInBtn);
        //verifyBtn = (AppCompatButton) findViewById(R.id.verifyButton);
        //tryAgainBtn = (AppCompatButton) findViewById(R.id.tryAgainButton);

        progressBar = (ProgressBar) findViewById(R.id.loginprogressbar) ;

        signInBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

               final String username = userNameInput.getText().toString();
               final String phoneNum = phoneNumInput.getText().toString();

                if (username.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Désolé, veuillez entrer votre nom s'il vous plaît!", Toast.LENGTH_LONG).show();
                }
                else if (phoneNum.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Désolé, veuillez entrer votre numéro s'il vous plaît!", Toast.LENGTH_LONG).show();
                } else if(phoneNum.length() < 8 || phoneNum.length() > 8) {
                    Toast.makeText(getApplicationContext(), "Désolé, veuillez entrer un numéro correct!", Toast.LENGTH_LONG).show();
                }

                else if (!termsCheck.isChecked()){
                    Toast.makeText(getApplicationContext(), "Désolé, veuillez d'abord accepter les Termes et Conditions de Mossosouk.com!", Toast.LENGTH_LONG).show();
                }

                else {

                    progressBar.setVisibility(View.VISIBLE);

                    //VolleyCreateUserMobile();
                    String url = "http://www.mossosouk.com/createUserMobile";

                    StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                            builder.setTitle("CONFIRMATION !");
                            builder.setMessage("Bravo! Vous êtes inscrit(e)s sur Mossosouk MarketPlace. Merci pour votre confiance.");
                            builder.setIcon(R.drawable.ic_check_circle_black_24dp);
                            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();

                                    progressBar.setVisibility(View.INVISIBLE);

                                    sharedPreferences = getSharedPreferences(fileName, Context.MODE_PRIVATE);
                                    sharedPreferences.edit().putString("username", username).apply();
                                    sharedPreferences.edit().putString("contact", phoneNum).apply();

                                    SendUserToMainActivity();
                                }
                            });
                            AlertDialog alertDialog = builder.create();
                            alertDialog.show();
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(getApplicationContext(), "Erreur Connexion", Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    }) {
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String, String> params = new HashMap<String, String>();

                            params.put("nom", username);
                            params.put("tel", phoneNum);

                            return params;
                        }
                    };
                    RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                    requestQueue.add(stringRequest);


                }

            }
        });

        /*verifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                signInBtn.setVisibility(View.INVISIBLE);
                linearPhoneNum.setVisibility(View.INVISIBLE);
                userNameInput.setVisibility(View.INVISIBLE);
                linearTerms.setVisibility(View.INVISIBLE);

                String verificationCode = verifCodeInput.getText().toString();

                if (TextUtils.isEmpty(verificationCode)){
                    Toast.makeText(LoginActivity.this, "Veuillez entrer le code de vérification, s'il vous plait...", Toast.LENGTH_SHORT).show();
                }
                else {

                    progressBar.setVisibility(View.VISIBLE);
                    sharedPreferences = getSharedPreferences(fileName, Context.MODE_PRIVATE);
                    String phoneNumVerif = sharedPreferences.getString("phoneNumVerif", "");

                    String url = "http://www.mossosouk.com/validateUserMobile?otp=" + verificationCode + "&tel=" + phoneNumVerif;
                    VerifyTask vtask = new VerifyTask();
                    vtask.execute(url);
                }
            }
        });

        tryAgainBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifCodeInput.setVisibility(View.INVISIBLE);
                verifCodeTextMsg.setVisibility(View.INVISIBLE);
                verifyBtn.setVisibility(View.INVISIBLE);
                tryAgainBtn.setVisibility(View.INVISIBLE);

                signInBtn.setVisibility(View.VISIBLE);
                linearPhoneNum.setVisibility(View.VISIBLE);
                userNameInput.setVisibility(View.VISIBLE);
                linearTerms.setVisibility(View.VISIBLE);

                sharedPreferences = getSharedPreferences(fileName, Context.MODE_PRIVATE);
                sharedPreferences.edit().clear().apply();
            }
        });

        sharedPreferences = getSharedPreferences(fileName, Context.MODE_PRIVATE);
        Boolean codeSent = sharedPreferences.getBoolean("codeSent", false);

        if (codeSent.equals(true)) {
            signInBtn.setVisibility(View.INVISIBLE);
            linearPhoneNum.setVisibility(View.INVISIBLE);
            userNameInput.setVisibility(View.INVISIBLE);
            linearTerms.setVisibility(View.INVISIBLE);

            verifCodeInput.setVisibility(View.VISIBLE);
            verifCodeTextMsg.setVisibility(View.VISIBLE);
            verifyBtn.setVisibility(View.VISIBLE);
            tryAgainBtn.setVisibility(View.VISIBLE);
        }*/
    }

    private void VolleyCreateUserMobile(){

        /*username = userNameInput.getText().toString();
        phoneNum = phoneNumInput.getText().toString();*/


        /*LoginTask ltask = new LoginTask();
        ltask.execute(url);*/

    }

    private void SendUserToMainActivity() {

        Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();

    }

    private void SendUserToTermsAndConditionsActivity() {

        Intent termsIntent = new Intent(LoginActivity.this, TermsAndConditionsActivity.class);
        //termsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(termsIntent);
        //finish();

    }

}

