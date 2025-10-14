package com.vaudoise.api.clientscontracts.model;

import jakarta.persistence.Entity;
import jakarta.persistence.DiscriminatorValue;
import jakarta.validation.constraints.NotBlank;

@Entity
@DiscriminatorValue("COMPANY")
public class Company extends Client {

    @NotBlank
    private String companyIdentifier;

    // Getters & Setters
    public String getCompanyIdentifier() { return companyIdentifier; }
    public void setCompanyIdentifier(String companyIdentifier) { this.companyIdentifier = companyIdentifier; }
}

