package com.example.construconecta_interdisciplinar_certo.shop.conta;

import android.os.Bundle;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.example.construconecta_interdisciplinar_certo.R;
import com.example.construconecta_interdisciplinar_certo.ui.BaseActivity;

public class PoliticaPrivacidadeActivity extends BaseActivity {
    ImageButton voltar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_politica_privacidade);
        voltar = findViewById(R.id.voltar3);
        voltar.setOnClickListener(v -> finish());
    }
}
