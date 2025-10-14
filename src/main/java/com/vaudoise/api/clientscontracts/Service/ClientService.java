package com.vaudoise.api.clientscontracts.Service;

import org.springframework.stereotype.Service;

import com.vaudoise.api.clientscontracts.Repository.ClientRepository;
import com.vaudoise.api.clientscontracts.Repository.ContractRepository;

@Service
public class ClientService {
	
	private final ClientRepository clientRepository;
    private final ContractRepository contractRepository;
	
	public ClientService(ClientRepository clientRepo, ContractRepository contractRepo) {
		this.clientRepository = clientRepo;
		this.contractRepository = contractRepo;
	}

}
