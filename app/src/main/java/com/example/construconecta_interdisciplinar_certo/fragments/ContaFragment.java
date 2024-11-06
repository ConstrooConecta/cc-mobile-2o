package com.example.construconecta_interdisciplinar_certo.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.construconecta_interdisciplinar_certo.shop.conta.AnunciarProdutoActivity;
import com.example.construconecta_interdisciplinar_certo.shop.conta.MeusEnderecosActivity;
import com.example.construconecta_interdisciplinar_certo.shop.conta.MeusPedidosActivity;
import com.example.construconecta_interdisciplinar_certo.shop.conta.PoliticaPrivacidadeActivity;
import com.example.construconecta_interdisciplinar_certo.databinding.FragmentContaBinding;
import com.example.construconecta_interdisciplinar_certo.models.Usuario;
import com.example.construconecta_interdisciplinar_certo.repositories.UsuarioRepository;
import com.example.construconecta_interdisciplinar_certo.ui.InternetErrorActivity;
import com.example.construconecta_interdisciplinar_certo.ui.MainActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class ContaFragment extends Fragment {
    private FragmentContaBinding binding;
    private ProgressBar progressBar;
    private List<View> elementsToHide;
    WebView webView;
    private boolean isAreaRestritaOpen = false; // Para rastrear o estado da área restrita

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentContaBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        progressBar = binding.progressBar1;

        // Define todos os elementos que deseja esconder em uma lista
        elementsToHide = Arrays.asList(
                binding.logoutButton, binding.view8, binding.circuloPerfil, binding.imagemPerfil,
                binding.textViewUsuarioNome, binding.textViewApelid, binding.botaoEdit,
                binding.viewPedido, binding.viewVender, binding.viewFaleConosco, binding.viewDados,
                binding.viewEndereco, binding.viewAssinatura, binding.viewPoliticaPrivacidade,
                binding.viewEditarDados, binding.imageButton2, binding.imageButtonPoliticaPrivacidade,
                binding.imageButtonAssinatura, binding.imageButtonDadoPessoal, binding.imageButtonEndereco,
                binding.imageButtonFaleConsco, binding.imageButtonVender, binding.imageButtonDadoSeguranca,
                binding.textView27, binding.textViewEditarDadosPessoais, binding.textViewPoliticaPrivacidade,
                binding.textViewAssinaturas, binding.textViewMeusEndereco, binding.textViewFaleConosco,
                binding.textViewVender, binding.textViewAlterarDados, binding.btnAreaRestrita
        );

        // Configura os cliques
        binding.viewVender.setOnClickListener(v -> startActivity(new Intent(getActivity(), AnunciarProdutoActivity.class)));
        binding.viewPoliticaPrivacidade.setOnClickListener(v -> startActivity(new Intent(getActivity(), PoliticaPrivacidadeActivity.class)));
        binding.logoutButton.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(getActivity(), MainActivity.class));
            getActivity().finish();
        });
        binding.btnAreaRestrita.setOnClickListener(v -> areaRestrita());

        binding.viewEndereco.setOnClickListener(v -> meusEnderecos());
        binding.textViewMeusEndereco.setOnClickListener(v -> meusEnderecos());
        binding.imageButtonEndereco.setOnClickListener(v -> meusEnderecos());

        binding.viewPedido.setOnClickListener(v -> meusPedidos());
        binding.textView27.setOnClickListener(v -> meusPedidos());
        binding.imageButton2.setOnClickListener(v -> meusPedidos());
    }

    @Override
    public void onResume() {
        super.onResume();

        // Verifica se o binding não é nulo e se o Fragment está anexado
        if (binding == null || !isAdded()) {
            return;
        }

        // Verifica conexão com a internet antes de carregar os dados
        if (!isConnectedToInternet()) {
            startActivity(new Intent(getActivity(), InternetErrorActivity.class));
            return;
        }

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // Torna todos os elementos invisíveis antes de carregar a API
            setVisibility(elementsToHide, View.INVISIBLE);
            progressBar.setVisibility(View.VISIBLE);

            // Carrega dados do usuário
            UsuarioRepository.getInstance().loadData(user.getEmail(), usuarios -> {
                if (!usuarios.isEmpty()) {
                    Usuario usuario = usuarios.get(0);
                    // Verifica se o binding não é nulo antes de usá-lo
                    if (binding != null) {
                        binding.textViewApelid.setText("@" + usuario.getNomeUsuario());
                        binding.textViewApelid.setTextColor(Color.BLACK);
                        binding.textViewApelid.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                        binding.textViewApelid.setTextSize(24);
                        binding.textViewUsuarioNome.setText(usuario.getNomeCompleto());
                        binding.textViewUsuarioNome.setTextSize(12);
                    }

                    // Exibe todos os elementos da lista após o carregamento
                    setVisibility(elementsToHide, View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                }
            });

            // Carrega a imagem do perfil
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference fotoRef = storage.getReference().child("galeria/" + Objects.requireNonNull(user.getEmail()) + ".jpg");
            fotoRef.getDownloadUrl().addOnSuccessListener(uri -> {
                if (isAdded() && getContext() != null) {
                    Glide.with(getContext()).load(uri).centerCrop().circleCrop().into(binding.imagemPerfil);
                }
            });
        }
    }

    private boolean isConnectedToInternet() {
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();
    }

    private void setVisibility(List<View> views, int visibility) {
        for (View view : views) {
            view.setVisibility(visibility);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void areaRestrita() {
        webView = binding.webViewAeR; // Inicialize a WebView antes de usá-la

        if (isAreaRestritaOpen) {
            // Se a área restrita está aberta, fecha
            webView.setVisibility(View.GONE);
            binding.btnAreaRestrita.setText("Área Restrita");
            isAreaRestritaOpen = false; // Atualiza o estado
        } else {
            // Se a área restrita está fechada, abre
            webView.setVisibility(View.VISIBLE);
            binding.logoutButton.setVisibility(View.GONE);
            binding.btnAreaRestrita.setText("Sair da Área Restrita");
            webView.loadUrl("https://area-restria-qa.onrender.com/");
            webView.getSettings().setJavaScriptEnabled(true);
            webView.getSettings().setDomStorageEnabled(true);
            webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);

            webView.setWebViewClient(new WebViewClient() {
                @Override
                public void onPageStarted(WebView view, String url, Bitmap favicon) {
                    super.onPageStarted(view, url, favicon);
                    progressBar.setVisibility(View.VISIBLE);
                }

                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
                    progressBar.setVisibility(View.INVISIBLE);
                }
            });
            isAreaRestritaOpen = true; // Atualiza o estado
        }
    }

    public void meusEnderecos() {
        Intent intent = new Intent(getActivity(), MeusEnderecosActivity.class);
        startActivity(intent);
    }

    public void meusPedidos() {
        Intent intent = new Intent(getActivity(), MeusPedidosActivity.class);
        startActivity(intent);
    }

}
