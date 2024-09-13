package com.example.construconecta_interdisciplinar_certo;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.construconecta_interdisciplinar_certo.databinding.ActivityMainBinding;
import com.example.construconecta_interdisciplinar_certo.databinding.ActivityTelaHomeBinding;

public class TelaHome extends AppCompatActivity {
    ActivityTelaHomeBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTelaHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());



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

                    } else if (id == R.id.notificacoes) {
                      //  item.setIcon(R.drawable.person_selected_icon);
                        replaceFragment(new NotificationFragment());
                        Toast.makeText(this, "Notificações", Toast.LENGTH_SHORT).show();
                    }
                    else if (id == R.id.conta) {
                        //item.setIcon(R.drawable.home_selected_icon);
                        replaceFragment(new ContaFragment());
                        Toast.makeText(this, "Conta", Toast.LENGTH_SHORT).show();

                    }
                    return true;
                }
        );
    }

    //método para mudar o fragmento na navbar
    public void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();

    }
}