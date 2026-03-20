package com.antonio.pagamentos.repository;

import com.antonio.pagamentos.domain.entity.Pagamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface PagamentoRepository extends JpaRepository<Pagamento, Long>,
        JpaSpecificationExecutor<Pagamento> {
}
