package com.example.construconecta_interdisciplinar_certo.models;

public class Carrinho {
    private Integer carrinhoId;
    private String usuario;
    private Integer produto;
    private Integer quantidade;
    private String produtoImg;
    private double valorTotal;

    public Carrinho(
            Integer carrinhoId,
            String usuario,
            Integer produto,
            Integer quantidade,
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

    public Integer getCarrinhoId() {
        return carrinhoId;
    }

    public void setCarrinhoId(Integer carrinhoId) {
        this.carrinhoId = carrinhoId;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public Integer getProduto() {
        return produto;
    }

    public void setProduto(Integer produto) {
        this.produto = produto;
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

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }
}
