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
        validarNumeroCartao(metodoPagamento, numeroCartao);
        
        this.codigoDebito = codigoDebito;
        this.cpfCnpjPagador = cpfCnpjPagador;
        this.metodoPagamento = metodoPagamento;
        this.numeroCartao = numeroCartao;
        this.valor = valor;
        this.status = StatusPagamento.PENDENTE;
    }

    private void validarNumeroCartao(MetodoPagamento metodo, String numeroCartao) {
        boolean temCartao = numeroCartao != null && !numeroCartao.isBlank();
        
        if (exigeCartao(metodo) && !temCartao) {
            throw new IllegalArgumentException("Número do cartão é obrigatório para pagamentos com cartão de crédito ou débito.");
        }
        
        if (!exigeCartao(metodo) && temCartao) {
            throw new IllegalArgumentException("Número do cartão não deve ser informado para pagamentos com boleto ou PIX.");
        }
    }

    private boolean exigeCartao(MetodoPagamento metodo) {
        return metodo == MetodoPagamento.CARTAO_CREDITO || metodo == MetodoPagamento.CARTAO_DEBITO;
    }

    public void alterarStatus(StatusPagamento novoStatus){
        switch (novoStatus){
            case PROCESSADO_SUCESSO -> marcarComSucesso();
            case PROCESSADO_FALHA -> marcarComFalha();
            case PENDENTE -> retornarParaPendente();
            default -> throw new TransicaoStatusInvalidaException(
                    "Transição para o status '" + novoStatus + "' não é permitida via atualização."
            );
        }
    }

    private void marcarComSucesso() {
        validarTransicaoDePendente("Processado com Sucesso");
        this.status = StatusPagamento.PROCESSADO_SUCESSO;
    }

    private void marcarComFalha() {
        validarTransicaoDePendente("Processado com Falha");
        this.status = StatusPagamento.PROCESSADO_FALHA;
    }

    private void retornarParaPendente() {
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
