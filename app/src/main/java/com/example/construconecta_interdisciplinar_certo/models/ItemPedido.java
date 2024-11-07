package com.example.construconecta_interdisciplinar_certo.models;

public class ItemPedido {
    private Integer itemPedidoId;
    private Integer pedido;
    private Integer produto;
    private Integer quantidade;
    private Double precoUnitario;

    public ItemPedido(
            Integer itemPedidoId,
            Integer pedido,
            Integer produto,
            Integer quantidade,
            Double precoUnitario
    ) {
        this.itemPedidoId = itemPedidoId;
        this.pedido = pedido;
        this.produto = produto;
        this.quantidade = quantidade;
        this.precoUnitario = precoUnitario;
    }

    public Integer getItemPedidoId() {
        return itemPedidoId;
    }

    public void setItemPedidoId(Integer itemPedidoId) {
        this.itemPedidoId = itemPedidoId;
    }

    public Integer getPedido() {
        return pedido;
    }

    public void setPedido(Integer pedido) {
        this.pedido = pedido;
    }

    public Integer getProduto() {
        return produto;
    }

    public void setProduto(Integer produto) {
        this.produto = produto;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

    public Double getPrecoUnitario() {
        return precoUnitario;
    }

    public void setPrecoUnitario(Double precoUnitario) {
        this.precoUnitario = precoUnitario;
    }
}
