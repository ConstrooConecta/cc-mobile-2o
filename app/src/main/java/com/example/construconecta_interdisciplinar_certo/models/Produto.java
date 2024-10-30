package com.example.construconecta_interdisciplinar_certo.models;

import java.util.List;

public class Produto {
    String produtoId;
    String nomeProduto;
    String imagem;
    double preco;
    double desconto;
    String descricao;
    int estoque;
    boolean condicao;
    String usuario;
    int topico;
    List<Categoria> categorias;  // Para as categorias

    // Construtor
    public Produto(
            String produtoId,
            String nomeProduto,
            String imagem,
            double preco,
            double desconto,
            String descricao,
            int estoque,
            boolean condicao,
            String usuario,
            int topico,
            List<Categoria> categorias
    ) {
        this.produtoId = produtoId;
        this.nomeProduto = nomeProduto;
        this.imagem = imagem;
        this.preco = preco;
        this.desconto = desconto;
        this.descricao = descricao;
        this.estoque = estoque;
        this.condicao = condicao;
        this.usuario = usuario;
        this.topico = topico;
        this.categorias = categorias;
    }

    // Getters e Setters
    public String getprodutoId() {
        return produtoId;
    }

    public String getNomeProduto() {
        return nomeProduto;
    }

    public String getImagem() {
        return imagem;
    }

    public void setImagem(String imagem) {
        this.imagem = imagem;
    }

    public double getPreco() {
        return preco;
    }

    public double getDesconto() {
        return desconto;
    }

    public String getDescricao() {
        return descricao;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }
}
