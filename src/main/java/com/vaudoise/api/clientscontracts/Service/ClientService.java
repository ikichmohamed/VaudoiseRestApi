package com.vaudoise.api.clientscontracts.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.vaudoise.api.clientscontracts.Repository.ClientRepository;
import com.vaudoise.api.clientscontracts.Repository.ContractRepository;
import com.vaudoise.api.clientscontracts.model.Client;
import com.vaudoise.api.clientscontracts.model.Contract;

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
	
	// creer un client = ajouter dans le repo
	public Client createClient(Client client) {
		return this.clientRepository.save(client);
	}
	
	// update client by id in the repo by the informations containing in the updatedclient object 
	// if the id doesnt exist in the repo an exception occured with a message Client not found
	public Client updateClient(long id, Client updatedClient) {
		return this.clientRepository.findById(id).map(
				client -> {
					client.setName(updatedClient.getName());
					client.setPhone(updatedClient.getPhone());
					client.setEmail(updatedClient.getEmail());
					return this.clientRepository.save(client);
				}
				).orElseThrow(() -> new RuntimeException("Client Not found")) ;
	}
	
	public List<Contract> deleteClient(long id) {
		// extraire les contrats dun client par id
		List<Contract> contracts = this.contractRepository.findByClientIdAndEndDateAfterOrEndDateIsNull(id, LocalDate.now());
		
		// modifie end date de chaque contract
		for (Contract c : contracts) {
			c.setEndDate(LocalDate.now());
		}
		//maJ dans le repo
		List<Contract> savedcontracts = this.contractRepository.saveAll(contracts);
		//suppression du client
		this.clientRepository.deleteById(id);
		return savedcontracts;
	}
	
	public List<Contract> getActiveContracts(long idClient) {
	      List<Contract> activecontracts = this.contractRepository.findByClientIdAndEndDateAfterOrEndDateIsNull(idClient, LocalDate.now());
	      return activecontracts;
	}
	
	public List<Contract> getActiveContractsFilteredByUpdatedDate(long idClient, LocalDate updatedDate) {
		List<Contract> filteredactivecontracts = this.contractRepository.findByClientIdAndUpdateDateAfter(idClient, updatedDate);
		return filteredactivecontracts;
	}
	
	public double getActiveContractsTotal(long idClient) {
		List<Contract> activecontracts = this.contractRepository.findByClientIdAndUpdateDateAfter(idClient, LocalDate.now());
		return activecontracts.stream().mapToDouble(Contract::getCostAmount).sum();
	}
	
	
	

}
