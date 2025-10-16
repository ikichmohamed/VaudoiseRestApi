package com.vaudoise.api.clientscontracts.testing;

import com.vaudoise.api.clientscontracts.Controllers.ClientController;
import com.vaudoise.api.clientscontracts.Service.ClientService;
import com.vaudoise.api.clientscontracts.dto.ClientDto;
import com.vaudoise.api.clientscontracts.dto.ContractDto;
import com.vaudoise.api.clientscontracts.model.Client;
import com.vaudoise.api.clientscontracts.model.Company;
import com.vaudoise.api.clientscontracts.model.Person;
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
}
