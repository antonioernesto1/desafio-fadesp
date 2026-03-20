package com.antonio.pagamentos.validation;

import com.antonio.pagamentos.domain.enums.MetodoPagamento;
import com.antonio.pagamentos.dto.request.CriarPagamentoRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValidCartaoValidator implements ConstraintValidator<ValidCartao, CriarPagamentoRequest> {

    @Override
    public boolean isValid(CriarPagamentoRequest request, ConstraintValidatorContext context) {
        if (request.getMetodoPagamento() == null) return true;

        boolean exigeCartao = request.getMetodoPagamento() == MetodoPagamento.CARTAO_CREDITO
                || request.getMetodoPagamento() == MetodoPagamento.CARTAO_DEBITO;
        boolean temCartao = request.getNumeroCartao() != null && !request.getNumeroCartao().isBlank();

        if (exigeCartao && !temCartao) {
            setMensagem(context, "Número do cartão é obrigatório para pagamentos com cartão de crédito ou débito.");
            return false;
        }

        if (!exigeCartao && temCartao) {
            setMensagem(context, "Número do cartão não deve ser informado para pagamentos com boleto ou PIX.");
            return false;
        }

        return true;
    }

    private void setMensagem(ConstraintValidatorContext context, String mensagem) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(mensagem).addConstraintViolation();
    }
}