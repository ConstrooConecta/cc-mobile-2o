package com.example.construconecta_interdisciplinar_certo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class Em_breve extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_em_breve);
    }

    public void Voltar(View view) {
        //abrir a intent e abrir nova tela de "Em_breve"
        finish();

    }
}
