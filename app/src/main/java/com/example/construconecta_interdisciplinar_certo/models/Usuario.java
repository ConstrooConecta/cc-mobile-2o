package com.example.construconecta_interdisciplinar_certo.models;

public class Usuario {
    private String uid;
    private String cpf;
    private String nomeCompleto;
    private String nomeUsuario;
    private String email;
    private String senha;
    private String telefone;
    private Integer genero;
    private String dataNascimento;

    public Usuario(
            String uid,
            String cpf,
            String nomeCompleto,
            String nomeUsuario,
            String email,
            String senha,
            String telefone,
            Integer genero,
            String dataNascimento
    ) {
        this.uid = uid;
        this.cpf = cpf;
        this.nomeCompleto = nomeCompleto;
        this.nomeUsuario = nomeUsuario;
        this.email = email;
        this.senha = senha;
        this.telefone = telefone;
        this.genero = genero;
        this.dataNascimento = dataNascimento;
    }

    public String getUid() { return uid; }
    public String getNomeCompleto() { return nomeCompleto; }
    public String getNomeUsuario() { return nomeUsuario; }
    public String getEmail() { return email; }
}
