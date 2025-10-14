package com.vaudoise.api.clientscontracts.model;

import jakarta.persistence.Entity;
import jakarta.persistence.DiscriminatorValue;
import java.time.LocalDate;

@Entity
@DiscriminatorValue("PERSON")
public class Person extends Client {

    private LocalDate birthDate;

    // Getters & Setters
    public LocalDate getBirthDate() { return birthDate; }
    public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }
}
