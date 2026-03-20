package com.antonio.pagamentos.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

import java.util.List;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    private final boolean sucesso;
    private final T dados;
    private final String mensagem;
    private final List<String> erros;

    private ApiResponse(boolean sucesso, T dados, String mensagem, List<String> erros) {
        this.sucesso = sucesso;
        this.dados = dados;
        this.mensagem = mensagem;
        this.erros = erros;
    }

    public static <T> ApiResponse<T> sucesso(T dados) {
        return new ApiResponse<>(true, dados, null, null);
    }

    public static <T> ApiResponse<T> sucesso(T dados, String mensagem) {
        return new ApiResponse<>(true, dados, mensagem, null);
    }

    public static <T> ApiResponse<T> erro(String mensagem) {
        return new ApiResponse<>(false, null, mensagem, null);
    }

    public static <T> ApiResponse<T> erro(String mensagem, List<String> erros) {
        return new ApiResponse<>(false, null, mensagem, erros);
    }
}

