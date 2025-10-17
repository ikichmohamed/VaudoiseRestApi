package com.vaudoise.api.clientscontracts.testing;

import com.vaudoise.api.clientscontracts.Controllers.ClientController;
import com.vaudoise.api.clientscontracts.Controllers.ContractController;
import com.vaudoise.api.clientscontracts.Service.ClientService;
import com.vaudoise.api.clientscontracts.Service.ContractService;
import com.vaudoise.api.clientscontracts.dto.ClientDto;
import com.vaudoise.api.clientscontracts.dto.ContractDto;
import com.vaudoise.api.clientscontracts.model.Client;
import com.vaudoise.api.clientscontracts.model.Company;
import com.vaudoise.api.clientscontracts.model.Contract;
import com.vaudoise.api.clientscontracts.model.Person;

import jakarta.persistence.EntityNotFoundException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ContractController.class)
class ContractControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ContractService contractService;

    @MockBean
    private ModelMapper modelMapper; // ✅ ajouté pour corriger l’erreur

    private Person personEntity;
    private ClientDto personDto;

    @BeforeEach
    void setUp() {
        // Entity
        personEntity = new Person();
        personEntity.setId(1L);
        personEntity.setName("Mohamed Ikich");
        personEntity.setPhone("0600000000");
        personEntity.setEmail("mohamed.ikich@vaudoise.ch");

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
    // ✅ TEST 1 — Client trouvé
    // ==========================
    @Test
    @DisplayName("GET /api/clients/1 → retourne 200 et le client PERSON")
    void testGetClientById_Found() throws Exception {
        when(contractService.getClient(1)).thenReturn(Optional.of(personEntity));
        when(modelMapper.map(personEntity, ClientDto.class)).thenReturn(personDto);

        mockMvc.perform(get("/api/clients/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Mohamed Ikich"))
                .andExpect(jsonPath("$.email").value("mohamed.ikich@vaudoise.ch"))
                .andExpect(jsonPath("$.clientType").value("PERSON"));
    }

    // ===================================
    // ✅ TEST 2 — Client non trouvé (404)
    // ===================================
    

}



