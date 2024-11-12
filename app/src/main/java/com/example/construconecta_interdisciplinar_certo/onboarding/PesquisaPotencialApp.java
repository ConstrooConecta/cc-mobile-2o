package com.example.construconecta_interdisciplinar_certo.onboarding;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.construconecta_interdisciplinar_certo.R;
import com.example.construconecta_interdisciplinar_certo.ui.MainActivity;

public class PesquisaPotencialApp extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pesquisa_potencial_app);

        // Configuração do WebView
        WebView webView = findViewById(R.id.wbView);
        webView.setWebViewClient(new WebViewClient());
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true); // Ativa o JavaScript, se necessário
        webView.loadUrl("https://cc-ad-forms-ia-2o-4.onrender.com/");

        // Configurar botão de voltar
        ImageButton btnVoltarPesquisa = findViewById(R.id.btnVoltarPesquisa);
        TextView txtSairPesquisa = findViewById(R.id.txtSairPesquisa);

        View.OnClickListener voltarListener = view -> {
            Intent intent = new Intent(PesquisaPotencialApp.this, MainActivity.class);
            startActivity(intent);
            finish();
        };

        // Atribui o listener ao botão e ao texto
        btnVoltarPesquisa.setOnClickListener(voltarListener);
        txtSairPesquisa.setOnClickListener(voltarListener);
    }
}