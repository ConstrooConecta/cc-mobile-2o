package com.example.construconecta_interdisciplinar_certo.loginPath;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.construconecta_interdisciplinar_certo.R;

public class TelaLogin3 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_login3);
    }

    public void EnviarCodigoWhatsapp(View view) {
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