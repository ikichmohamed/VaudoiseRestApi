package com.vaudoise.api.clientscontracts.Repository;

import com.vaudoise.api.clientscontracts.model.Contract;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ContractRepository extends JpaRepository<Contract, Long> {
    List<Contract> findByClientIdAndEndDateAfterOrEndDateIsNull(Long clientId, LocalDate currentDate);
    List<Contract> findByClientIdAndUpdateDateAfter(Long clientId, LocalDate updateDate);
}
