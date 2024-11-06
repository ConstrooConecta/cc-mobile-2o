package com.example.construconecta_interdisciplinar_certo.models;

public class PagamentoPlano {
    private int pagamentoPlanoId;
    private int plano;
    private String usuario;
    private double valor;
    private String tipoPagamento;
    private String dataPagamento;

    public PagamentoPlano(int pagamentoPlanoId, int plano, String usuario, double valor, String tipoPagamento, String dataPagamento) {
        this.pagamentoPlanoId = pagamentoPlanoId;
        this.plano = plano;
        this.usuario = usuario;
        this.valor = valor;
        this.tipoPagamento = tipoPagamento;
        this.dataPagamento = dataPagamento;
    }

    public int getPagamentoPlanoId() {
        return pagamentoPlanoId;
    }

    public void setPagamentoPlanoId(int pagamentoPlanoId) {
        this.pagamentoPlanoId = pagamentoPlanoId;
    }

    public int getPlano() {
        return plano;
    }

    public void setPlano(int plano) {
        this.plano = plano;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public String getTipoPagamento() {
        return tipoPagamento;
    }

    public void setTipoPagamento(String tipoPagamento) {
        this.tipoPagamento = tipoPagamento;
    }

    public String getDataPagamento() {
        return dataPagamento;
    }

    public void setDataPagamento(String dataPagamento) {
        this.dataPagamento = dataPagamento;
    }

    @Override
    public String toString() {
        return "PagamentoPlano{" +
                "pagamentoPlanoId=" + pagamentoPlanoId +
                ", plano=" + plano +
                ", usuario='" + usuario + '\'' +
                ", valor=" + valor +
                ", tipoPagamento='" + tipoPagamento + '\'' +
                ", dataPagamento='" + dataPagamento + '\'' +
                '}';
    }
}
