package com.example.construconecta_interdisciplinar_certo.models;

import java.math.BigDecimal;
import java.util.Date;

public class PagamentoProduto {
    private Integer pagamentoProdutoId;
    private Integer pedido;
    private String usuario;
    private String dataPagamento;
    private String tipoPagamento;
    private BigDecimal valorTotal;
    private BigDecimal valorFrete;

    public PagamentoProduto(
            Integer pagamentoProdutoId,
            Integer pedido,
            String usuario,
            String dataPagamento,
            String tipoPagamento,
            BigDecimal valorTotal,
            BigDecimal valorFrete
    ) {
        this.pagamentoProdutoId = pagamentoProdutoId;
        this.pedido = pedido;
        this.usuario = usuario;
        this.dataPagamento = dataPagamento;
        this.tipoPagamento = tipoPagamento;
        this.valorTotal = valorTotal;
        this.valorFrete = valorFrete;
    }

    public Integer getPagamentoProdutoId() {
        return pagamentoProdutoId;
    }

    public void setPagamentoProdutoId(Integer pagamentoProdutoId) {
        this.pagamentoProdutoId = pagamentoProdutoId;
    }

    public Integer getPedido() {
        return pedido;
    }

    public void setPedido(Integer pedido) {
        this.pedido = pedido;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getDataPagamento() {
        return dataPagamento;
    }

    public void setDataPagamento(String dataPagamento) {
        this.dataPagamento = dataPagamento;
    }

    public String getTipoPagamento() {
        return tipoPagamento;
    }

    public void setTipoPagamento(String tipoPagamento) {
        this.tipoPagamento = tipoPagamento;
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
}
