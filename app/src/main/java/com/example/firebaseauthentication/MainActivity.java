package com.example.firebaseauthentication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

import java.util.Arrays;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private CardView cardView_btnLoginGoogle, cardView_btnLoginFacebook, cardView_btnLoginEmail;
    private LinearLayout linearLayout_cadastrar;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private GoogleSignInClient mGoogleSignInClient;
    private CallbackManager mCallbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        cardView_btnLoginGoogle = (CardView) findViewById(R.id.cardview_google);
        cardView_btnLoginFacebook = (CardView) findViewById(R.id.cardview_facebook);
        cardView_btnLoginEmail = (CardView) findViewById(R.id.cardview_email);
        linearLayout_cadastrar = (LinearLayout) findViewById(R.id.linearLayout_cadastrar);

        cardView_btnLoginGoogle.setOnClickListener(this);
        cardView_btnLoginFacebook.setOnClickListener(this);
        cardView_btnLoginEmail.setOnClickListener(this);
        linearLayout_cadastrar.setOnClickListener(this);

        servicosGoogle();
        servicosFacebook();
        servicosEmail();

    }



    private void servicosGoogle() {
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void servicosFacebook() {
        mCallbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(mCallbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        AdicionarContaFacebookFirebase(loginResult.getAccessToken());
                    }

                    @Override
                    public void onCancel() {
                    }

                    @Override
                    public void onError(FacebookException error) {
                        String erro = error.getMessage();
                        Util.CustomMessageError(erro, getBaseContext());
                    }
                });
    }

    private void servicosEmail() {
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null && user.isEmailVerified()) {
                    Toast.makeText(MainActivity.this, "Usuario " + user.getDisplayName() + " logado", Toast.LENGTH_SHORT).show();
                }
            }
        };
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cardview_google:
                SignGoogle();
                break;

            case R.id.cardview_facebook:
                SignFacebook();
                break;

            case R.id.cardview_email:
                SignEmail();
                break;

            case R.id.linearLayout_cadastrar:
                startActivity(new Intent(this, CadastrarActivity.class));
                break;
        }
    }

    private void SignGoogle() {
        GoogleSignInAccount googleSignInAccount = GoogleSignIn.getLastSignedInAccount(this);

        if (googleSignInAccount == null) {
            // nao conectado
            Intent intent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(intent, 555);
        } else {
            // conectado
            Toast.makeText(this, "Conectado", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getBaseContext(), PrincipalActivity.class));
            finish();
        }
    }

    private void SignFacebook() {
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "email"));
    }

    private void SignEmail() {
        mUser = mAuth.getCurrentUser();

        if (mUser == null || !mUser.isEmailVerified()) {
            startActivity(new Intent(this, LoginEmailActivity.class));
        } else {
            startActivity(new Intent(this, PrincipalActivity.class));
            finish();
        }
    }

    // Retorno da tela de login do Google
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        mCallbackManager.onActivityResult(requestCode, resultCode, data); // Config Facebook
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 555) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            try {
                GoogleSignInAccount googleSignInAccount = task.getResult(ApiException.class);
                AdicionarContaGoogleFirebase(googleSignInAccount);
            } catch (ApiException err) {
                Toast.makeText(this, "Erro ao conectar com a conta do Google", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void AdicionarContaGoogleFirebase(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            startActivity(new Intent(getBaseContext(), PrincipalActivity.class));
                            finish();
                        } else {
                            Toast.makeText(MainActivity.this, "Erro ao criar conta com o Google", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void AdicionarContaFacebookFirebase(AccessToken token) {
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            startActivity(new Intent(getBaseContext(), PrincipalActivity.class));
                            finish();
                        } else {
                            Toast.makeText(MainActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();

        mAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (mAuthStateListener != null) {
            mAuth.removeAuthStateListener(mAuthStateListener);
        }
    }
}
