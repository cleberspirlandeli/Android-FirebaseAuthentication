package com.example.firebaseauthentication;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CadastrarActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btn_Cadastrar, btn_Voltar;
    private EditText txt_Email, txt_Senha, txt_ConfirmarSenha;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastrar);

        mAuth = FirebaseAuth.getInstance();

        txt_Email = (EditText) findViewById(R.id.txt_email);
        txt_Senha = (EditText) findViewById(R.id.txt_senha);
        txt_ConfirmarSenha = (EditText) findViewById(R.id.txt_confirmar_senha);

        btn_Cadastrar = (Button) findViewById(R.id.btn_cadastrar);
        btn_Voltar = (Button) findViewById(R.id.btn_voltar);

        btn_Cadastrar.setOnClickListener(this);
        btn_Voltar.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_cadastrar:
                CadastrarUsuario();
                break;

            case R.id.btn_voltar:
                //startActivity();
                break;

        }
    }

    private void CadastrarUsuario() {
        String email = txt_Email.getText().toString().trim();
        String senha = txt_Senha.getText().toString();

        if (!FormularioValido())
            return;

        if (!Util.UsuarioConectadoInternet(this)) {
            Toast.makeText(this, "Sem conexao com a internet", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, senha).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {

                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                    user.sendEmailVerification()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(CadastrarActivity.this, "Email de confirmaçao enviado.", Toast.LENGTH_LONG).show();
                                    } else {
                                        Toast.makeText(CadastrarActivity.this, "Falha ao enviar email de confirmaçao, tente novamente.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                } else {
                    String error = task.getException().getMessage();
                    Util.CustomMessageError(error, getBaseContext());
                }

            }

        });
    }

    private Boolean FormularioValido() {
        String email = txt_Email.getText().toString().trim();
        String senha = txt_Senha.getText().toString();
        String confirmarSenha = txt_ConfirmarSenha.getText().toString();

        if (email.isEmpty()) {
            txt_Email.setError("Email invalido");
            return false;
        }

        if (senha.isEmpty() || senha.length() < 6) {
            txt_Senha.setError("Senha invalida");
            return false;
        }

        if (confirmarSenha.isEmpty() || confirmarSenha.length() < 6) {
            txt_ConfirmarSenha.setError("Confirmaçao de senha invalida");
            return false;
        }

        if (!senha.equals(confirmarSenha)) {
            txt_ConfirmarSenha.setError("As senhas sao diferentes");
            return false;
        }

        return true;
    }




}
