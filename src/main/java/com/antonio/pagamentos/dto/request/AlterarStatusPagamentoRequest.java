package com.antonio.pagamentos.dto.request;

import com.antonio.pagamentos.domain.enums.StatusPagamento;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class AlterarStatusPagamentoRequest {
    @NotNull
    private StatusPagamento statusPagamento;
}
