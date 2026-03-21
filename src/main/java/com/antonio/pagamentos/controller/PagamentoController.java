package com.antonio.pagamentos.controller;

import com.antonio.pagamentos.domain.enums.StatusPagamento;
import com.antonio.pagamentos.dto.request.AlterarStatusPagamentoRequest;
import com.antonio.pagamentos.dto.request.CriarPagamentoRequest;
import com.antonio.pagamentos.dto.response.ApiResponse;
import com.antonio.pagamentos.dto.response.PagamentoResponse;
import com.antonio.pagamentos.service.PagamentoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Pagamentos", description = "Operacoes de pagamentos")
@RestController
@RequestMapping("/pagamentos")
public class PagamentoController {
    private final PagamentoService service;

    public PagamentoController(PagamentoService service) {
        this.service = service;
    }

    @Operation(summary = "Cria um novo pagamento")
    @PostMapping
    public ResponseEntity<ApiResponse<PagamentoResponse>> criar(@RequestBody @Valid CriarPagamentoRequest request) {
        PagamentoResponse pagamento = service.criar(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.sucesso(pagamento, "Pagamento criado com sucesso"));
    }

    @Operation(summary = "Altera o status de um pagamento")
    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<PagamentoResponse>> alterarStatus(@PathVariable Long id, @RequestBody @Valid AlterarStatusPagamentoRequest request) {
        PagamentoResponse pagamento = service.alterarStatus(id, request);
        return ResponseEntity
                .ok(ApiResponse.sucesso(pagamento, "Status do pagamento alterado com sucesso"));
    }

    @Operation(summary = "Inativa um pagamento")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> inativar(@PathVariable Long id) {
        service.inativar(id);
        return ResponseEntity
                .ok(ApiResponse.sucesso(null, "Pagamento inativado com sucesso"));
    }

    @Operation(summary = "Lista pagamentos com filtros opcionais")
    @GetMapping
    public ResponseEntity<ApiResponse<List<PagamentoResponse>>> listar(
            @RequestParam(required = false) Integer codigoDebito,
            @RequestParam(required = false) String cpfCnpj,
            @RequestParam(required = false) StatusPagamento status) {
        List<PagamentoResponse> pagamentos = service.listar(codigoDebito, cpfCnpj, status);
        return ResponseEntity.ok(ApiResponse.sucesso(pagamentos, "Pagamentos listados com sucesso"));
    }
}
