# Test Technique – Gestion des Clients et Contrats

## 📘 Contexte
Ce projet a été réalisé dans le cadre d’un test technique visant à concevoir une application **Spring Boot** exposant une **API REST** pour la gestion des **clients** et de leurs **contrats**.

L’application permet notamment de :
- Créer, lire, mettre à jour et supprimer des clients (personne ou entreprise) ;
- Créer des contrats liés à un client, avec gestion automatique des dates et règles métier spécifiques (ex. clôture des contrats lors de la suppression d’un client).

## 🧩 Architecture
L’application est structurée selon une architecture en couches :
- **Model** → entités JPA (`Client`, `Person`, `Company`, `Contract`)  
- **Repository** → interfaces Spring Data JPA  
- **Service** → logique métier (validation, gestion des dates, règles de suppression, etc.)  
- **DTO & Mapper (ModelMapper)** → conversion automatique entre entités et objets exposés  
- **REST Controller** → exposition des endpoints REST

## ✅ Avancement
J’ai finalisé et testé la couche **Service**, puis implémenté la première route REST :
`POST /api/clients`  
→ pour la création d’un client (Person ou Company).

Cette étape a permis de valider la cohérence entre les couches **Controller**, **Service** et **Repository**, ainsi que la conversion automatique via **ModelMapper**.

## 🧪 Test de la fonctionnalité – Create Client

Une fois l’application lancée (par défaut sur le port `8080`), il est possible de tester la création d’un client via la commande suivante :

```bash
curl -X POST http://localhost:8080/api/clients \
-H "Content-Type: application/json" \
-d '{
  "name": "Mohamed IKICH",
  "email": "ikich.mohamed.mpsi@gmail.com",
  "phone": "0600000000",
  "birthDate": "2000-12-05"
}'
```


# 📸 Captures d’écran

## 1. Commande curl pour créer un client
![curl command result](./screenshots/CurlPostCreateClient.png)
