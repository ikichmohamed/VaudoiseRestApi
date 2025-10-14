package com.vaudoise.api.clientscontracts.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;

@Entity // Indique que cette classe est une entité JPA, c’est-à-dire quelle sera mappée sur une table dans la base de données
public class Contract {

    @Id // Spécifie que ce champ est la clé primaire de l'entité
    @GeneratedValue(strategy = GenerationType.IDENTITY) // La valeur de l'id sera générée automatiquement par la base de données (auto-increment)
    private Long id;

    private LocalDate startDate; // Date de début du contrat
    private LocalDate endDate;   // Date de fin du contrat
    private Double costAmount;   // Montant du contrat
    private LocalDateTime updateDate; // Date de dernière mise à jour du contrat

    @ManyToOne // Relation plusieurs contrats -> un client
    @JoinColumn(name = "client_id") // Colonne de jointure dans la table Contract qui fait référence à la clé primaire de Client
    private Client client;

    @PrePersist // Méthode exécutée automatiquement avant la création d’un enregistrement en base
    public void onCreate() {
        if (startDate == null) // Si la date de début n’est pas renseignée
            startDate = LocalDate.now(); // On la fixe à aujourd’hui par défaut
        updateDate = LocalDateTime.now(); // Date de mise à jour initialisée à maintenant
    }

    @PreUpdate // Méthode exécutée automatiquement avant toute mise à jour en base
    public void onUpdate() {
        updateDate = LocalDateTime.now(); // On met à jour la date de modification à chaque update
    }
}

