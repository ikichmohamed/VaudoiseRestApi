package com.vaudoise.api.clientscontracts.Controllers;

import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vaudoise.api.clientscontracts.Service.ClientService;
import com.vaudoise.api.clientscontracts.dto.ClientDto;
import com.vaudoise.api.clientscontracts.model.Client;
import com.vaudoise.api.clientscontracts.model.Company;
import com.vaudoise.api.clientscontracts.model.Person;


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

        return ResponseEntity.ok(responseDto);

    	
    }
    
    @GetMapping(value = "/{id}")
    public ResponseEntity<ClientDto> getClient(@PathVariable("id") String idClient) {
    	Client client = this.clientService.getClient(Integer.parseInt(idClient)).orElseThrow(()-> new RuntimeException("Client not found"));
    	ClientDto responseDto = modelMapper.map(client, ClientDto.class);
    	return ResponseEntity.ok(responseDto);
    }


}
