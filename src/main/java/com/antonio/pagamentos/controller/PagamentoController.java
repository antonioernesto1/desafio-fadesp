package com.antonio.pagamentos.controller;

import com.antonio.pagamentos.dto.request.CriarPagamentoRequest;
import com.antonio.pagamentos.dto.response.ApiResponse;
import com.antonio.pagamentos.dto.response.PagamentoResponse;
import com.antonio.pagamentos.service.PagamentoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/pagamentos")
public class PagamentoController {
    private final PagamentoService service;

    public PagamentoController(PagamentoService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<PagamentoResponse>> criar(@RequestBody @Valid CriarPagamentoRequest request) {
        PagamentoResponse pagamento = service.criar(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.sucesso(pagamento, "Pagamento criado com sucesso"));
    }
}
