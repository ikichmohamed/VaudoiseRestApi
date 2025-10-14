package com.vaudoise.api.clientscontracts.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.OneToMany;

@Entity // Indique que cette classe est une entité JPA, donc mappée à une table dans la base de données
@Inheritance(strategy = InheritanceType.SINGLE_TABLE) 
// Spécifie la stratégie d’héritage pour les classes dérivées
// SINGLE_TABLE = toutes les sous-classes seront stockées dans une seule table, avec une colonne discriminante [personne ou company]
@DiscriminatorColumn(name = "client_type") 
// Nom de la colonne qui indiquera le type concret du client (par exemple: personne, company)
public abstract class Client {

    @Id // Champ clé primaire
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
    // Génération automatique de l'ID par la base de données (auto-increment)
    private Long id;

    private String name;   // Nom du client
    private String phone;  // Téléphone du client
    private String email;  // Email du client

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = true)
    // Relation 1:N avec l'entité Contract (un client peut avoir plusieurs contrats)
    // mappedBy = "client" indique que la relation est gérée par le champ 'client' dans Contract
    // cascade ALL = toute opération sur Client (persist, merge, remove) se répercute sur ses contrats
    // orphanRemoval = true supprime automatiquement les contrats qui ne sont plus liés au client
    private List<Contract> contracts = new ArrayList<>(); 
    // Initialisation de la liste pour éviter les NullPointerException
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public List<Contract> getContracts() { return contracts; }
    public void setContracts(List<Contract> contracts) { this.contracts = contracts; }
}

