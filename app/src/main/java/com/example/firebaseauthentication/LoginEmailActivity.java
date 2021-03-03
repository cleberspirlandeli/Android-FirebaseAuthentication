package com.example.firebaseauthentication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginEmailActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;

    private Button btn_Cadastrar, btn_Voltar, btn_RecuperarSenha;
    private EditText txt_Email, txt_Senha;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_email);

        mAuth = FirebaseAuth.getInstance();

        txt_Email = (EditText) findViewById(R.id.txt_email);
        txt_Senha = (EditText) findViewById(R.id.txt_senha);

        btn_Cadastrar = (Button) findViewById(R.id.btn_cadastrar);
        btn_Voltar = (Button) findViewById(R.id.btn_voltar);
        btn_RecuperarSenha = (Button) findViewById(R.id.btn_recuperar_senha);

        btn_Cadastrar.setOnClickListener(this);
        btn_RecuperarSenha.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_cadastrar:
                LoginEmail();
                break;

            case R.id.btn_recuperar_senha:
                RecuperarSenha();
                break;
        }
    }

    private void RecuperarSenha() {
        String email = txt_Email.getText().toString();

        if (email.isEmpty()) {
            txt_Email.setError("Campo obrigatorio");
            Toast.makeText(LoginEmailActivity.this, "Informe o email", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.sendPasswordResetEmail(email).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(LoginEmailActivity.this, "Confira seu email", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Util.CustomMessageError(e.getMessage().toString(), getBaseContext());
            }
        });
    }

    private void LoginEmail() {
        String email = txt_Email.getText().toString();
        String senha = txt_Senha.getText().toString();

        mAuth.signInWithEmailAndPassword(email, senha).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {

                    FirebaseUser user = mAuth.getCurrentUser();

                    if (user != null && user.isEmailVerified()) {
                        startActivity(new Intent(getBaseContext(), PrincipalActivity.class));
                        Toast.makeText(LoginEmailActivity.this, "Usuario logado com sucesso", Toast.LENGTH_SHORT).show();
                        finish();
                    }

                    if (!user.isEmailVerified()) {
                        Toast.makeText(LoginEmailActivity.this, "Seu email ainda nao foi verificado", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    String message = task.getException().getMessage();
                    if (message.contains("There is no user"))
                        Toast.makeText(LoginEmailActivity.this, "Email nao cadastrado", Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(LoginEmailActivity.this, "Usuario e/ou Senha invalido", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
