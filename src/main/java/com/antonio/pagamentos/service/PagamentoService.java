package com.antonio.pagamentos.service;

import com.antonio.pagamentos.domain.entity.Pagamento;
import com.antonio.pagamentos.dto.request.CriarPagamentoRequest;
import com.antonio.pagamentos.dto.response.PagamentoResponse;
import com.antonio.pagamentos.repository.PagamentoRepository;
import org.springframework.stereotype.Service;

@Service
public class PagamentoService {
    private final PagamentoRepository pagamentoRepository;

    public PagamentoService(PagamentoRepository pagamentoRepository) {
        this.pagamentoRepository = pagamentoRepository;
    }
    
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
}
