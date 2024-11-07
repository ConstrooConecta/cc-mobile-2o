package com.example.construconecta_interdisciplinar_certo.models;

import java.math.BigDecimal;

public class Pedido {
    private Integer pedidoId;
    private String usuario;
    private String cupom;
    private BigDecimal valorDesconto;
    private BigDecimal valorTotal;
    private BigDecimal valorFrete;
    private String dataPedido;
    private String dataEntrega;

    public Pedido(
            Integer pedidoId,
            String usuario,
            String cupom,
            BigDecimal valorDesconto,
            BigDecimal valorTotal,
            BigDecimal valorFrete,
            String dataPedido,
            String dataEntrega
    ) {
        this.pedidoId = pedidoId;
        this.usuario = usuario;
        this.cupom = cupom;
        this.valorDesconto = valorDesconto;
        this.valorTotal = valorTotal;
        this.valorFrete = valorFrete;
        this.dataPedido = dataPedido;
        this.dataEntrega = dataEntrega;
    }

    public Integer getPedidoId() {
        return pedidoId;
    }

    public void setPedidoId(Integer pedidoId) {
        this.pedidoId = pedidoId;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getCupom() {
        return cupom;
    }

    public void setCupom(String cupom) {
        this.cupom = cupom;
    }

    public BigDecimal getValorDesconto() {
        return valorDesconto;
    }

    public void setValorDesconto(BigDecimal valorDesconto) {
        this.valorDesconto = valorDesconto;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }

    public BigDecimal getValorFrete() {
        return valorFrete;
    }

    public void setValorFrete(BigDecimal valorFrete) {
        this.valorFrete = valorFrete;
    }

    public String getDataPedido() {
        return dataPedido;
    }

    public void setDataPedido(String dataPedido) {
        this.dataPedido = dataPedido;
    }

    public String getDataEntrega() {
        return dataEntrega;
    }

    public void setDataEntrega(String dataEntrega) {
        this.dataEntrega = dataEntrega;
    }
}
