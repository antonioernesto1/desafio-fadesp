package com.antonio.pagamentos.repository;


import com.antonio.pagamentos.domain.entity.Pagamento;
import com.antonio.pagamentos.domain.enums.StatusPagamento;
import org.springframework.data.jpa.domain.Specification;

public class PagamentoSpecification {

    private PagamentoSpecification() {}

    public static Specification<Pagamento> porCodigoDebito(Integer codigoDebito) {
        return (root, query, cb) ->
                codigoDebito == null ? null : cb.equal(root.get("codigoDebito"), codigoDebito);
    }

    public static Specification<Pagamento> porCpfCnpj(String cpfCnpj) {
        return (root, query, cb) ->
                cpfCnpj == null ? null : cb.equal(root.get("cpfCnpj"), cpfCnpj);
    }

    public static Specification<Pagamento> porStatus(StatusPagamento status) {
        return (root, query, cb) ->
                status == null ? null : cb.equal(root.get("status"), status);
    }
}
