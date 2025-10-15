package com.vaudoise.api.clientscontracts.Controllers;

import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vaudoise.api.clientscontracts.Service.ClientService;
import com.vaudoise.api.clientscontracts.dto.ClientDto;
import com.vaudoise.api.clientscontracts.model.Client;


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
    	Client client = this.modelMapper.map(clientDto,Client.class);
    	Client savedClient = this.clientService.createClient(client);
    	return ResponseEntity.ok(modelMapper.map(savedClient,ClientDto.class));
    	
    }


}
