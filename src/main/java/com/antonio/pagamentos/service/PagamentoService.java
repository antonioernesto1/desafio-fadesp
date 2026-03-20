package com.antonio.pagamentos.service;

import com.antonio.pagamentos.domain.entity.Pagamento;
import com.antonio.pagamentos.domain.enums.StatusPagamento;
import com.antonio.pagamentos.dto.request.AlterarStatusPagamentoRequest;
import com.antonio.pagamentos.dto.request.CriarPagamentoRequest;
import com.antonio.pagamentos.dto.response.PagamentoResponse;
import com.antonio.pagamentos.repository.PagamentoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PagamentoService {
    private final PagamentoRepository pagamentoRepository;

    public PagamentoService(PagamentoRepository pagamentoRepository) {
        this.pagamentoRepository = pagamentoRepository;
    }

    @Transactional
    public PagamentoResponse criar(CriarPagamentoRequest request) {
        Pagamento pagamento = new Pagamento(
                request.getCodigoDebito(),
                request.getCpfCnpjPagador(),
                request.getMetodoPagamento(),
                request.getNumeroCartao(),
                request.getValor()
        );
        return new PagamentoResponse(pagamentoRepository.save(pagamento));
    }

    @Transactional
    public PagamentoResponse alterarStatus(Long id, AlterarStatusPagamentoRequest request) {
        Pagamento pagamento = pagamentoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pagamento não encontrado"));

        pagamento.alterarStatus(request.getStatusPagamento());

        return new PagamentoResponse(pagamentoRepository.save(pagamento));
    }
}
