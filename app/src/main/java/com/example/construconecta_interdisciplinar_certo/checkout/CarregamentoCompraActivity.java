package com.example.construconecta_interdisciplinar_certo.checkout;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.example.construconecta_interdisciplinar_certo.R;
import com.example.construconecta_interdisciplinar_certo.apis.CarrinhoApi;
import com.example.construconecta_interdisciplinar_certo.apis.ItemPedidoApi;
import com.example.construconecta_interdisciplinar_certo.apis.PagamentoProdutoApi;
import com.example.construconecta_interdisciplinar_certo.apis.PedidoApi;
import com.example.construconecta_interdisciplinar_certo.apis.ProdutoApi;
import com.example.construconecta_interdisciplinar_certo.models.Carrinho;
import com.example.construconecta_interdisciplinar_certo.models.ItemPedido;
import com.example.construconecta_interdisciplinar_certo.models.PagamentoProduto;
import com.example.construconecta_interdisciplinar_certo.models.Pedido;
import com.example.construconecta_interdisciplinar_certo.models.Produto;
import com.example.construconecta_interdisciplinar_certo.ui.BaseActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CarregamentoCompraActivity extends BaseActivity {
    private ProgressBar progressBar;
    private ImageView imageViewSacola;
    private int progresso = 0;
    private final int progressoEtapas = 25; // 4 etapas, 25% cada uma
    private final Handler handler = new Handler();
    private Double total, desconto;
    private String cupom;
    FirebaseUser usuarioAtual = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carregamento_compra);

        progressBar = findViewById(R.id.progressBar);
        imageViewSacola = findViewById(R.id.imageViewSacola);

        // Inicia o carregamento e as chamadas de API
        startLoading();
    }

    private void startLoading() {
        // Adiciona o pedido e continua com o fluxo de APIs
        addOrder();
    }

    private LocalDate calcularDataEntrega(int diasUteis) {
        LocalDate data = LocalDate.now();
        int contadorDias = 0;

        while (contadorDias < diasUteis) {
            data = data.plusDays(1);

            if (data.getDayOfWeek().getValue() <= 5) { // Ignora finais de semana (sábado=6, domingo=7)
                contadorDias++;
            }
        }

        return data; // Retorna a data de entrega como LocalDate
    }

    public void addOrder() {
        // Configuração e chamada de criação de pedido
        cupom = getIntent().getExtras() != null ? getIntent().getExtras().getString("cupom") : null;
        total = getIntent().getExtras() != null ? getIntent().getExtras().getDouble("total") : null;

        // Obtendo a data atual
        LocalDate agora = LocalDate.now();

        // Calculando a data de entrega
        LocalDate dataEntregaLocal = calcularDataEntrega(15); // Calcula 15 dias úteis a partir de hoje

        // Formatar as datas no formato YYYY-MM-DD
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String dataPedidoFormatada = agora.format(formatter);
        String dataEntregaFormatada = dataEntregaLocal.format(formatter);

        Pedido pedido = new Pedido(
                1,
                usuarioAtual.getUid(),
                cupom,
                BigDecimal.valueOf(0.0),
                BigDecimal.valueOf(total),
                BigDecimal.valueOf(0.0),
                dataPedidoFormatada,
                dataEntregaFormatada
        );

        // Log para verificar a data do pedido
        Log.d("CarregamentoCompra", "Data do Pedido: " + dataPedidoFormatada);

        addOrderToDatabase(pedido);
    }

    private void addOrderToDatabase(Pedido pedido) {
        String url = "https://cc-api-sql-qa.onrender.com/";
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        PedidoApi api = retrofit.create(PedidoApi.class);
        Call<Pedido> call = api.createOrder(pedido);

        call.enqueue(new Callback<Pedido>() {
            @Override
            public void onResponse(Call<Pedido> call, Response<Pedido> response) {
                if (response.isSuccessful()) {
                    updateProgress(); // Atualiza progresso após criar o pedido
                    addOrderItem(); // Próxima etapa
                } else {
                    String errorMessage = "Erro ao cadastrar pedido: " + response.code() + " - " + response.message();
                    Toast.makeText(CarregamentoCompraActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                    Log.e("CarregamentoCompra", errorMessage); // Log do erro
                }
            }

            @Override
            public void onFailure(Call<Pedido> call, Throwable throwable) {
                Toast.makeText(CarregamentoCompraActivity.this, "Erro de comunicação: " + throwable.getMessage(), Toast.LENGTH_LONG).show();
                Log.e("CarregamentoCompra", "Erro de comunicação", throwable);
            }

        });
    }

    public void addOrderItem() {
        assert usuarioAtual != null;
        String userId = usuarioAtual.getUid();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://cc-api-sql-qa.onrender.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        PedidoApi pedidoApi = retrofit.create(PedidoApi.class);
        CarrinhoApi carrinhoApi = retrofit.create(CarrinhoApi.class);
        ProdutoApi produtoApi = retrofit.create(ProdutoApi.class);

        // Passo 1: Buscar o Pedido ID
        pedidoApi.findByUserId(userId).enqueue(new Callback<List<Pedido>>() {
            @Override
            public void onResponse(Call<List<Pedido>> call, Response<List<Pedido>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    Collections.sort(response.body(), (p1, p2) -> p2.getDataEntrega().compareTo(p1.getDataPedido()));
                    Pedido pedido = response.body().get(0); // Obtém o pedido mais recente
                    int pedidoId = pedido.getPedidoId();

                    // Passo 2: Buscar os produtos no carrinho do usuário
                    carrinhoApi.findByUserId(userId).enqueue(new Callback<List<Carrinho>>() {
                        @Override
                        public void onResponse(Call<List<Carrinho>> call, Response<List<Carrinho>> response) {
                            if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                                List<Carrinho> carrinhos = response.body();
                                List<ItemPedido> itensPedido = new ArrayList<>();

                                // Iterar pelos produtos no carrinho
                                for (Carrinho carrinho : carrinhos) {
                                    Integer produtoId = carrinho.getProduto();

                                    // Passo 3: Buscar o preço do produto pelo produtoId
                                    produtoApi.findProductsById(produtoId).enqueue(new Callback<Produto>() {
                                        @Override
                                        public void onResponse(Call<Produto> call, Response<Produto> response) {
                                            if (response.isSuccessful() && response.body() != null) {
                                                Produto produto = response.body();
                                                Double precoUnitario = produto.getPreco();
                                                int quantidade = carrinho.getQuantidade();

                                                ItemPedido itemPedido = new ItemPedido(
                                                        0,
                                                        pedidoId,
                                                        produtoId,
                                                        quantidade,
                                                        precoUnitario
                                                );
                                                itensPedido.add(itemPedido);

                                                // Verifica se todos os itens do carrinho foram processados
                                                if (itensPedido.size() == carrinhos.size()) {
                                                    saveOrderItemsToDatabase(itensPedido);
                                                }
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<Produto> call, Throwable t) {
                                            Toast.makeText(CarregamentoCompraActivity.this, "Erro ao buscar preço do produto", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<List<Carrinho>> call, Throwable t) {
                            Toast.makeText(CarregamentoCompraActivity.this, "Erro ao buscar carrinho do usuário", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
            @Override
            public void onFailure(Call<List<Pedido>> call, Throwable t) {
                Toast.makeText(CarregamentoCompraActivity.this, "Erro ao buscar pedido do usuário", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveOrderItemsToDatabase(List<ItemPedido> itensPedido) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://cc-api-sql-qa.onrender.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ItemPedidoApi itemPedidoApi = retrofit.create(ItemPedidoApi.class);
        int totalItems = itensPedido.size();
        final int[] savedItemsCount = {0};

        for (ItemPedido item : itensPedido) {
            Call<ItemPedido> call = itemPedidoApi.createOrderItem(item);
            call.enqueue(new Callback<ItemPedido>() {
                @Override
                public void onResponse(Call<ItemPedido> call, Response<ItemPedido> response) {
                    if (response.isSuccessful()) {
                        savedItemsCount[0]++;
                        updateProgress(); // Atualiza o progresso após cada item salvo

                        // Verifica se todos os itens foram salvos
                        if (savedItemsCount[0] == totalItems) {
                            updateProductStock(itensPedido); // Chama o método para atualizar o estoque dos produtos
                            addProductPayment(); // Próxima etapa
                        }
                    } else {
                        Toast.makeText(CarregamentoCompraActivity.this, "Erro ao adicionar item ao pedido", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ItemPedido> call, Throwable t) {
                    Toast.makeText(CarregamentoCompraActivity.this, "Falha na comunicação com o servidor", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void addProductPayment() {
        assert usuarioAtual != null;
        String userId = usuarioAtual.getUid();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://cc-api-sql-qa.onrender.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        PedidoApi pedidoApi = retrofit.create(PedidoApi.class);

        // Passo 1: Buscar o pedido criado mais recente para pegar os detalhes do pagamento
        pedidoApi.findByUserId(userId).enqueue(new Callback<List<Pedido>>() {
            @Override
            public void onResponse(Call<List<Pedido>> call, Response<List<Pedido>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    Pedido pedido = response.body().get(0); // Pega o pedido criado mais recentemente
                    int pedidoId = pedido.getPedidoId();
                    BigDecimal valorTotal = pedido.getValorTotal();
                    BigDecimal valorFrete = pedido.getValorFrete();

                    // Obtendo a data atual
                    LocalDate agora = LocalDate.now();


                    // Formatar as datas no formato YYYY-MM-DD
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                    String dataPagamentoFormatada = agora.format(formatter);

                    // Criar o objeto PagamentoProduto com os dados necessários
                    PagamentoProduto pagamentoProduto = new PagamentoProduto(
                            0,
                            pedidoId,
                            userId,
                            dataPagamentoFormatada,
                            "PIX",
                            valorTotal,
                            valorFrete
                    );

                    // Adicionar o pagamento ao banco de dados
                    addProductPaymentToDatabase(pagamentoProduto);
                } else {
                    Toast.makeText(CarregamentoCompraActivity.this, "Erro ao buscar detalhes do pedido", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Pedido>> call, Throwable t) {
                Toast.makeText(CarregamentoCompraActivity.this, "Falha ao buscar pedido do usuário", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addProductPaymentToDatabase(PagamentoProduto pagamentoProduto) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://cc-api-sql-qa.onrender.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        PagamentoProdutoApi pagamentoProdutoApi = retrofit.create(PagamentoProdutoApi.class);

        // Verificação e criação de pagamento
        Call<PagamentoProduto> createCall = pagamentoProdutoApi.createProductPayment(pagamentoProduto);
        createCall.enqueue(new Callback<PagamentoProduto>() {
            @Override
            public void onResponse(Call<PagamentoProduto> call, Response<PagamentoProduto> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(CarregamentoCompraActivity.this, "Pagamento registrado com sucesso", Toast.LENGTH_SHORT).show();
                    updateProgress(); // Atualiza progresso após adicionar o pagamento
                    completeLoading(); // Conclui o carregamento
                } else {
                    Toast.makeText(CarregamentoCompraActivity.this, "Erro ao registrar pagamento", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PagamentoProduto> call, Throwable t) {
                Toast.makeText(CarregamentoCompraActivity.this, "Falha na comunicação com o servidor", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateProductStock(List<ItemPedido> itensPedido) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://cc-api-sql-qa.onrender.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ProdutoApi produtoApi = retrofit.create(ProdutoApi.class);

        for (ItemPedido item : itensPedido) {
            int produtoId = item.getProduto();
            int quantidadeComprada = item.getQuantidade();

            // Passo 1: Buscar o produto pelo ID
            produtoApi.findProductsById(produtoId).enqueue(new Callback<Produto>() {
                @Override
                public void onResponse(Call<Produto> call, Response<Produto> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Produto produto = response.body();
                        int estoqueAtual = produto.getEstoque();
                        int novoEstoque = estoqueAtual - quantidadeComprada;

                        // Se o novo estoque for zero ou menor, exclui o produto
                        if (novoEstoque <= 0) {
                            produtoApi.deleteProduct(produtoId).enqueue(new Callback<Void>() {
                                @Override
                                public void onResponse(Call<Void> call, Response<Void> response) {
                                    if (response.isSuccessful()) {
                                        Log.d("CarregamentoCompra", "Produto excluído: ID " + produtoId);
                                    } else {
                                        Log.e("CarregamentoCompra", "Erro ao excluir produto");
                                    }
                                }

                                @Override
                                public void onFailure(Call<Void> call, Throwable t) {
                                    Log.e("CarregamentoCompra", "Falha ao excluir produto", t);
                                }
                            });
                        } else {
                            // Atualiza o estoque do produto com o corpo correto
                            Map<String, Integer> estoqueMap = new HashMap<>();
                            estoqueMap.put("estoque", novoEstoque);

                            produtoApi.updateProduct(produtoId, estoqueMap).enqueue(new Callback<Void>() {
                                @Override
                                public void onResponse(Call<Void> call, Response<Void> response) {
                                    if (response.isSuccessful()) {
                                        Log.d("CarregamentoCompra", "Estoque atualizado para o produto ID " + produtoId);
                                    } else {
                                        Log.e("CarregamentoCompra", "Erro ao atualizar estoque");
                                    }
                                }

                                @Override
                                public void onFailure(Call<Void> call, Throwable t) {
                                    Log.e("CarregamentoCompra", "Falha ao atualizar estoque", t);
                                }
                            });
                        }
                    }
                }

                @Override
                public void onFailure(Call<Produto> call, Throwable t) {
                    Log.e("CarregamentoCompra", "Erro ao buscar produto", t);
                }
            });
        }
    }

    private void excluirCarrinho(String userId) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://cc-api-sql-qa.onrender.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        CarrinhoApi carrinhoApi = retrofit.create(CarrinhoApi.class);
        carrinhoApi.deleteCarrinhoByUserId(userId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d("CarregamentoCompra", "Carrinho excluído para o usuário ID " + userId);
                } else {
                    Log.e("CarregamentoCompra", "Erro ao excluir carrinho");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("CarregamentoCompra", "Falha ao excluir carrinho", t);
            }
        });
    }

    private void updateProgress() {
        progresso += progressoEtapas;
        ObjectAnimator.ofInt(progressBar, "progress", progresso).setDuration(300).start();
    }

    private void completeLoading() {
        excluirCarrinho(usuarioAtual.getUid()); // Exclui o carrinho após concluir a compra
        handler.postDelayed(() -> {
            progressBar.setProgress(100); // Conclui a barra de progresso
            imageViewSacola.setImageResource(R.drawable.compracarregada);
            Drawable progressDrawable = progressBar.getProgressDrawable().mutate();
            progressDrawable.setColorFilter(
                    ContextCompat.getColor(CarregamentoCompraActivity.this, R.color.verde),
                    android.graphics.PorterDuff.Mode.SRC_IN);

            // Navega para a próxima página
            startActivity(new Intent(CarregamentoCompraActivity.this, PedidoConcluidoActivity.class));
            finishAffinity(); // Encerra todas as activities
        }, 500); // Pequeno delay para transição
    }
}
