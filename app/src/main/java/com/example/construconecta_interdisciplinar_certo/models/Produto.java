package com.example.construconecta_interdisciplinar_certo.models;

import java.util.List;

public class Produto {
    String produtoId;
    String nomeProduto;
    String imagem;
    Double preco;
    Double desconto;
    String descricao;
    Integer estoque;
    boolean condicao;
    String usuario;
    Integer topico;
    List<Categoria> categorias;

    // Construtor
    public Produto(
            String produtoId,
            String nomeProduto,
            String imagem,
            Double preco,
            Double desconto,
            String descricao,
            Integer estoque,
            boolean condicao,
            String usuario,
            Integer topico,
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

    public String getProdutoId() {
        return produtoId;
    }

    public void setProdutoId(String produtoId) {
        this.produtoId = produtoId;
    }

    public String getNomeProduto() {
        return nomeProduto;
    }

    public void setNomeProduto(String nomeProduto) {
        this.nomeProduto = nomeProduto;
    }

    public String getImagem() {
        return imagem;
    }

    public void setImagem(String imagem) {
        this.imagem = imagem;
    }

    public Double getPreco() {
        return preco;
    }

    public void setPreco(Double preco) {
        this.preco = preco;
    }

    public Double getDesconto() {
        return desconto;
    }

    public void setDesconto(Double desconto) {
        this.desconto = desconto;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Integer getEstoque() {
        return estoque;
    }

    public void setEstoque(Integer estoque) {
        this.estoque = estoque;
    }

    public boolean isCondicao() {
        return condicao;
    }

    public void setCondicao(boolean condicao) {
        this.condicao = condicao;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public Integer getTopico() {
        return topico;
    }

    public void setTopico(Integer topico) {
        this.topico = topico;
    }

    public List<Categoria> getCategorias() {
        return categorias;
    }

    public void setCategorias(List<Categoria> categorias) {
        this.categorias = categorias;
    }
}
