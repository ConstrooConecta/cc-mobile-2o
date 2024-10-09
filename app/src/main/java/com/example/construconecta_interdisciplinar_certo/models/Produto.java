package com.example.construconecta_interdisciplinar_certo.models;

public class Produto {


    String id;
    String nomeProduto;
    String imagem;
    double preco;
    double desconto;
    String descricao;

    public Produto(String id, String nomeProduto, String imagem, double preco, double desconto, String descricao) {
        this.id = id;
        this.nomeProduto = nomeProduto;
        this.imagem = imagem;
        this.preco = preco;
        this.desconto = desconto;
        this.descricao = descricao;

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNomeProduto() {
        return nomeProduto;
    }

    public void setNome(String nomeProduto) {
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



    @Override
    public String toString() {
        return "Produto{" +
                "id='" + id + '\'' +
                ", nomeProduto='" + nomeProduto + '\'' +
                ", imagem='" + imagem + '\'' +
                ", preco=" + preco +
                ", desconto=" + desconto +
                ", descricao='" + descricao + '\'' +
                '}';
    }
}
