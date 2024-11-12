package com.example.construconecta_interdisciplinar_certo.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.construconecta_interdisciplinar_certo.AnunciarProdutoActivity;
import com.example.construconecta_interdisciplinar_certo.shop.conta.AnunciarServicoActivity;
import com.example.construconecta_interdisciplinar_certo.shop.conta.EditarDadosPessoaisActivity;
import com.example.construconecta_interdisciplinar_certo.shop.conta.EditarDadosSeguranca;
import com.example.construconecta_interdisciplinar_certo.shop.conta.PlanosActivity;
import com.example.construconecta_interdisciplinar_certo.R;
import com.example.construconecta_interdisciplinar_certo.apis.PagamentoPlanoApi;
import com.example.construconecta_interdisciplinar_certo.apis.UsuarioApi;
import com.example.construconecta_interdisciplinar_certo.databinding.FragmentContaBinding;
import com.example.construconecta_interdisciplinar_certo.models.PagamentoPlano;
import com.example.construconecta_interdisciplinar_certo.models.Usuario;
import com.example.construconecta_interdisciplinar_certo.onboarding.CameraActivity;
import com.example.construconecta_interdisciplinar_certo.repositories.UsuarioRepository;
import com.example.construconecta_interdisciplinar_certo.shop.conta.MeusEnderecosActivity;
import com.example.construconecta_interdisciplinar_certo.shop.conta.MeusPedidosActivity;
import com.example.construconecta_interdisciplinar_certo.shop.conta.PoliticaPrivacidadeActivity;
import com.example.construconecta_interdisciplinar_certo.ui.InternetErrorActivity;
import com.example.construconecta_interdisciplinar_certo.ui.MainActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ContaFragment extends Fragment {
    WebView webView;
    private FragmentContaBinding binding;
    private ProgressBar progressBar;
    private List<View> elementsToHide;
    private boolean isAreaRestritaOpen = false; // Para rastrear o estado da área restrita
    private View viewVender, viewPoliticaPrivacidade;
    private ImageButton botaoEdit;
    private View viewEditarDados;
    private List<PagamentoPlano> pagamentosPlano = new ArrayList<>();
    boolean aBoolean = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentContaBinding.inflate(inflater, container, false);
        return binding.getRoot(); // Retorna a root view através do binding
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (binding == null) {
            return; // Se o binding for nulo, retorna
        }

        viewEditarDados = binding.viewEditarDados;
        botaoEdit = view.findViewById(R.id.botaoEdit);

        viewVender = view.findViewById(R.id.viewVender);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        progressBar = binding.progressBar1;
        viewPoliticaPrivacidade = binding.viewPoliticaPrivacidade;

        ConexaoApiProcurarPorEmail(user);
        ApiPlanos(user.getUid());

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference fotoRef = storageRef.child("galeria/" + user.getEmail() + ".jpg");

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

        viewEditarDados.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), EditarDadosPessoaisActivity.class);
            startActivity(intent);
        });
        binding.viewDados.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), EditarDadosSeguranca.class);
            startActivity(intent);
        });

        binding.viewAssinatura.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), PlanosActivity.class);
            intent.putExtra("premium", aBoolean);
            startActivity(intent);
        });
        binding.textViewAssinaturas.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), PlanosActivity.class);
            intent.putExtra("premium", aBoolean);
            startActivity(intent);
        });

        viewVender.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AnunciarProdutoActivity.class);
            startActivity(intent);
        });
        botaoEdit.setOnClickListener(v -> {
            //abrir activity da camera
            Intent intent = new Intent(getActivity(), CameraActivity.class);
            startActivity(intent);
        });
        viewVender.setOnClickListener(v -> mostrarDialogoSelecao());
        viewPoliticaPrivacidade.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), PoliticaPrivacidadeActivity.class);
            startActivity(intent);
        });
        fotoRef.getDownloadUrl().addOnSuccessListener(uri -> {
            // Verifica se o fragmento está anexado antes de usar o contexto
            if (isAdded() && getContext() != null) {
                Glide.with(getContext())
                        .load(uri)
                        .centerCrop()
                        .circleCrop()
                        .into(binding.imagemPerfil); // Use binding para acessar a ImageView
            }
        }).addOnFailureListener(exception -> {
            // Tratar falha
            Log.e("ContaFragment - fotoref", "Erro ao carregar imagem: " + exception.getMessage());
        });

        binding.viewPoliticaPrivacidade.setOnClickListener(v -> startActivity(new Intent(getActivity(), PoliticaPrivacidadeActivity.class)));
        binding.logoutButton.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(getActivity(), MainActivity.class);
            startActivity(intent);
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
                    binding.textViewApelid.setText("@" + apelido);
                    binding.textViewApelid.setTextColor(Color.parseColor("#000000"));
                    binding.textViewApelid.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                    binding.textViewApelid.setTextSize(24);
                    binding.imagemPerfil.setVisibility(View.VISIBLE);
                    binding.textViewApelid.setVisibility(View.VISIBLE);
                    binding.circuloPerfil.setVisibility(View.VISIBLE);
                    binding.textViewUsuarioNome.setVisibility(View.VISIBLE);
                    binding.botaoEdit.setVisibility(View.VISIBLE);
                    binding.textView25.setVisibility(View.VISIBLE);
                    binding.view9.setVisibility(View.VISIBLE);
                    binding.textViewUsuarioNome.setText(nomeCompleto);
                    binding.textViewUsuarioNome.setTextSize(12);
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<List<Usuario>> call, Throwable t) {
                Log.e("DetalhesProdutosActivity", "Erro na chamada de API: " + t.getMessage());
            }
        });
    }

    private void ApiPlanos(String usuario) {
        String API = "https://cc-api-sql-qa.onrender.com/";
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        PagamentoPlanoApi pagamentoPlanoApi = retrofit.create(PagamentoPlanoApi.class);
        Call<List<PagamentoPlano>> call = pagamentoPlanoApi.findByUserId(usuario);
        call.enqueue(new Callback<List<PagamentoPlano>>() {
            @Override
            public void onResponse(Call<List<PagamentoPlano>> call, Response<List<PagamentoPlano>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    pagamentosPlano.clear();
                    pagamentosPlano.addAll(response.body());

                    if (pagamentosPlano.size() == 2) {
                        if (binding != null && getActivity() != null) { // Verifica se o binding e o contexto não são nulos
                            // Seta o background e atualiza o texto
                            binding.view9.setBackgroundResource(R.drawable.design_plano_premium);
                            binding.textView25.setText("PREMIUM");
                            binding.textView25.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                            aBoolean=true;
                        }
                    }
                } else {
                    if (getActivity() != null) {
                        Toast.makeText(getActivity(), "Erro na resposta da API: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
            @Override
            public void onFailure(Call<List<PagamentoPlano>> call, Throwable throwable) {
                if (getActivity() != null) {
                    Toast.makeText(getActivity(), "Erro ao mostrar produtos relevantes: " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void mostrarDialogoSelecao() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setTitle("Escolha uma opção");

        // Adiciona as opções ao dialog
        String[] opcoes = {"Serviço", "Produto"};
        builder.setItems(opcoes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    // Se escolher "Serviço", abre a AnunciarServicoActivity
                    Intent intent = new Intent(getActivity(), AnunciarServicoActivity.class);
                    startActivity(intent);
                } else if (which == 1) {
                    // Se escolher "Produto", abre a AnunciarProdutoActivity
                    Intent intent = new Intent(getActivity(), AnunciarProdutoActivity.class);
                    startActivity(intent);
                }
            }
        });

        // Mostra o dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Limpa o binding quando a view for destruída
    }

    public void areaRestrita() {
        webView = binding.webViewAeR; // Inicialize a WebView antes de usá-la

        if (isAreaRestritaOpen) {
            // Se a área restrita está aberta, fecha
            webView.setVisibility(View.GONE);
            binding.logoutButton.setVisibility(View.VISIBLE);
            binding.textView25.setVisibility(View.VISIBLE);
            binding.view9.setVisibility(View.VISIBLE);
            binding.btnAreaRestrita.setText("Área Restrita");
            isAreaRestritaOpen = false; // Atualiza o estado
        } else {
            // Se a área restrita está fechada, abre
            webView.setVisibility(View.VISIBLE);
            binding.logoutButton.setVisibility(View.GONE);
            binding.textView25.setVisibility(View.GONE);
            binding.view9.setVisibility(View.GONE);
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
