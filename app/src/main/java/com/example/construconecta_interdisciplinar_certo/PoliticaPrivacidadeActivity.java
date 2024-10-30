package com.example.construconecta_interdisciplinar_certo;

import android.os.Bundle;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class PoliticaPrivacidadeActivity extends AppCompatActivity {
    ImageButton voltar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_politica_privacidade);
        voltar = findViewById(R.id.voltar3);
        voltar.setOnClickListener(v -> finish());
    }
}
