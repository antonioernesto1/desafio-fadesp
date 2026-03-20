package com.antonio.pagamentos.exception;

public class TransicaoStatusInvalidaException extends RuntimeException {
    public TransicaoStatusInvalidaException(String mensagem) {
        super(mensagem);
    }
}
