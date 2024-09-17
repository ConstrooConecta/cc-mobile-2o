package com.example.construconecta_interdisciplinar_certo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void ContinueVisit(View view) {
        Intent intent = new Intent(MainActivity.this, TelaHome.class);
        startActivity(intent);
    }

    public void CriarContaNova(View view) {
        Intent intent = new Intent(MainActivity.this, TelaCadastro1.class);
        startActivity(intent);
    }

    public void IniciarSessao(View view) {
        Intent intent = new Intent(MainActivity.this, TelaLogin1.class);
        startActivity(intent);
    }
}