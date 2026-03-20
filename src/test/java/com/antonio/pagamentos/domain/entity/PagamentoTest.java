package com.antonio.pagamentos.domain.entity;

import com.antonio.pagamentos.domain.enums.MetodoPagamento;
import com.antonio.pagamentos.domain.enums.StatusPagamento;
import com.antonio.pagamentos.exception.ExclusaoNaoPermitidaException;
import com.antonio.pagamentos.exception.TransicaoStatusInvalidaException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class PagamentoTest {

    private static final Integer CODIGO_DEBITO = 12345;
    private static final String CPF_VALIDO = "12345678901";
    private static final String NUMERO_CARTAO = "1234567890123456";
    private static final BigDecimal VALOR = new BigDecimal("100.00");

    @Nested
    @DisplayName("Testes de criação de Pagamento")
    class CriacaoPagamento {

        @Test
        @DisplayName("Deve criar pagamento com boleto sem número de cartão")
        void deveCriarPagamentoComBoletoSemCartao() {
            Pagamento pagamento = new Pagamento(
                    CODIGO_DEBITO,
                    CPF_VALIDO,
                    MetodoPagamento.BOLETO,
                    null,
                    VALOR
            );

            assertNotNull(pagamento);
            assertEquals(StatusPagamento.PENDENTE, pagamento.getStatus());
            assertEquals(MetodoPagamento.BOLETO, pagamento.getMetodoPagamento());
            assertNull(pagamento.getNumeroCartao());
        }

        @Test
        @DisplayName("Deve criar pagamento com PIX sem número de cartão")
        void deveCriarPagamentoComPixSemCartao() {
            Pagamento pagamento = new Pagamento(
                    CODIGO_DEBITO,
                    CPF_VALIDO,
                    MetodoPagamento.PIX,
                    null,
                    VALOR
            );

            assertNotNull(pagamento);
            assertEquals(StatusPagamento.PENDENTE, pagamento.getStatus());
            assertEquals(MetodoPagamento.PIX, pagamento.getMetodoPagamento());
        }

        @Test
        @DisplayName("Deve criar pagamento com cartão de crédito e número de cartão")
        void deveCriarPagamentoComCartaoCredito() {
            Pagamento pagamento = new Pagamento(
                    CODIGO_DEBITO,
                    CPF_VALIDO,
                    MetodoPagamento.CARTAO_CREDITO,
                    NUMERO_CARTAO,
                    VALOR
            );

            assertNotNull(pagamento);
            assertEquals(StatusPagamento.PENDENTE, pagamento.getStatus());
            assertEquals(MetodoPagamento.CARTAO_CREDITO, pagamento.getMetodoPagamento());
            assertEquals(NUMERO_CARTAO, pagamento.getNumeroCartao());
        }

        @Test
        @DisplayName("Deve criar pagamento com cartão de débito e número de cartão")
        void deveCriarPagamentoComCartaoDebito() {
            Pagamento pagamento = new Pagamento(
                    CODIGO_DEBITO,
                    CPF_VALIDO,
                    MetodoPagamento.CARTAO_DEBITO,
                    NUMERO_CARTAO,
                    VALOR
            );

            assertNotNull(pagamento);
            assertEquals(StatusPagamento.PENDENTE, pagamento.getStatus());
            assertEquals(MetodoPagamento.CARTAO_DEBITO, pagamento.getMetodoPagamento());
            assertEquals(NUMERO_CARTAO, pagamento.getNumeroCartao());
        }

        @Test
        @DisplayName("Deve lançar exceção ao criar pagamento com cartão de crédito sem número de cartão")
        void deveLancarExcecaoCartaoCreditoSemNumero() {
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> new Pagamento(
                            CODIGO_DEBITO,
                            CPF_VALIDO,
                            MetodoPagamento.CARTAO_CREDITO,
                            null,
                            VALOR
                    )
            );

            assertEquals("Número do cartão é obrigatório para pagamentos com cartão de crédito ou débito.",
                    exception.getMessage());
        }

        @Test
        @DisplayName("Deve lançar exceção ao criar pagamento com cartão de débito sem número de cartão")
        void deveLancarExcecaoCartaoDebitoSemNumero() {
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> new Pagamento(
                            CODIGO_DEBITO,
                            CPF_VALIDO,
                            MetodoPagamento.CARTAO_DEBITO,
                            null,
                            VALOR
                    )
            );

            assertEquals("Número do cartão é obrigatório para pagamentos com cartão de crédito ou débito.",
                    exception.getMessage());
        }

        @Test
        @DisplayName("Deve lançar exceção ao criar pagamento com boleto e número de cartão")
        void deveLancarExcecaoBoletoComCartao() {
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> new Pagamento(
                            CODIGO_DEBITO,
                            CPF_VALIDO,
                            MetodoPagamento.BOLETO,
                            NUMERO_CARTAO,
                            VALOR
                    )
            );

            assertEquals("Número do cartão não deve ser informado para pagamentos com boleto ou PIX.",
                    exception.getMessage());
        }

        @Test
        @DisplayName("Deve lançar exceção ao criar pagamento com PIX e número de cartão")
        void deveLancarExcecaoPixComCartao() {
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> new Pagamento(
                            CODIGO_DEBITO,
                            CPF_VALIDO,
                            MetodoPagamento.PIX,
                            NUMERO_CARTAO,
                            VALOR
                    )
            );

            assertEquals("Número do cartão não deve ser informado para pagamentos com boleto ou PIX.",
                    exception.getMessage());
        }
    }

    @Nested
    @DisplayName("Testes de alteração de status")
    class AlteracaoStatus {

        @Test
        @DisplayName("Deve alterar status de PENDENTE para PROCESSADO_SUCESSO")
        void deveAlterarParaProcessadoSucesso() {
            Pagamento pagamento = criarPagamentoPendente();

            pagamento.alterarStatus(StatusPagamento.PROCESSADO_SUCESSO);

            assertEquals(StatusPagamento.PROCESSADO_SUCESSO, pagamento.getStatus());
        }

        @Test
        @DisplayName("Deve alterar status de PENDENTE para PROCESSADO_FALHA")
        void deveAlterarParaProcessadoFalha() {
            Pagamento pagamento = criarPagamentoPendente();

            pagamento.alterarStatus(StatusPagamento.PROCESSADO_FALHA);

            assertEquals(StatusPagamento.PROCESSADO_FALHA, pagamento.getStatus());
        }

        @Test
        @DisplayName("Deve alterar status de PROCESSADO_FALHA para PENDENTE")
        void deveVoltarParaPendenteApartirDeFalha() {
            Pagamento pagamento = criarPagamentoPendente();
            pagamento.alterarStatus(StatusPagamento.PROCESSADO_FALHA);

            pagamento.alterarStatus(StatusPagamento.PENDENTE);

            assertEquals(StatusPagamento.PENDENTE, pagamento.getStatus());
        }

        @Test
        @DisplayName("Deve lançar exceção ao tentar alterar para PROCESSADO_SUCESSO quando não está PENDENTE")
        void deveLancarExcecaoAlterarSucessoQuandoNaoPendente() {
            Pagamento pagamento = criarPagamentoPendente();
            pagamento.alterarStatus(StatusPagamento.PROCESSADO_SUCESSO);

            TransicaoStatusInvalidaException exception = assertThrows(
                    TransicaoStatusInvalidaException.class,
                    () -> pagamento.alterarStatus(StatusPagamento.PROCESSADO_SUCESSO)
            );

            assertTrue(exception.getMessage().contains("Pagamento deve estar Pendente"));
        }

        @Test
        @DisplayName("Deve lançar exceção ao tentar voltar para PENDENTE quando está com SUCESSO")
        void deveLancarExcecaoVoltarPendenteQuandoSucesso() {
            Pagamento pagamento = criarPagamentoPendente();
            pagamento.alterarStatus(StatusPagamento.PROCESSADO_SUCESSO);

            TransicaoStatusInvalidaException exception = assertThrows(
                    TransicaoStatusInvalidaException.class,
                    () -> pagamento.alterarStatus(StatusPagamento.PENDENTE)
            );

            assertEquals("Apenas pagamentos com falha podem voltar para Pendente.", exception.getMessage());
        }

        @Test
        @DisplayName("Deve lançar exceção ao tentar alterar para status INATIVO via alterarStatus")
        void deveLancarExcecaoAlterarParaInativo() {
            Pagamento pagamento = criarPagamentoPendente();

            TransicaoStatusInvalidaException exception = assertThrows(
                    TransicaoStatusInvalidaException.class,
                    () -> pagamento.alterarStatus(StatusPagamento.INATIVO)
            );

            assertTrue(exception.getMessage().contains("não é permitida via atualização"));
        }
    }

    @Nested
    @DisplayName("Testes de inativação")
    class Inativacao {

        @Test
        @DisplayName("Deve inativar pagamento quando está PENDENTE")
        void deveInativarPagamentoPendente() {
            Pagamento pagamento = criarPagamentoPendente();

            pagamento.inativar();

            assertEquals(StatusPagamento.INATIVO, pagamento.getStatus());
        }

        @Test
        @DisplayName("Deve lançar exceção ao tentar inativar pagamento com PROCESSADO_SUCESSO")
        void deveLancarExcecaoInativarSucesso() {
            Pagamento pagamento = criarPagamentoPendente();
            pagamento.alterarStatus(StatusPagamento.PROCESSADO_SUCESSO);

            ExclusaoNaoPermitidaException exception = assertThrows(
                    ExclusaoNaoPermitidaException.class,
                    () -> pagamento.inativar()
            );

            assertEquals("Apenas pagamentos Pendentes podem ser inativados.", exception.getMessage());
        }

        @Test
        @DisplayName("Deve lançar exceção ao tentar inativar pagamento com PROCESSADO_FALHA")
        void deveLancarExcecaoInativarFalha() {
            Pagamento pagamento = criarPagamentoPendente();
            pagamento.alterarStatus(StatusPagamento.PROCESSADO_FALHA);

            ExclusaoNaoPermitidaException exception = assertThrows(
                    ExclusaoNaoPermitidaException.class,
                    () -> pagamento.inativar()
            );

            assertEquals("Apenas pagamentos Pendentes podem ser inativados.", exception.getMessage());
        }
    }

    private Pagamento criarPagamentoPendente() {
        return new Pagamento(
                CODIGO_DEBITO,
                CPF_VALIDO,
                MetodoPagamento.PIX,
                null,
                VALOR
        );
    }
}

