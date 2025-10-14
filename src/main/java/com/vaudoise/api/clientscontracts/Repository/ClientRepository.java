package com.vaudoise.api.clientscontracts.Repository;

import com.vaudoise.api.clientscontracts.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
}
