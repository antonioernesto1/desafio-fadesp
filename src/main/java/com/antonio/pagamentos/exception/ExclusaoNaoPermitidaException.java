package com.antonio.pagamentos.exception;

public class ExclusaoNaoPermitidaException extends RuntimeException {
    public ExclusaoNaoPermitidaException(String mensagem) {
        super(mensagem);
    }
}