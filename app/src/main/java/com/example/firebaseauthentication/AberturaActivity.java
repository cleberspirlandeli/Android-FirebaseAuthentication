package com.example.firebaseauthentication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AberturaActivity extends AppCompatActivity implements Runnable {

    private Handler mHandler;
    private Thread mThread;
    private FirebaseAuth mFirebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_abertura);

        mFirebaseAuth = FirebaseAuth.getInstance();

        mThread = new Thread(this);
        mThread.start();
    }

    @Override
    public void run() {
        try {
            FirebaseUser user = mFirebaseAuth.getCurrentUser();

            Thread.sleep(2000);

            if (user == null || !user.isEmailVerified() ) {
                startActivity(new Intent(getBaseContext(), MainActivity.class));
            } else {
                startActivity(new Intent(getBaseContext(), PrincipalActivity.class));
            }

            finish();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}