package com.example.construconecta_interdisciplinar_certo.ui;

import android.os.Bundle;
import android.view.View;

import com.example.construconecta_interdisciplinar_certo.R;

public class AtualizacoesFuturas extends BaseActivity {
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
