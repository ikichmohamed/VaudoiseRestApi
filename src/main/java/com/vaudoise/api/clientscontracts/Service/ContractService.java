package com.vaudoise.api.clientscontracts.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.vaudoise.api.clientscontracts.Repository.ClientRepository;
import com.vaudoise.api.clientscontracts.Repository.ContractRepository;
import com.vaudoise.api.clientscontracts.model.Client;
import com.vaudoise.api.clientscontracts.model.Contract;

@Service
public class ContractService {
	private final ContractRepository contractRepository;
	private final ClientRepository clientRepository;
	
	public ContractService(ContractRepository contractRepo, ClientRepository clientRepo) {
		this.clientRepository = clientRepo;
		this.contractRepository = contractRepo;
	}
	
	public Contract createContract(long idClient, Contract contract) {
		// recuperer le client depuis le repo
		Client client = this.clientRepository.findById(idClient).orElseThrow(()-> new RuntimeException("Client not found"));
		
		if (contract.getStartDate()==null) {
			contract.setStartDate(LocalDate.now());
		}
		contract.setClient(client);
		return this.contractRepository.save(contract);
	}
	
	public Contract updateCost(long idContract, double updatedCost) {
		Contract contract = this.contractRepository.findById(idContract).orElseThrow(()-> new RuntimeException("Contract Not found"));
		contract.setCostAmount(updatedCost);
		contract.setLastUpdateDate(LocalDateTime.now());
		return this.contractRepository.save(contract);
	}

}
