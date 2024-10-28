package com.example.construconecta_interdisciplinar_certo.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.construconecta_interdisciplinar_certo.AnunciarProdutoActivity;
import com.example.construconecta_interdisciplinar_certo.PoliticaPrivacidadeActivity;
import com.example.construconecta_interdisciplinar_certo.R;
import com.example.construconecta_interdisciplinar_certo.apis.UsuarioApi;
import com.example.construconecta_interdisciplinar_certo.databinding.FragmentContaBinding;
import com.example.construconecta_interdisciplinar_certo.models.Usuario;
import com.example.construconecta_interdisciplinar_certo.onboarding.CadastroInfosPessoais3;
import com.example.construconecta_interdisciplinar_certo.onboarding.DatabaseCamera;
import com.example.construconecta_interdisciplinar_certo.ui.InternetErrorActivity;
import com.example.construconecta_interdisciplinar_certo.ui.MainActivity;
import com.example.construconecta_interdisciplinar_certo.utils.AnimationUtils;
import com.example.construconecta_interdisciplinar_certo.utils.ButtonUtils;
import com.example.construconecta_interdisciplinar_certo.utils.InputUtils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ContaFragment extends Fragment {

    private FragmentContaBinding binding;
    private TextView textViewUsuarioNome, textViewApelido;
    private ImageView imagemPerfil;
    private ProgressBar progressBar;
    private View viewVender, viewPoliticaPrivacidade;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentContaBinding.inflate(inflater, container, false);
        return binding.getRoot(); // Retorna a root view através do binding
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewVender = view.findViewById(R.id.viewVender);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        textViewApelido = binding.textViewApelid;
        textViewUsuarioNome = binding.textViewUsuarioNome;
        progressBar = binding.progressBar1;
        viewPoliticaPrivacidade = binding.viewPoliticaPrivacidade;

        Conexao_Api_procurar_por_email(user);

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference fotoRef = storageRef.child("galeria/" + user.getEmail() + ".jpg");


        viewVender.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AnunciarProdutoActivity.class);
            startActivity(intent);
        });

        viewPoliticaPrivacidade.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), PoliticaPrivacidadeActivity.class);
            startActivity(intent);
        });
        fotoRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // Carrega a imagem com Glide
                Glide.with(getContext())
                        .load(uri)
                        .centerCrop()
                        .circleCrop()
                        .into(binding.imagemPerfil); // Use binding para acessar a ImageView
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Tratar falha
                Toast.makeText(getContext(), "Erro ao carregar imagem.", Toast.LENGTH_SHORT).show();
            }
        });

        binding.logoutButton.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(getActivity(), MainActivity.class);
            startActivity(intent);
            getActivity().finish();
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!isConnectedToInternet()) {
            Intent intent = new Intent(getActivity(), InternetErrorActivity.class);
            startActivity(intent);
        }
    }

    private boolean isConnectedToInternet() {
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();
    }

    private void Conexao_Api_procurar_por_email(FirebaseUser user) {
        String url = "https://cc-api-sql-qa.onrender.com/";

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        UsuarioApi usuarioApi = retrofit.create(UsuarioApi.class);
        Call<List<Usuario>> call = usuarioApi.findByEmail(Objects.requireNonNull(user.getEmail()));

        call.enqueue(new Callback<List<Usuario>>() {
            @Override
            public void onResponse(Call<List<Usuario>> call, Response<List<Usuario>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    Usuario usuario = response.body().get(0);

                    String apelido = usuario.getNomeUsuario();
                    String nomeCompleto = usuario.getNomeCompleto();
                    binding.textViewApelid.setText("@"+apelido);
                    binding.textViewApelid.setTextColor(Color.parseColor("#000000"));
                    binding.textViewApelid.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                    binding.textViewApelid.setTextSize(24);

                    binding.imagemPerfil.setVisibility(View.VISIBLE);
                    binding.textViewApelid.setVisibility(View.VISIBLE);
                    binding.circuloPerfil.setVisibility(View.VISIBLE);

                    binding.textViewUsuarioNome.setVisibility(View.VISIBLE);
                    binding.botaoEdit.setVisibility(View.VISIBLE);



                    binding.textViewUsuarioNome.setText(nomeCompleto);
                    binding.textViewUsuarioNome.setTextSize(12);

                    progressBar.setVisibility(View.GONE);


                    Toast.makeText(getContext(), "apelido: " + binding.textViewApelid.getText() + " nome: " + binding.textViewUsuarioNome.getText(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Entrou no else", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Usuario>> call, Throwable t) {
                Toast.makeText(getContext(), "Erro na chamada: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Limpa o binding quando a view for destruída
    }
}
