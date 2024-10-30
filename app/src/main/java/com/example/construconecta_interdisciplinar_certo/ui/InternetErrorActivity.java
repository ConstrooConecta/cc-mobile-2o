package com.example.construconecta_interdisciplinar_certo.ui;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.construconecta_interdisciplinar_certo.R;

public class InternetErrorActivity extends AppCompatActivity {
    // O NetworkCallback para monitorar a conexão
    private final ConnectivityManager.NetworkCallback networkCallback = new ConnectivityManager.NetworkCallback() {
        @Override
        public void onAvailable(Network network) {
            super.onAvailable(network);
            runOnUiThread(() -> onConnectionRestored());
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_internet_error);
        // Registre o callback de rede
        registerNetworkCallback();
    }

    // Método para registrar o NetworkCallback
    private void registerNetworkCallback() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        connectivityManager.registerDefaultNetworkCallback(networkCallback);
    }

    // Método que será chamado quando a conexão for restaurada
    private void onConnectionRestored() {
        // Verifique a conexão (opcional, dependendo da sua lógica)
        if (isConnectedToInternet()) {
            // Finaliza a Activity de erro
            finish(); // Isso vai voltar para a última Activity
        }
    }

    // Método para verificar a conexão (opcional)
    private boolean isConnectedToInternet() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Desregistre o callback de rede para evitar vazamentos de memória
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        connectivityManager.unregisterNetworkCallback(networkCallback);
    }
}
