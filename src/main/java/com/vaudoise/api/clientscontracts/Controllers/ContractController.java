package com.vaudoise.api.clientscontracts.Controllers;

import java.util.Collections;

import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vaudoise.api.clientscontracts.Service.ContractService;
import com.vaudoise.api.clientscontracts.dto.ContractDto;
import com.vaudoise.api.clientscontracts.model.Contract;

@RestController
@RequestMapping("/api/contracts")
public class ContractController {
	
	private final ContractService contractService;
    private final ModelMapper modelMapper;

    public ContractController(ContractService contractService, ModelMapper modelMapper) {
        this.contractService = contractService;
        this.modelMapper = modelMapper;
    }
    
    @PostMapping("/client/{idClient}")
    public ResponseEntity<?> createContract(@PathVariable("id") long idClient, @RequestBody Contract contract) {
    	try {
    		Contract createdContract = this.contractService.createContract(idClient, contract);
    		return ResponseEntity.status(201).body(createdContract);
    		
    	}
    	catch (RuntimeException e) {
    		if (e.getMessage().contains("Client not found")) {
    			return ResponseEntity.status(404).body(Collections.singletonMap("message", "Client not found"));
    		}
    		else {
    			return ResponseEntity.internalServerError().body(Collections.singletonMap("Internal error: ", e.getMessage()));
    		}
    	}
    }

}
