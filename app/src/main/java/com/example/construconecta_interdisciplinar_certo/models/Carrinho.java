package com.example.construconecta_interdisciplinar_certo.models;

public class Carrinho {
    private int carrinhoId;
    private String usuario;
    private int produto;
    private int quantidade;
    private String produtoImg;
    private double valorTotal;

    public Carrinho(
            int carrinhoId,
            String usuario,
            int produto,
            int quantidade,
            String produtoImg,
            double valorTotal
    ) {
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

    public String getProdutoImg() {
        return produtoImg;
    }

    public double getValorTotal() {
        return valorTotal;
    }
}
