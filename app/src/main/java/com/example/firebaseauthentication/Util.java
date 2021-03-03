package com.example.firebaseauthentication;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import static android.content.Context.CONNECTIVITY_SERVICE;

public class Util {

    public static void CustomMessageError(String error, Context context) {
        if (error.contains("least 6 characters")) {
            Toast.makeText(context, "Digite uma senha maior que 6 caracteres", Toast.LENGTH_SHORT).show();
            return;
        }

        if (error.contains("address is badly")) {
            Toast.makeText(context, "Email invalido", Toast.LENGTH_SHORT).show();
            return;
        }

        if (error.contains("email address is already")) {
            Toast.makeText(context, "Email ja cadastrado", Toast.LENGTH_SHORT).show();
            return;
        }

        if (error.contains("interrupted connection")) {
            Toast.makeText(context, "Sem conexao com a internet", Toast.LENGTH_SHORT).show();
            return;
        }

        if (error.contains("There is no user record corresponding to this identifier. The user may have been deleted")) {
            Toast.makeText(context, "Usuario nao cadastrado", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(context, "Ocorreu um erro inesperado, tente novamente mais tarde.", Toast.LENGTH_SHORT).show();

    }

    public static Boolean UsuarioConectadoInternet(Context context) {
        ConnectivityManager conexao = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo informacao = conexao.getActiveNetworkInfo();

        return informacao != null && informacao.isConnected();
    }
}
