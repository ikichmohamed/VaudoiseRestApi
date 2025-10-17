package com.vaudoise.api.clientscontracts.testing;

import com.vaudoise.api.clientscontracts.Controllers.ClientController;
import com.vaudoise.api.clientscontracts.Service.ClientService;
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ClientController.class)
class ClientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ClientService clientService;

    @MockBean
    private ModelMapper modelMapper; // ✅ ajouté pour corriger l’erreur

    private Person personEntity;
    private ClientDto personDto;
    private List<Contract> activeContracts;
    private Contract contract1;
    private Contract contract2;

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
        
     // 📜 Create active contracts for this client (no end date yet)
        contract1 = new Contract();
        contract1.setId(10L);
        contract1.setStartDate(LocalDate.of(2024, 1, 1));
        contract1.setEndDate(null);
        

        contract2 = new Contract();
        contract2.setId(11L);
        contract2.setStartDate(LocalDate.of(2024, 3, 10));
        contract2.setEndDate(null);
        
    }

    // ==========================
    // ✅ TEST 1 — Client trouvé
    // ==========================
    @Test
    @DisplayName("GET /api/clients/1 → retourne 200 et le client PERSON")
    void testGetClientById_Found() throws Exception {
        when(clientService.getClient(1)).thenReturn(Optional.of(personEntity));
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
    @Test
    @DisplayName("GET /api/clients/999 → retourne 404 si client inexistant")
    void testGetClientById_NotFound() throws Exception {
        when(clientService.getClient(999)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/clients/999")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    // ========================================
    // ✅ TEST 3 — Création d’un nouveau client
    // ========================================
    @Test
    @DisplayName("POST /api/clients → crée un client et retourne 201")
    void testCreateClient() throws Exception {
        when(modelMapper.map(any(ClientDto.class), Mockito.eq(Person.class))).thenReturn(personEntity);
        when(clientService.createClient(any(Client.class))).thenReturn(personEntity);
        when(modelMapper.map(personEntity, ClientDto.class)).thenReturn(personDto);

        String newClientJson = """
            {
              "name": "Mohamed Ikich",
              "phone": "0600000000",
              "email": "mohamed.ikich@vaudoise.ch",
              "clientType": "PERSON",
              "birthdate": "1998-05-17"
            }
            """;

        mockMvc.perform(post("/api/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newClientJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Mohamed Ikich"))
                .andExpect(jsonPath("$.clientType").value("PERSON"));
    }

    // ========================================
    // ✅ TEST 4 — Création invalide (400)
    // ========================================
    @Test
    @DisplayName("POST /api/clients → retourne 400 si données invalides")
    void testCreateClient_InvalidRequest() throws Exception {
        String invalidJson = """
            {
              "email": "invalid@vaudoise.ch"
            }
            """;

        mockMvc.perform(post("/api/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    @DisplayName("✅ doit mettre à jour un client PERSON et retourner 200 OK")
    void testUpdateClient_Person_Success() throws Exception {
        long clientId = 1L;

        ClientDto updatedClientDto = new ClientDto();
        updatedClientDto.setId(clientId);
        updatedClientDto.setName("Updated Name");
        updatedClientDto.setPhone("0611223344");
        updatedClientDto.setEmail("updated@email.com");
        updatedClientDto.setClientType("PERSON");

        Person updatedClient = new Person();
        updatedClient.setId(clientId);
        updatedClient.setName("Updated Name");
        updatedClient.setPhone("0611223344");
        updatedClient.setEmail("updated@email.com");

        when(modelMapper.map(any(ClientDto.class), eq(Person.class))).thenReturn(updatedClient);
        when(clientService.updateClient(eq(clientId), any(Client.class))).thenReturn(updatedClient);
        when(modelMapper.map(any(Client.class), eq(ClientDto.class))).thenReturn(updatedClientDto);

        mockMvc.perform(put("/api/clients/{id}", clientId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "id": 1,
                        "name": "Updated Name",
                        "phone": "0611223344",
                        "email": "updated@email.com",
                        "clientType": "PERSON"
                    }
                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Name"))
                .andExpect(jsonPath("$.phone").value("0611223344"))
                .andExpect(jsonPath("$.email").value("updated@email.com"));

        verify(clientService, times(1)).updateClient(eq(clientId), any(Client.class));
    }

    @Test
    @DisplayName("✅ doit mettre à jour un client COMPANY et retourner 200 OK")
    void testUpdateClient_Company_Success() throws Exception {
        long clientId = 2L;

        ClientDto updatedClientDto = new ClientDto();
        updatedClientDto.setId(clientId);
        updatedClientDto.setName("New Company Name");
        updatedClientDto.setPhone("0711223344");
        updatedClientDto.setEmail("contact@company.com");
        updatedClientDto.setClientType("COMPANY");

        Company updatedClient = new Company();
        updatedClient.setId(clientId);
        updatedClient.setName("New Company Name");
        updatedClient.setPhone("0711223344");
        updatedClient.setEmail("contact@company.com");

        when(modelMapper.map(any(ClientDto.class), eq(Company.class))).thenReturn(updatedClient);
        when(clientService.updateClient(eq(clientId), any(Client.class))).thenReturn(updatedClient);
        when(modelMapper.map(any(Client.class), eq(ClientDto.class))).thenReturn(updatedClientDto);

        mockMvc.perform(put("/api/clients/{id}", clientId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "id": 2,
                        "name": "New Company Name",
                        "phone": "0711223344",
                        "email": "contact@company.com",
                        "clientType": "COMPANY"
                    }
                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("New Company Name"))
                .andExpect(jsonPath("$.email").value("contact@company.com"));

        verify(clientService, times(1)).updateClient(eq(clientId), any(Client.class));
    }

    @Test
    @DisplayName("❌ doit retourner 400 Bad Request si le type de client est invalide")
    void testUpdateClient_InvalidType() throws Exception {
        mockMvc.perform(put("/api/clients/{id}", 3)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "id": 3,
                        "name": "Bad Client",
                        "phone": "0800000000",
                        "email": "bad@client.com",
                        "clientType": "ALIEN"
                    }
                """))
                .andExpect(status().isBadRequest());

        verify(clientService, never()).updateClient(anyLong(), any(Client.class));
    }

    @Test
    @DisplayName("❌ doit retourner 404 Not Found si le client n’existe pas")
    void testUpdateClient_ClientNotFound() throws Exception {
        long clientId = 4L;

        ClientDto updatedClientDto = new ClientDto();
        updatedClientDto.setId(clientId);
        updatedClientDto.setName("Ghost");
        updatedClientDto.setClientType("PERSON");

        Person updatedClient = new Person();
        updatedClient.setId(clientId);
        updatedClient.setName("Ghost");

        when(modelMapper.map(any(ClientDto.class), eq(Person.class))).thenReturn(updatedClient);
        when(clientService.updateClient(eq(clientId), any(Client.class)))
                .thenThrow(new RuntimeException("Client Not found"));

        mockMvc.perform(put("/api/clients/{id}", clientId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "id": 4,
                        "name": "Ghost",
                        "clientType": "PERSON"
                    }
                """))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Client Not found"));

        verify(clientService, times(1)).updateClient(eq(clientId), any(Client.class));
    }

    @Test
    @DisplayName("❌ doit retourner 400 Bad Request si le JSON est invalide")
    void testUpdateClient_InvalidJson() throws Exception {
        mockMvc.perform(put("/api/clients/{id}", 5)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ invalid json }"))
                .andExpect(status().isBadRequest());

        verify(clientService, never()).updateClient(anyLong(), any(Client.class));
    }
    
 // ✅ CAS 1 : Suppression réussie (client trouvé, contrats mis à jour)
    @Test
    @DisplayName("✅ DELETE /api/clients/{id} - should return 200 and update contracts endDate when client deleted successfully")
    void deleteClient_Success() throws Exception {
        long clientId = 1L;

        // On simule la mise à jour des contrats avec endDate = today
        List<Contract> originContracts = List.of(contract1, contract2);
        personEntity.setContracts(originContracts);
        contract1.setEndDate(LocalDate.now());
        contract2.setEndDate(LocalDate.now());
        List<Contract> updatedContracts = List.of(contract1, contract2);
        

        when(clientService.deleteClient(clientId)).thenReturn(updatedContracts);

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/api/clients/delete/{id}", clientId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print()) // affichage du résultat HTTP
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Client deleted successfully"))
                .andExpect(jsonPath("$.contractsUpdated").value(2));

        verify(clientService, times(1)).deleteClient(clientId);
    }

    @Test
    @DisplayName("❌ DELETE /api/clients/{id} - should return 404 when client does not exist")
    void deleteClient_NotFound() throws Exception {
        long clientId = 999L;
        doThrow(new EntityNotFoundException("Client not found")).when(clientService).deleteClient(clientId);

        mockMvc.perform(delete("/api/clients/delete/{id}", clientId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("⚠️ DELETE /api/clients/{id} - should return 500 when internal error occurs")
    void deleteClient_InternalError() throws Exception {
        long clientId = 5L;
        doThrow(new IllegalStateException("Unexpected error")).when(clientService).deleteClient(clientId);

        mockMvc.perform(delete("/api/clients/delete/{id}", clientId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }
    
    @Test
    @DisplayName("✅ doit retourner la liste des contrats actifs d’un client existant")
    void testGetActiveContracts_Success() throws Exception {
        long clientId = 1L;

        Contract contract1 = new Contract();
        contract1.setId(100L);
        contract1.setCostAmount(2500.0);

        Contract contract2 = new Contract();
        contract2.setId(101L);
        contract2.setCostAmount(3200.0);

        when(clientService.getClient(clientId)).thenReturn(Optional.of(new Person()));
        when(clientService.getActiveContracts(clientId)).thenReturn(List.of(contract1, contract2));

        mockMvc.perform(get("/api/clients/{id}/contracts/active", clientId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(100))
                .andExpect(jsonPath("$[1].id").value(101));

        verify(clientService, times(1)).getActiveContracts(clientId);
    }

    @Test
    @DisplayName("✅ doit retourner une liste vide si le client n’a aucun contrat actif")
    void testGetActiveContracts_EmptyList() throws Exception {
        long clientId = 2L;

        when(clientService.getClient(clientId)).thenReturn(Optional.of(new Person()));
        when(clientService.getActiveContracts(clientId)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/clients/{id}/contracts/active", clientId))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        verify(clientService, times(1)).getActiveContracts(clientId);
    }

    @Test
    @DisplayName("❌ doit retourner 404 si le client n’existe pas")
    void testGetActiveContracts_ClientNotFound() throws Exception {
        long clientId = 999L;

        when(clientService.getClient(clientId)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/clients/{id}/contracts/active", clientId))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Client not found"));

        verify(clientService, never()).getActiveContracts(anyLong());
    }

    @Test
    @DisplayName("❌ doit retourner 500 si une erreur interne survient")
    void testGetActiveContracts_InternalError() throws Exception {
        long clientId = 3L;

        when(clientService.getClient(clientId)).thenReturn(Optional.of(new Person()));
        when(clientService.getActiveContracts(clientId)).thenThrow(new RuntimeException("Unexpected DB error"));

        mockMvc.perform(get("/api/clients/{id}/contracts/active", clientId))
                .andExpect(status().isInternalServerError());

        verify(clientService, times(1)).getActiveContracts(clientId);
    }
    @Test
    @DisplayName("✅ Should return active contracts updated after given date for existing client")
    void testGetActiveContractsFilteredByUpdatedDate_Success() throws Exception {
        long clientId = 1L;
        LocalDate filterDate = LocalDate.of(2024, 1, 1);

        // Création de contrats fictifs
        Contract activeRecentContract = new Contract();
        activeRecentContract.setId(10L);
        

        Contract oldContract = new Contract();
        oldContract.setId(11L);
        

        Contract inactiveContract = new Contract();
        inactiveContract.setId(12L);
        

        // Attacher les contrats au client
        Person client = new Person();
        client.setId(clientId);
        client.setName("Client Test");
        client.setContracts(List.of(activeRecentContract, oldContract, inactiveContract));

        // Seul activeRecentContract doit passer le filtre
        List<Contract> expectedContracts = List.of(activeRecentContract);

        when(clientService.getActiveContractsFilteredByUpdatedDate(clientId, filterDate))
                .thenReturn(expectedContracts);

        mockMvc.perform(get("/api/clients/{id}/contracts/filteredactive", clientId)
                        .param("updatedDate", filterDate.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(10L));
    }

    @Test
    @DisplayName("❌ Should return 404 when trying to get contracts for non-existent client")
    void testGetActiveContractsFilteredByUpdatedDate_ClientNotFound() throws Exception {
        long clientId = 99L;
        LocalDate filterDate = LocalDate.of(2024, 1, 1);

        when(clientService.getActiveContractsFilteredByUpdatedDate(clientId, filterDate))
                .thenThrow(new EntityNotFoundException("Client not found"));

        mockMvc.perform(get("/api/clients/{id}/contracts/filteredactive", clientId)
                        .param("updatedDate", filterDate.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Client not found"));
    }

    @Test
    @DisplayName("⚠️ Should return 400 when updatedDate is missing or invalid")
    void testGetActiveContractsFilteredByUpdatedDate_MissingOrInvalidDate() throws Exception {
        long clientId = 1L;

        // Missing date
        mockMvc.perform(get("/api/clients/{id}/contracts/filteredactive", clientId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        // Invalid date format
        mockMvc.perform(get("/api/clients/{id}/contracts/filteredactive", clientId)
                        .param("updatedDate", "invalid-date")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("⚠️ Should return 500 when unexpected error occurs in service")
    void testGetActiveContractsFilteredByUpdatedDate_InternalServerError() throws Exception {
        long clientId = 1L;
        LocalDate filterDate = LocalDate.of(2024, 1, 1);

        when(clientService.getActiveContractsFilteredByUpdatedDate(clientId, filterDate))
                .thenThrow(new RuntimeException("Unexpected error"));

        mockMvc.perform(get("/api/clients/{id}/contracts/filteredactive", clientId)
                        .param("updatedDate", filterDate.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Unexpected error"));
    }
    
    @Test
    @DisplayName("✅ doit retourner le total des contrats actifs si le client existe")
    void testGetActiveContractsTotal_shouldReturnTotal_whenClientExists() throws Exception {
        long clientId = 1L;

        // Mock : le client existe
        when(clientService.getClient(clientId)).thenReturn(Optional.of(new Person()));
        when(clientService.getActiveContractsTotal(clientId)).thenReturn(1250.75);

        mockMvc.perform(get("/api/clients/{id}/contracts/active/total", clientId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.clientId").value(clientId))
                .andExpect(jsonPath("$.totalActiveContractsCost").value(1250.75));
    }


    @Test
    @DisplayName("⚪ doit retourner 0.0 si le client existe mais n’a aucun contrat actif")
    void testGetActiveContractsTotal_shouldReturnZero_whenNoActiveContracts() throws Exception {
        long clientId = 2L;

        // Mock : le client existe mais aucun contrat actif
        when(clientService.getClient(clientId)).thenReturn(Optional.of(new Person()));
        when(clientService.getActiveContractsTotal(clientId)).thenReturn(0.0);

        mockMvc.perform(get("/api/clients/{id}/contracts/active/total", clientId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.clientId").value(clientId))
                .andExpect(jsonPath("$.totalActiveContractsCost").value(0.0));
    }


    @Test
    @DisplayName("❌ doit retourner 404 si le client n’existe pas")
    void testGetActiveContractsTotal_shouldReturn404_whenClientNotFound() throws Exception {
        long clientId = 999L;

        // Mock : client inexistant
        when(clientService.getClient(clientId)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/clients/{id}/contracts/active/total", clientId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Client not found"));
    }


    @Test
    @DisplayName("🔥 doit retourner 500 si une erreur interne survient")
    void testGetActiveContractsTotal_shouldReturn500_whenInternalErrorOccurs() throws Exception {
        long clientId = 3L;

        // Mock : exception simulée
        when(clientService.getClient(clientId)).thenThrow(new RuntimeException("Unexpected error"));

        mockMvc.perform(get("/api/clients/{id}/contracts/active/total", clientId))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Unexpected error"));
    }

}



