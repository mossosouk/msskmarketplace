package com.mossosouk.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;

public class AnimationActivity extends Activity {

    boolean handler;
    //ImageView logoAnimated;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animation);

        //logoAnimated = (ImageView) findViewById(R.id.logo);
        //Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.animation);
        //logoAnimated.startAnimation(animation);

        int TIME_OUT = 2000;
        handler = new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                SharedPreferences sharedPreferences = getSharedPreferences(LoginActivity.fileName, Context.MODE_PRIVATE);
                if (sharedPreferences.getString("contact", "").equals("")) {

                    Toast.makeText(getApplicationContext(), "Veuillez vous inscrire s'il vous plaît!", Toast.LENGTH_LONG).show();

                    SendUserToLoginActivity();

                }
                else {

                    SendUserToMainActivity();

                }

            }
        }, TIME_OUT);

    }

    private void SendUserToLoginActivity() {

        Intent loginIntent = new Intent(AnimationActivity.this, LoginActivity.class);
        startActivity(loginIntent);
        finish();

    }

    private void SendUserToMainActivity() {

        Intent mainIntent = new Intent(AnimationActivity.this, MainActivity.class);
        startActivity(mainIntent);
        finish();

    }
}
