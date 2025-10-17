package com.vaudoise.api.clientscontracts.Controllers;

import java.util.Collections;
import java.util.Map;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
    
    @PostMapping("/client/{id}")
    public ResponseEntity<?> createContract(@PathVariable("id") long idClient, @RequestBody Contract contract) {
    	try {
    		Contract createdContract = this.contractService.createContract(idClient, contract);
    		return ResponseEntity.status(HttpStatus.CREATED).body(createdContract);
    		
    	}
    	catch (RuntimeException e) {
    		if (e.getMessage().contains("Client not found")) {
    			return ResponseEntity.status(404).body(Map.of("message", "Client not found"));
    		}
    		else {
    			return ResponseEntity.internalServerError().body(Map.of("message", "Internal error: Unexpected error"));
    		}
    	}
    }
    
    @PutMapping("/{id}/updateCost")
    public ResponseEntity<?> updateCost(
            @PathVariable("id") long id,
            @RequestParam("updatedCost") double updatedCost) {
        try {
            Contract updatedContract = contractService.updateCost(id, updatedCost);
            if (updatedContract == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("message", "Contract not found"));
            }
            return ResponseEntity.ok(updatedContract);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Internal error: " + e.getMessage()));
        }
    }

}
