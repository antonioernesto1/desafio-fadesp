package com.antonio.pagamentos.dto.request;

import com.antonio.pagamentos.domain.enums.MetodoPagamento;
import com.antonio.pagamentos.validation.ValidCartao;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@ValidCartao
public class CriarPagamentoRequest {

    @NotNull(message = "Código do débito é obrigatório.")
    private Integer codigoDebito;

    @NotBlank(message = "CPF/CNPJ é obrigatório.")
    @Pattern(regexp = "\\d{11}|\\d{14}", message = "CPF deve ter 11 dígitos e CNPJ 14 dígitos (apenas números).")
    private String cpfCnpjPagador;

    @NotNull(message = "Método de pagamento é obrigatório.")
    private MetodoPagamento metodoPagamento;

    private String numeroCartao;

    @NotNull(message = "Valor é obrigatório.")
    @Positive(message = "Valor deve ser positivo.")
    private BigDecimal valor;
}