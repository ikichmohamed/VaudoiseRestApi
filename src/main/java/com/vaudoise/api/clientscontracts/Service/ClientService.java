package com.vaudoise.api.clientscontracts.Service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.vaudoise.api.clientscontracts.Repository.ClientRepository;
import com.vaudoise.api.clientscontracts.Repository.ContractRepository;
import com.vaudoise.api.clientscontracts.model.Client;

@Service
public class ClientService {
	
	private final ClientRepository clientRepository;
    private final ContractRepository contractRepository;
	
    // constructor of the service client class
	public ClientService(ClientRepository clientRepo, ContractRepository contractRepo) {
		this.clientRepository = clientRepo;
		this.contractRepository = contractRepo;
	}
	
	// read the client that have an id 
	// this id could be not found in the client repository
	public Optional<Client> getClient(long id) {
		return this.clientRepository.findById(id);
	}
	

}
