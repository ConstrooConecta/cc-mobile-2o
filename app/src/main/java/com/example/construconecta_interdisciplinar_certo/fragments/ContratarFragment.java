package com.example.construconecta_interdisciplinar_certo.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.construconecta_interdisciplinar_certo.Adapters.AdapterTagServico;
import com.example.construconecta_interdisciplinar_certo.Adapters.AdapterTagServicoContratar;
import com.example.construconecta_interdisciplinar_certo.Adapters.CardAdapter;
import com.example.construconecta_interdisciplinar_certo.R;
import com.example.construconecta_interdisciplinar_certo.apis.TagServicoApi;
import com.example.construconecta_interdisciplinar_certo.apis.UsuarioApi;
import com.example.construconecta_interdisciplinar_certo.models.CardItem;
import com.example.construconecta_interdisciplinar_certo.models.TagServico;
import com.example.construconecta_interdisciplinar_certo.models.TagServicoCategoria;
import com.example.construconecta_interdisciplinar_certo.models.Usuario;
import com.example.construconecta_interdisciplinar_certo.ui.InternetErrorActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ContratarFragment extends Fragment {
    private RecyclerView recyclerCards;
    private ImageView imagemPerfil;
    private TextView textView40, textView39;
    private List<TagServico> servicoTagList;
    private AdapterTagServico adapterTagServico;
    private RecyclerView recyclerCategoriaSer;
    private AdapterTagServicoContratar adapter;

    public ContratarFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contratar, container, false);

        imagemPerfil = view.findViewById(R.id.imageView13);

        textView40 = view.findViewById(R.id.textView40);
        textView39 = view.findViewById(R.id.textView39);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerServico);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        servicoTagList = new ArrayList<>();
        adapterTagServico = new AdapterTagServico(servicoTagList, getContext());
        recyclerView.setAdapter(adapterTagServico);

        chamarAPIRetrofitServico();

        recyclerCategoriaSer = view.findViewById(R.id.recycler_categoriaSer);
        recyclerCategoriaSer.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        List<TagServicoCategoria> categorias = new ArrayList<>();
        categorias.add(new TagServicoCategoria("Arquiteto", R.drawable.arquiteto));
        categorias.add(new TagServicoCategoria("Engenheiro", R.drawable.engenheiro));
        categorias.add(new TagServicoCategoria("Carpinteiro", R.drawable.carpinteiro));
        categorias.add(new TagServicoCategoria("Pedreiro", R.drawable.pedreiro));
        categorias.add(new TagServicoCategoria("Encanador", R.drawable.encanadorcerto));
        // Adicione outras categorias...

        adapter = new AdapterTagServicoContratar(categorias, getContext());
        recyclerCategoriaSer.setAdapter(adapter);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        ConexaoApiProcurarPorEmail(user);


        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference fotoRef = storageRef.child("galeria/" + user.getEmail() + ".jpg");

        fotoRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // Carrega a imagem com Glide
                Glide.with(getContext())
                        .load(uri)
                        .centerCrop()
                        .circleCrop()
                        .into(imagemPerfil); // Use binding para acessar a ImageView
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Tratar falha
                Toast.makeText(getContext(), "Erro ao carregar imagem.", Toast.LENGTH_SHORT).show();
            }
        });

        recyclerCards = view.findViewById(R.id.recycler_cards);
        recyclerCards.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        List<CardItem> cardItems = new ArrayList<>();
        cardItems.add(new CardItem("Conte o que precisa", "Conte o que precisa, inclua o \n máximo de detalhes, se puder \n inclua fotos para receber \n orçamentos mais precisos."));
        cardItems.add(new CardItem("Receba orçamentos", "Os profissionais Constroo irão \n enviar orçamentos gratuitos. \n Avalie, compare os orçamentos."));
        cardItems.add(new CardItem("Escolha o profissional", "Após o serviço realizado, pague \n pelo aplicativo e parcele em até \n 6x sem juros no cartão de \n crédito."));

        CardAdapter cardAdapter = new CardAdapter(cardItems);
        recyclerCards.setAdapter(cardAdapter);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!isConnectedToInternet()) {
            // Exibir a Activity de erro de internet
            Intent intent = new Intent(getActivity(), InternetErrorActivity.class);
            startActivity(intent);
            // Opcional: finalizar o fragmento atual ou fazer qualquer outra lógica
        }
    }

    private boolean isConnectedToInternet() {
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();
    }

    private void chamarAPIRetrofitServico() {
        String API = "https://cc-api-sql-qa.onrender.com/";
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        TagServicoApi tagServicoApi = retrofit.create(TagServicoApi.class);
        Call<List<TagServico>> call = tagServicoApi.findAllTagServices();

        call.enqueue(new Callback<List<TagServico>>() {
            @Override
            public void onResponse(Call<List<TagServico>> call, Response<List<TagServico>> response) {
                Log.d("API Response", "Código de status: " + response.code());
                Log.d("API Response", "Corpo: " + response.body());

                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        servicoTagList.clear();
                        servicoTagList.addAll(response.body());
                        adapterTagServico.notifyDataSetChanged();
                    } else {
                        Toast.makeText(getActivity(), "A resposta do corpo é nula", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity(), "Erro na resposta da API: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<TagServico>> call, Throwable throwable) {
                Toast.makeText(getActivity(), "Erro ao mostrar serviço: " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void ConexaoApiProcurarPorEmail(FirebaseUser user) {
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

                    textView39.setText("@" + apelido);
                    textView39.setTextColor(Color.parseColor("#797979"));
                    textView40.setVisibility(View.VISIBLE);
                    textView40.setText("Olá, " + nomeCompleto);
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
}
