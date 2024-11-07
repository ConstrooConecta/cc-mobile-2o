package com.example.construconecta_interdisciplinar_certo.models;

public class Desconto {
    private String id;
    private String cupom;
    private Double valorDesconto;

    public Desconto(
            String id,
            String cupom,
            Double valorDesconto
    ) {
        this.id = id;
        this.cupom = cupom;
        this.valorDesconto = valorDesconto;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCupom() {
        return cupom;
    }

    public void setCupom(String cupom) {
        this.cupom = cupom;
    }

    public Double getValorDesconto() {
        return valorDesconto;
    }

    public void setValorDesconto(Double valorDesconto) {
        this.valorDesconto = valorDesconto;
    }
}
