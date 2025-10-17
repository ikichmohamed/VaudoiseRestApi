package com.vaudoise.api.clientscontracts.Controllers;

import java.net.URI;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vaudoise.api.clientscontracts.Service.ClientService;
import com.vaudoise.api.clientscontracts.dto.ClientDto;
import com.vaudoise.api.clientscontracts.dto.ContractDto;
import com.vaudoise.api.clientscontracts.model.Client;
import com.vaudoise.api.clientscontracts.model.Company;
import com.vaudoise.api.clientscontracts.model.Contract;
import com.vaudoise.api.clientscontracts.model.Person;

import jakarta.persistence.EntityNotFoundException;


@RestController
@RequestMapping("/api/clients")
public class ClientController {
	
	private final ClientService clientService;
    private final ModelMapper modelMapper;

    public ClientController(ClientService clientService, ModelMapper modelMapper) {
        this.clientService = clientService;
        this.modelMapper = modelMapper;
    }
    
    @PostMapping
    public ResponseEntity<ClientDto> createClient(@RequestBody ClientDto clientDto) {
    	Client client;

        if ("PERSON".equalsIgnoreCase(clientDto.getClientType())) {
            client = modelMapper.map(clientDto, Person.class);
        } else if ("COMPANY".equalsIgnoreCase(clientDto.getClientType())) {
            client = modelMapper.map(clientDto, Company.class);
        } else {
            return ResponseEntity.badRequest().build();
        }
        
        Client savedClient = clientService.createClient(client);
        ClientDto responseDto = modelMapper.map(savedClient, ClientDto.class);
        
     // ✅ retourne 201 Created + en-tête Location
        return ResponseEntity
                .created(URI.create("/api/clients/" + responseDto.getId()))
                .body(responseDto);

    	
    }
    
    @GetMapping(value = "/{id}")
    public ResponseEntity<ClientDto> getClient(@PathVariable("id") String idClient) {
    	Optional<Client> clientOpt = clientService.getClient(Integer.parseInt(idClient));

        if (clientOpt.isEmpty()) {
            return ResponseEntity.notFound().build(); // ✅ 404 Not Found
        }

        ClientDto responseDto = modelMapper.map(clientOpt.get(), ClientDto.class);
        return ResponseEntity.ok(responseDto); // ✅ 200 OK si trouvé
    }
    
 // =========================================================
    // ✅ PUT : Mettre à jour un client
    // =========================================================
    @PutMapping("/{id}")
    public ResponseEntity<?> updateClient(@PathVariable("id") long idClient, @RequestBody ClientDto clientDto) {
    	Client client;

        if ("PERSON".equalsIgnoreCase(clientDto.getClientType())) {
            client = modelMapper.map(clientDto, Person.class);
        } else if ("COMPANY".equalsIgnoreCase(clientDto.getClientType())) {
            client = modelMapper.map(clientDto, Company.class);
        } else {
            return ResponseEntity.badRequest().build();
        }

        try {
            Client updatedClient = clientService.updateClient(idClient, client);
            ClientDto responseDto = modelMapper.map(updatedClient, ClientDto.class);
            return ResponseEntity.ok(responseDto);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found") || e.getMessage().contains("Not found")) {
                return ResponseEntity.status(404).body(e.getMessage());
            }
            return ResponseEntity.internalServerError().body(e.getMessage());
        }

    }
    
 // 🔥 DELETE client
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteClient(@PathVariable("id") long id) {
    	try {
            List<Contract> updatedContracts = clientService.deleteClient(id);
            return ResponseEntity.ok(Map.of(
                    "message", "Client deleted successfully",
                    "contractsUpdated", updatedContracts.size()
            ));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Client not found"));
        } catch (Exception e) {
            // Autre erreur interne
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Internal error: Unexpected error"));
        }
    }
    
 // =========================================================
    // ✅ GET : Récupérer les contrats actifs d’un client
    // =========================================================
    @GetMapping("/{id}/contracts/active")
    public ResponseEntity<?> getActiveContracts(@PathVariable("id") long idClient) {
    	try {
            if (clientService.getClient(idClient).isEmpty()) {
                return ResponseEntity.status(404).body("Client not found");
            }

            List<Contract> activeContracts = clientService.getActiveContracts(idClient);

            if (activeContracts.isEmpty()) {
                return ResponseEntity.ok(Collections.emptyList());
            }

            return ResponseEntity.ok(activeContracts);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Internal server error: " + e.getMessage());
        }
    }
    
    @GetMapping("/{id}/contracts/filteredactive")
    public ResponseEntity<?> getActiveContractsFilteredByUpdatedDate(
            @PathVariable long id,
            @RequestParam("updatedDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate updatedDate) {

        try {
            List<Contract> contracts = clientService.getActiveContractsFilteredByUpdatedDate(id, updatedDate);
            return ResponseEntity.ok(contracts);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Invalid parameters"));
        }
    }
    
    @GetMapping("/{id}/contracts/active/total")
    public ResponseEntity<?> getActiveContractsTotal(
            @PathVariable long id) {

        try {
            var optClient = clientService.getClient(id);
            
            if (optClient.isEmpty()) {
            	return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("message", "Client not found"));
            }
            double TotalCost = clientService.getActiveContractsTotal(id);
            
            return ResponseEntity.ok(Map.of("clientId", id,
                    "totalActiveContractsCost", TotalCost));
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Invalid parameters"));
        }
    }



}
