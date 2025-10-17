package com.vaudoise.api.clientscontracts.testing;

import com.vaudoise.api.clientscontracts.Controllers.ContractController;
import com.vaudoise.api.clientscontracts.Service.ClientService;
import com.vaudoise.api.clientscontracts.Service.ContractService;
import com.vaudoise.api.clientscontracts.dto.ClientDto;
import com.vaudoise.api.clientscontracts.dto.ContractDto;
import com.vaudoise.api.clientscontracts.model.Contract;
import com.vaudoise.api.clientscontracts.model.Person;

import jakarta.persistence.EntityNotFoundException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ContractController.class)
class ContractControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ContractService contractService;
    
    @MockBean
    private ClientService clientService;

    @MockBean
    private ModelMapper modelMapper; // ✅ ajouté pour corriger l’erreur
    
    @Autowired
    private ObjectMapper objectMapper;

    private Contract contractEntity;
    private Person personEntity;
    private ContractDto contractDto;
    private ClientDto personDto;
    private Contract contractUp;

    @BeforeEach
    void setUp() {
        // Entity
        contractEntity = new Contract();
        contractEntity.setId(1L);
        
        contractUp = new Contract();
        contractUp.setId(1L);
        
        personEntity = new Person();
        personEntity.setId(1L);
        personEntity.setName("Mohamed Ikich");
        personEntity.setPhone("0600000000");
        personEntity.setEmail("mohamed.ikich@vaudoise.ch");


        // DTO
        contractDto = new ContractDto();
        contractDto.setId(1L);
        
     // DTO
        personDto = new ClientDto();
        personDto.setId(1L);
        personDto.setName("Mohamed Ikich");
        personDto.setPhone("0600000000");
        personDto.setEmail("mohamed.ikich@vaudoise.ch");
        personDto.setClientType("PERSON");
        personDto.setBirthdate("1998-05-17");
        personDto.setContracts(List.of());
    }

    // ==========================
    // ✅ TEST 1 — Client trouvé et contract cree avec succes
    // ==========================
    @Test
    @DisplayName("✅ createContract - doit créer un contrat avec succès (201)")
    void createContract_Success() throws Exception {
    	
        long clientId = 1L;

        Contract contract = new Contract();
        contract.setId(10L);
        contract.setCostAmount(1000.0);
        contract.setStartDate(LocalDate.of(2025, 1, 1));

        when(contractService.createContract(eq(clientId), any(Contract.class)))
        .thenReturn(contract);

        mockMvc.perform(post("/api/contracts/client/{id}", clientId)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(contract)))
        .andDo(print()) // ✅ affiche dans les logs la requête et la réponse
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(10))
        .andExpect(jsonPath("$.costAmount").value(1000.0))
        .andExpect(jsonPath("$.startDate").value("2025-01-01"));
    }


 // ==========================
    // ✅ TEST 1 — Client trouvé
    // ==========================
    @Test
    @DisplayName("✅ createContract - client inexistant erreur (404)")
    void createContract_ClientNotFound() throws Exception {
    	
        long clientId = 999L;

        Contract contract = new Contract();
        contract.setId(10L);
        contract.setCostAmount(1000.0);
        contract.setStartDate(LocalDate.of(2025, 1, 1));

        when(contractService.createContract(eq(clientId), any(Contract.class)))
        .thenThrow(new RuntimeException("Client not found"));

        mockMvc.perform(post("/api/contracts/client/{id}", clientId)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(contract)))
        .andDo(print()) // ✅ affiche dans les logs la requête et la réponse
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").value("Client not found"));
        
    }
    
    @Test
    @DisplayName("💥 createContract - doit renvoyer 500 en cas d'erreur interne")
    void createContract_InternalServerError() throws Exception {
        long clientId = 1L;

        Contract contract = new Contract();
        contract.setCostAmount(200.0);

        when(contractService.createContract(eq(clientId), any(Contract.class)))
                .thenThrow(new RuntimeException("Unexpected error"));

        mockMvc.perform(post("/api/contracts/client/{id}", clientId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(contract)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Internal error: Unexpected error"));
    }
    
 // ✅ SUCCESS CASE
    @Test
    @DisplayName("✅ PUT /api/contracts/{id}/updateCost - success (200)")
    void updateCost_Success() throws Exception {
        long contractId = 10L;
        double updatedCost = 2000.0;

        Contract updatedContract = new Contract();
        updatedContract.setId(contractId);
        updatedContract.setCostAmount(updatedCost);
        updatedContract.setStartDate(LocalDate.of(2025, 1, 1));

        when(contractService.updateCost(eq(contractId), eq(updatedCost)))
                .thenReturn(updatedContract);

        mockMvc.perform(put("/api/contracts/{id}/updateCost", contractId)
                        .param("updatedCost", "2000")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.costAmount").value(2000.0))
                .andExpect(jsonPath("$.startDate").value("2025-01-01"));

        verify(contractService).updateCost(contractId, updatedCost);
    }

    // ❌ CONTRACT NOT FOUND
    @Test
    @DisplayName("❌ PUT /api/contracts/{id}/updateCost - contract not found (404)")
    void updateCost_ContractNotFound() throws Exception {
        long contractId = 999L;
        double updatedCost = 2000.0;

        when(contractService.updateCost(eq(contractId), eq(updatedCost)))
                .thenReturn(null);

        mockMvc.perform(put("/api/contracts/{id}/updateCost", contractId)
                        .param("updatedCost", "2000")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Contract not found"));
    }

    // 💥 INTERNAL SERVER ERROR
    @Test
    @DisplayName("💥 PUT /api/contracts/{id}/updateCost - internal server error (500)")
    void updateCost_InternalServerError() throws Exception {
        long contractId = 10L;
        double updatedCost = 2000.0;

        when(contractService.updateCost(eq(contractId), eq(updatedCost)))
                .thenThrow(new RuntimeException("Unexpected error"));

        mockMvc.perform(put("/api/contracts/{id}/updateCost", contractId)
                        .param("updatedCost", "2000")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Internal error: Unexpected error"));
    }

    

}



