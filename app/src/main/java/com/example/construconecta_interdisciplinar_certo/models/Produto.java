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
    public Produto(String produtoId, String nomeProduto, String imagem, double preco, double desconto, String descricao, int estoque, boolean condicao, String usuario, int topico, List<Categoria> categorias) {
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

    public void setprodutoId(String produtoId) {
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

    public double getPreco() {
        return preco;
    }

    public void setPreco(double preco) {
        this.preco = preco;
    }

    public double getDesconto() {
        return desconto;
    }

    public void setDesconto(double desconto) {
        this.desconto = desconto;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public int getEstoque() {
        return estoque;
    }

    public void setEstoque(int estoque) {
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

    public int getTopico() {
        return topico;
    }

    public void setTopico(int topico) {
        this.topico = topico;
    }

    public List<Categoria> getCategorias() {
        return categorias;
    }

    public void setCategorias(List<Categoria> categorias) {
        this.categorias = categorias;
    }

    @Override
    public String toString() {
        return "Produto{" +
                "produtoId='" + produtoId + '\'' +
                ", nomeProduto='" + nomeProduto + '\'' +
                ", imagem='" + imagem + '\'' +
                ", preco=" + preco +
                ", desconto=" + desconto +
                ", descricao='" + descricao + '\'' +
                ", estoque=" + estoque +
                ", condicao=" + condicao +
                ", usuario='" + usuario + '\'' +
                ", topico=" + topico +
                ", categorias=" + categorias +
                '}';
    }
}

