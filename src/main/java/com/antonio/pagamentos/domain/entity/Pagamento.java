package com.antonio.pagamentos.domain.entity;

import com.antonio.pagamentos.domain.enums.MetodoPagamento;
import com.antonio.pagamentos.domain.enums.StatusPagamento;
import com.antonio.pagamentos.exception.ExclusaoNaoPermitidaException;
import com.antonio.pagamentos.exception.TransicaoStatusInvalidaException;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "pagamentos")
@Getter
@NoArgsConstructor
public class Pagamento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "cod_debito")
    private Integer codigoDebito;

    @Column(name = "cpf_cnpj_pagador", nullable = false)
    private String cpfCnpjPagador;

    @Enumerated(EnumType.STRING)
    @Column(name = "metodo_pagamento", nullable = false)
    private MetodoPagamento metodoPagamento;

    @Column(name = "numero_cartao")
    private String numeroCartao;

    @Column(name = "valor", nullable = false)
    private BigDecimal valor;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private StatusPagamento status;

    public Pagamento(Integer codigoDebito, String cpfCnpjPagador, MetodoPagamento metodoPagamento, String numeroCartao, BigDecimal valor) {
        this.codigoDebito = codigoDebito;
        this.cpfCnpjPagador = cpfCnpjPagador;
        this.metodoPagamento = metodoPagamento;
        this.numeroCartao = numeroCartao;
        this.valor = valor;
        this.status = StatusPagamento.PENDENTE;
    }

    public void marcarComSucesso() {
        validarTransicaoDePendente("Processado com Sucesso");
        this.status = StatusPagamento.PROCESSADO_SUCESSO;
    }

    public void marcarComFalha() {
        validarTransicaoDePendente("Processado com Falha");
        this.status = StatusPagamento.PROCESSADO_FALHA;
    }

    public void reprocessar() {
        if (this.status != StatusPagamento.PROCESSADO_FALHA) {
            throw new TransicaoStatusInvalidaException(
                    "Apenas pagamentos com falha podem voltar para Pendente."
            );
        }
        this.status = StatusPagamento.PENDENTE;
    }


    public void inativar() {
        if (this.status != StatusPagamento.PENDENTE) {
            throw new ExclusaoNaoPermitidaException(
                    "Apenas pagamentos Pendentes podem ser inativados."
            );
        }
        this.status = StatusPagamento.INATIVO;
    }

    private void validarTransicaoDePendente(String operacao) {
        if (this.status != StatusPagamento.PENDENTE) {
            throw new TransicaoStatusInvalidaException(
                    String.format("Pagamento deve estar Pendente para ser marcado como '%s'. Status atual: %s.",
                            operacao, this.status)
            );
        }
    }
}
