package com.example.construconecta_interdisciplinar_certo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class TelaLogin3 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_login3);
    }

    public void EnviarCodigo(View view) {
        Intent intent = new Intent(TelaLogin3.this, TelaLogin4.class);
        startActivity(intent);
    }

    public void EnviarCodigoSms(View view) {
        Intent intent = new Intent(TelaLogin3.this, TelaLogin4_sms.class);
        startActivity(intent);
    }

    public void EnviarCodigoEmail(View view) {
        Intent intent = new Intent(TelaLogin3.this, TelaLogin4_email.class);
        startActivity(intent);
    }
}