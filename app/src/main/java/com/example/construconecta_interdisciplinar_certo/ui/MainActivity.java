package com.example.construconecta_interdisciplinar_certo.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import com.example.construconecta_interdisciplinar_certo.Home;
import com.example.construconecta_interdisciplinar_certo.R;
import com.example.construconecta_interdisciplinar_certo.onboarding.CadastroEmail1;
import com.example.construconecta_interdisciplinar_certo.onboarding.Login;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends BaseActivity {

    // Criando e definindo contador
    int cont = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Verifica se o usuário está logado
        if(FirebaseAuth.getInstance().getCurrentUser() != null) {
            Intent intent = new Intent(MainActivity.this, Home.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }
    }

    public void signUp(View view) {
        Intent intent = new Intent(MainActivity.this, CadastroEmail1.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    public void signIn(View view) {
        Intent intent = new Intent(MainActivity.this, Login.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // Verifica se o botão pressionado é o botão de "voltar"
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            cont++;
            if (cont == 1) {
                // Mostra o Toast pedindo para o usuário clicar novamente para sair
                Toast.makeText(this, "Aperte novamente para sair", Toast.LENGTH_SHORT).show();

                // Reseta o contador após 2 segundos
                new Handler().postDelayed(() -> cont = 0, 2000);
                return true;
            } else if (cont == 2) {
                // Se o usuário apertou novamente dentro do tempo, encerra a Activity
                return super.onKeyDown(keyCode, event);
            }
        }
        return true;
    }
}
