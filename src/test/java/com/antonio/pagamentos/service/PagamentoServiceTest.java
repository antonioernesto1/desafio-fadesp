package com.antonio.pagamentos.service;

import com.antonio.pagamentos.domain.entity.Pagamento;
import com.antonio.pagamentos.domain.enums.MetodoPagamento;
import com.antonio.pagamentos.domain.enums.StatusPagamento;
import com.antonio.pagamentos.dto.request.AlterarStatusPagamentoRequest;
import com.antonio.pagamentos.dto.request.CriarPagamentoRequest;
import com.antonio.pagamentos.dto.response.PagamentoResponse;
import com.antonio.pagamentos.exception.ExclusaoNaoPermitidaException;
import com.antonio.pagamentos.exception.TransicaoStatusInvalidaException;
import com.antonio.pagamentos.repository.PagamentoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PagamentoServiceTest {

    @Mock
    private PagamentoRepository pagamentoRepository;

    @InjectMocks
    private PagamentoService pagamentoService;

    private static final Integer CODIGO_DEBITO = 12345;
    private static final String CPF_VALIDO = "12345678901";
    private static final BigDecimal VALOR = new BigDecimal("100.00");

    @Nested
    @DisplayName("Testes de criação de pagamento")
    class CriarPagamento {

        @Test
        @DisplayName("Deve criar pagamento com sucesso usando PIX")
        void deveCriarPagamentoComPix() {
            CriarPagamentoRequest request = criarRequest(MetodoPagamento.PIX, null);
            Pagamento pagamentoSalvo = criarPagamento(1L, StatusPagamento.PENDENTE);

            when(pagamentoRepository.save(any(Pagamento.class))).thenReturn(pagamentoSalvo);

            PagamentoResponse response = pagamentoService.criar(request);

            assertNotNull(response);
            assertEquals(1L, response.getId());
            assertEquals(StatusPagamento.PENDENTE, response.getStatus());
            verify(pagamentoRepository, times(1)).save(any(Pagamento.class));
        }

        @Test
        @DisplayName("Deve criar pagamento com sucesso usando Boleto")
        void deveCriarPagamentoComBoleto() {
            CriarPagamentoRequest request = criarRequest(MetodoPagamento.BOLETO, null);
            Pagamento pagamentoSalvo = criarPagamento(1L, StatusPagamento.PENDENTE);

            when(pagamentoRepository.save(any(Pagamento.class))).thenReturn(pagamentoSalvo);

            PagamentoResponse response = pagamentoService.criar(request);

            assertNotNull(response);
            assertEquals(StatusPagamento.PENDENTE, response.getStatus());
            verify(pagamentoRepository, times(1)).save(any(Pagamento.class));
        }

        @Test
        @DisplayName("Deve criar pagamento com sucesso usando Cartão de Crédito")
        void deveCriarPagamentoComCartaoCredito() {
            CriarPagamentoRequest request = criarRequest(MetodoPagamento.CARTAO_CREDITO, "1234567890123456");
            Pagamento pagamentoSalvo = criarPagamentoComCartao(1L, StatusPagamento.PENDENTE, "1234567890123456");

            when(pagamentoRepository.save(any(Pagamento.class))).thenReturn(pagamentoSalvo);

            PagamentoResponse response = pagamentoService.criar(request);

            assertNotNull(response);
            assertEquals(StatusPagamento.PENDENTE, response.getStatus());
            assertEquals(MetodoPagamento.CARTAO_CREDITO, response.getMetodoPagamento());
        }
    }

    @Nested
    @DisplayName("Testes de alteração de status")
    class AlterarStatus {

        @Test
        @DisplayName("Deve alterar status para PROCESSADO_SUCESSO")
        void deveAlterarParaProcessadoSucesso() {
            Long id = 1L;
            Pagamento pagamento = criarPagamento(id, StatusPagamento.PENDENTE);
            AlterarStatusPagamentoRequest request = criarAlterarStatusRequest(StatusPagamento.PROCESSADO_SUCESSO);

            when(pagamentoRepository.findById(id)).thenReturn(Optional.of(pagamento));
            when(pagamentoRepository.save(any(Pagamento.class))).thenAnswer(i -> i.getArguments()[0]);

            PagamentoResponse response = pagamentoService.alterarStatus(id, request);

            assertNotNull(response);
            assertEquals(StatusPagamento.PROCESSADO_SUCESSO, response.getStatus());
            verify(pagamentoRepository).findById(id);
            verify(pagamentoRepository).save(pagamento);
        }

        @Test
        @DisplayName("Deve alterar status para PROCESSADO_FALHA")
        void deveAlterarParaProcessadoFalha() {
            Long id = 1L;
            Pagamento pagamento = criarPagamento(id, StatusPagamento.PENDENTE);
            AlterarStatusPagamentoRequest request = criarAlterarStatusRequest(StatusPagamento.PROCESSADO_FALHA);

            when(pagamentoRepository.findById(id)).thenReturn(Optional.of(pagamento));
            when(pagamentoRepository.save(any(Pagamento.class))).thenAnswer(i -> i.getArguments()[0]);

            PagamentoResponse response = pagamentoService.alterarStatus(id, request);

            assertNotNull(response);
            assertEquals(StatusPagamento.PROCESSADO_FALHA, response.getStatus());
        }

        @Test
        @DisplayName("Deve lançar exceção quando pagamento não encontrado")
        void deveLancarExcecaoQuandoPagamentoNaoEncontrado() {
            Long id = 999L;
            AlterarStatusPagamentoRequest request = criarAlterarStatusRequest(StatusPagamento.PROCESSADO_SUCESSO);

            when(pagamentoRepository.findById(id)).thenReturn(Optional.empty());

            EntityNotFoundException exception = assertThrows(
                    EntityNotFoundException.class,
                    () -> pagamentoService.alterarStatus(id, request)
            );

            assertEquals("Pagamento não encontrado", exception.getMessage());
        }

        @Test
        @DisplayName("Deve lançar exceção ao tentar transição inválida de status")
        void deveLancarExcecaoTransicaoInvalida() {
            Long id = 1L;
            Pagamento pagamento = criarPagamento(id, StatusPagamento.PROCESSADO_SUCESSO);
            AlterarStatusPagamentoRequest request = criarAlterarStatusRequest(StatusPagamento.PROCESSADO_FALHA);

            when(pagamentoRepository.findById(id)).thenReturn(Optional.of(pagamento));

            assertThrows(
                    TransicaoStatusInvalidaException.class,
                    () -> pagamentoService.alterarStatus(id, request)
            );
        }
    }

    @Nested
    @DisplayName("Testes de inativação")
    class Inativar {

        @Test
        @DisplayName("Deve inativar pagamento pendente com sucesso")
        void deveInativarPagamentoPendente() {
            Long id = 1L;
            Pagamento pagamento = criarPagamento(id, StatusPagamento.PENDENTE);

            when(pagamentoRepository.findById(id)).thenReturn(Optional.of(pagamento));
            when(pagamentoRepository.save(any(Pagamento.class))).thenReturn(pagamento);

            assertDoesNotThrow(() -> pagamentoService.inativar(id));

            assertEquals(StatusPagamento.INATIVO, pagamento.getStatus());
            verify(pagamentoRepository).save(pagamento);
        }

        @Test
        @DisplayName("Deve lançar exceção ao tentar inativar pagamento não pendente")
        void deveLancarExcecaoInativarNaoPendente() {
            Long id = 1L;
            Pagamento pagamento = criarPagamento(id, StatusPagamento.PROCESSADO_SUCESSO);

            when(pagamentoRepository.findById(id)).thenReturn(Optional.of(pagamento));

            assertThrows(
                    ExclusaoNaoPermitidaException.class,
                    () -> pagamentoService.inativar(id)
            );
        }

        @Test
        @DisplayName("Deve lançar exceção quando pagamento não encontrado para inativação")
        void deveLancarExcecaoQuandoPagamentoNaoEncontrado() {
            Long id = 999L;

            when(pagamentoRepository.findById(id)).thenReturn(Optional.empty());

            EntityNotFoundException exception = assertThrows(
                    EntityNotFoundException.class,
                    () -> pagamentoService.inativar(id)
            );

            assertEquals("Pagamento não encontrado", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("Testes de listagem")
    class Listar {

        @Test
        @DisplayName("Deve listar pagamentos sem filtros")
        void deveListarPagamentosSemFiltros() {
            List<Pagamento> pagamentos = List.of(
                    criarPagamento(1L, StatusPagamento.PENDENTE),
                    criarPagamento(2L, StatusPagamento.PROCESSADO_SUCESSO)
            );

            when(pagamentoRepository.findAll(any(Specification.class))).thenReturn(pagamentos);

            List<PagamentoResponse> resultado = pagamentoService.listar(null, null, null);

            assertNotNull(resultado);
            assertEquals(2, resultado.size());
        }

        @Test
        @DisplayName("Deve listar pagamentos com filtro de código de débito")
        void deveListarComFiltroCodigoDebito() {
            List<Pagamento> pagamentos = List.of(
                    criarPagamento(1L, StatusPagamento.PENDENTE)
            );

            when(pagamentoRepository.findAll(any(Specification.class))).thenReturn(pagamentos);

            List<PagamentoResponse> resultado = pagamentoService.listar(CODIGO_DEBITO, null, null);

            assertNotNull(resultado);
            assertEquals(1, resultado.size());
            verify(pagamentoRepository).findAll(any(Specification.class));
        }

        @Test
        @DisplayName("Deve listar pagamentos com filtro de CPF/CNPJ")
        void deveListarComFiltroCpfCnpj() {
            List<Pagamento> pagamentos = List.of(
                    criarPagamento(1L, StatusPagamento.PENDENTE)
            );

            when(pagamentoRepository.findAll(any(Specification.class))).thenReturn(pagamentos);

            List<PagamentoResponse> resultado = pagamentoService.listar(null, CPF_VALIDO, null);

            assertNotNull(resultado);
            assertEquals(1, resultado.size());
        }

        @Test
        @DisplayName("Deve listar pagamentos com filtro de status")
        void deveListarComFiltroStatus() {
            List<Pagamento> pagamentos = List.of(
                    criarPagamento(1L, StatusPagamento.PENDENTE)
            );

            when(pagamentoRepository.findAll(any(Specification.class))).thenReturn(pagamentos);

            List<PagamentoResponse> resultado = pagamentoService.listar(null, null, StatusPagamento.PENDENTE);

            assertNotNull(resultado);
            assertEquals(1, resultado.size());
        }

        @Test
        @DisplayName("Deve retornar lista vazia quando não há pagamentos")
        void deveRetornarListaVazia() {
            when(pagamentoRepository.findAll(any(Specification.class))).thenReturn(List.of());

            List<PagamentoResponse> resultado = pagamentoService.listar(null, null, null);

            assertNotNull(resultado);
            assertTrue(resultado.isEmpty());
        }
    }

    // Métodos auxiliares

    private CriarPagamentoRequest criarRequest(MetodoPagamento metodo, String numeroCartao) {
        try {
            CriarPagamentoRequest request = new CriarPagamentoRequest();
            setField(request, "codigoDebito", CODIGO_DEBITO);
            setField(request, "cpfCnpjPagador", CPF_VALIDO);
            setField(request, "metodoPagamento", metodo);
            setField(request, "numeroCartao", numeroCartao);
            setField(request, "valor", VALOR);
            return request;
        } catch (Exception e) {
            throw new RuntimeException("Erro ao criar request", e);
        }
    }

    private AlterarStatusPagamentoRequest criarAlterarStatusRequest(StatusPagamento status) {
        try {
            AlterarStatusPagamentoRequest request = new AlterarStatusPagamentoRequest();
            setField(request, "statusPagamento", status);
            return request;
        } catch (Exception e) {
            throw new RuntimeException("Erro ao criar request", e);
        }
    }

    private Pagamento criarPagamento(Long id, StatusPagamento status) {
        Pagamento pagamento = new Pagamento(
                CODIGO_DEBITO,
                CPF_VALIDO,
                MetodoPagamento.PIX,
                null,
                VALOR
        );
        setField(pagamento, "id", id);
        if (status != StatusPagamento.PENDENTE) {
            setField(pagamento, "status", status);
        }
        return pagamento;
    }

    private Pagamento criarPagamentoComCartao(Long id, StatusPagamento status, String numeroCartao) {
        Pagamento pagamento = new Pagamento(
                CODIGO_DEBITO,
                CPF_VALIDO,
                MetodoPagamento.CARTAO_CREDITO,
                numeroCartao,
                VALOR
        );
        setField(pagamento, "id", id);
        if (status != StatusPagamento.PENDENTE) {
            setField(pagamento, "status", status);
        }
        return pagamento;
    }

    private void setField(Object target, String fieldName, Object value) {
        try {
            Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao definir campo " + fieldName, e);
        }
    }
}


