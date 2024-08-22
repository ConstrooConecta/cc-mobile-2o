package com.example.construconecta_interdisciplinar_certo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class TelaCadastro1 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_cadastro1);
    }

    public void voltarMain(View view) {
        finish();
    }

    public void AvancarParaLogin(View view) {
        Intent intent = new Intent(TelaCadastro1.this, TelaLogin3.class);
        startActivity(intent);
    }
}
