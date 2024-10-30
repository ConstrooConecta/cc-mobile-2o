package com.example.construconecta_interdisciplinar_certo.ui;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public abstract class BaseActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkInternetConnection();
    }
    private void checkInternetConnection() {
        if (!isConnectedToInternet()) {
            Intent intent = new Intent(this, InternetErrorActivity.class);
            startActivity(intent);
            finish(); // Finaliza a Activity atual
        }
    }
    private boolean isConnectedToInternet() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();
    }
}
