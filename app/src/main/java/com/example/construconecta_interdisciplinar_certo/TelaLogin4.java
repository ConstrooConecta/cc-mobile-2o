package com.example.construconecta_interdisciplinar_certo;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class TelaLogin4 extends AppCompatActivity {
    TextView frase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_login4);


        // Após 3 segundos, vá para a próxima tela
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(TelaLogin4.this, TelaLogin5_WhatsApp.class);
            startActivity(intent);
            finish();
        }, 4000);
    }
}