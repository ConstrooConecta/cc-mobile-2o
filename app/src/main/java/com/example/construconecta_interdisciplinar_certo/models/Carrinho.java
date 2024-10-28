package com.example.construconecta_interdisciplinar_certo.models;

public class Carrinho {
    // "carrinhoId": 0,
    // "identificador": 0,
    //  "usuario": "string",
    //  "produto": 0,
    //  "quantidade": 0,
    //  "produtoImg": "string",
    //  "valorTotal": 0

    private int carrinhoId;
    private String usuario;
    private int produto;
    private int quantidade;
    private String produtoImg;
    private double valorTotal;

    public Carrinho(int carrinhoId, String usuario, int produto, int quantidade, String produtoImg, double valorTotal) {
        this.carrinhoId = carrinhoId;
        this.usuario = usuario;
        this.produto = produto;
        this.quantidade = quantidade;
        this.produtoImg = produtoImg;
        this.valorTotal = valorTotal;
    }

    public int getCarrinhoId() {
        return carrinhoId;
    }

    public void setCarrinhoId(int carrinhoId) {
        this.carrinhoId = carrinhoId;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public int getProduto() {
        return produto;
    }

    public void setProduto(int produto) {
        this.produto = produto;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }

    public String getProdutoImg() {
        return produtoImg;
    }

    public void setProdutoImg(String produtoImg) {
        this.produtoImg = produtoImg;
    }

    public double getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(double valorTotal) {
        this.valorTotal = valorTotal;
    }

    @Override
    public String toString() {
        return "Carrinho{" +
                "carrinhoId=" + carrinhoId +
                ", usuario='" + usuario + '\'' +
                ", produto=" + produto +
                ", quantidade=" + quantidade +
                ", produtoImg='" + produtoImg + '\'' +
                ", valorTotal=" + valorTotal +
                '}';
    }
}
