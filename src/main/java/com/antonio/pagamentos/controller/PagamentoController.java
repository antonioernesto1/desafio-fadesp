package com.antonio.pagamentos.controller;

import com.antonio.pagamentos.dto.request.AlterarStatusPagamentoRequest;
import com.antonio.pagamentos.dto.request.CriarPagamentoRequest;
import com.antonio.pagamentos.dto.response.ApiResponse;
import com.antonio.pagamentos.dto.response.PagamentoResponse;
import com.antonio.pagamentos.service.PagamentoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<PagamentoResponse>> alterarStatus(@PathVariable Long id, @RequestBody AlterarStatusPagamentoRequest request) {
        PagamentoResponse pagamento = service.alterarStatus(id, request);
        return ResponseEntity
                .ok(ApiResponse.sucesso(pagamento, "Status do pagamento alterado com sucesso"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> inativar(@PathVariable Long id) {
        service.inativar(id);
        return ResponseEntity
                .ok(ApiResponse.sucesso(null, "Pagamento inativado com sucesso"));
    }
}
