package com.antonio.pagamentos.dto.response;

import com.antonio.pagamentos.domain.entity.Pagamento;
import com.antonio.pagamentos.domain.enums.MetodoPagamento;
import com.antonio.pagamentos.domain.enums.StatusPagamento;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class PagamentoResponse {

    private final Long id;
    private final Integer codigoDebito;
    private final String cpfCnpj;
    private final MetodoPagamento metodoPagamento;
    private final String numeroCartao;
    private final BigDecimal valor;
    private final StatusPagamento status;

    public PagamentoResponse(Pagamento pagamento) {
        this.id = pagamento.getId();
        this.codigoDebito = pagamento.getCodigoDebito();
        this.cpfCnpj = pagamento.getCpfCnpjPagador();
        this.metodoPagamento = pagamento.getMetodoPagamento();
        this.numeroCartao = pagamento.getNumeroCartao();
        this.valor = pagamento.getValor();
        this.status = pagamento.getStatus();
    }
}
