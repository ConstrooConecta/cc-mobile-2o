package com.example.construconecta_interdisciplinar_certo.shop;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.construconecta_interdisciplinar_certo.onboarding.CameraActivity;
import com.example.construconecta_interdisciplinar_certo.R;
import com.example.construconecta_interdisciplinar_certo.databinding.ActivityHomeBinding;
import com.example.construconecta_interdisciplinar_certo.fragments.ContaFragment;
import com.example.construconecta_interdisciplinar_certo.fragments.ContratarFragment;
import com.example.construconecta_interdisciplinar_certo.fragments.FavoritesFragment;
import com.example.construconecta_interdisciplinar_certo.fragments.HomeFragment;
import com.example.construconecta_interdisciplinar_certo.ui.BaseActivity;

public class Home extends BaseActivity {
    ActivityHomeBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Recuperando o Bundle
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            int castroConcluido = extras.getInt("castroConcluido", 0); // 0 é o valor padrão
            // Use a variável castroConcluido conforme necessário
            if (castroConcluido == 1) {
                mostrarBoasVindas();
            }
        }


        //Configurando a bottom navBar
        binding.bottomNavigationView.setOnItemSelectedListener(
                item -> {
                    //substituindo os fragments
                    int id = item.getItemId();
                    if (id == R.id.home1) {

                       // item.setIcon(R.drawable.chat_selected_icon);
                        replaceFragment(new HomeFragment());


                    } else if (id == R.id.contratar) {
                       // item.setIcon(R.drawable.home_selected_icon);
                        replaceFragment(new ContratarFragment());
                        Toast.makeText(this, "Contratar", Toast.LENGTH_SHORT).show();

                    } else if (id == R.id.favoritos) {
                      //  item.setIcon(R.drawable.person_selected_icon);
                        replaceFragment(new FavoritesFragment());
                        Toast.makeText(this, "Favoritos", Toast.LENGTH_SHORT).show();
                    }
                    else if (id == R.id.conta) {
                        //item.setIcon(R.drawable.home_selected_icon);
                        replaceFragment(new ContaFragment());
                        Toast.makeText(this, "Conta", Toast.LENGTH_SHORT).show();

                    }
                    return true;
                }
        );
        binding.bottomNavigationView.setSelectedItemId(R.id.home1);
    }

    //método para mudar o fragmento na navbar
    public void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }

    // Criando e definindo contador
    int cont = 0;
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


    private void mostrarBoasVindas() {
        // Criando o AlertDialog
        new AlertDialog.Builder(this)
                .setTitle("Boas-vindas!")
                .setMessage("Cadastro concluído com sucesso! Deseja tirar uma foto de perfil agora?")
                .setPositiveButton("Aceitar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Ação ao aceitar - Redirecionar para a activity de tirar foto
                        Intent intent = new Intent(Home.this, CameraActivity.class);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("Recusar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Ação ao recusar - Fechar o diálogo
                        dialog.dismiss();
                    }
                })
                .setCancelable(false) // Impede que o modal seja fechado clicando fora
                .show();
    }
}