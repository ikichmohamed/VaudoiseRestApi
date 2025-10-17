
# 🧠 Test Technique – Gestion des Clients et Contrats

## 📘 Contexte

Ce projet a été réalisé dans le cadre d’un test technique visant à concevoir une application **Spring Boot** exposant une **API REST** pour la gestion des **clients** et de leurs **contrats**.

L’application permet notamment de :

- Créer, lire, mettre à jour et supprimer des clients (personne ou entreprise)  
- Créer et gérer des contrats liés à un client  
- Calculer le coût total des contrats actifs d’un client  
- Clôturer automatiquement les contrats lors de la suppression d’un client  



## 🧩 Architecture du projet

Le projet suit une architecture en couches claire et modulaire :

| Couche | Description |
|--------|--------------|
| **Model** | Contient les entités JPA : `Client`, `Person`, `Company`, `Contract` |
| **Repository** | Interfaces Spring Data JPA pour la persistance (`ClientRepository`, `ContractRepository`) |
| **Service** | Contient la logique métier (validation, gestion des dates, règles de suppression, etc.) |
| **Controller (REST)** | Expose les endpoints REST : `/api/clients` et `/api/contracts` |
| **DTO & Mapper** | Conversion entre les entités et objets exposés via **ModelMapper** |
| **Test** | Ensemble de tests unitaires et MockMvc pour valider les comportements REST |



## ⚙️ Installation et exécution

### 🧱 Prérequis

- **Java 17** ou version supérieure  
- **Gradle** ou **Maven**  
- Un IDE compatible : *Eclipse*, *IntelliJ IDEA* ou *VS Code*  



### 🚀 Étapes d’installation

#### 1️⃣ Cloner le projet
```bash
git clone https://github.com/ikichmohamed/VaudoiseRestApi.git
cd VaudoiseRestApiClientsContracts
````

#### 2️⃣ Compiler et lancer les tests

```bash
./gradlew clean test
```

#### 3️⃣ Lancer l’application

```bash
./gradlew bootRun
```

L’application démarre par défaut sur :
👉 [http://localhost:8080](http://localhost:8080)

---

## 🧪 Tests unitaires et MockMvc

Le projet contient des tests **JUnit 5** et **Spring Boot** pour vérifier :

* Les règles métier du `ClientService` et `ContractService`
* Les endpoints REST exposés par `ClientController` et `ContractController`
* La gestion des erreurs (`404`, `500`, validations, etc.)

📍 **Lancer tous les tests :**

```bash
./gradlew test
```

Un rapport complet est ensuite disponible ici :
📄 `build/reports/tests/test/index.html`

---

## ✅ Exemples de tests REST MockMvc

### ➕ Création d’un client :

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

---

### 🔁 Mise à jour du coût d’un contrat :

```bash
curl -X PUT "http://localhost:8080/api/contracts/10/updateCost?updatedCost=2000"
```

---

### ❌ Suppression d’un client et clôture automatique de ses contrats :

```bash
curl -X DELETE http://localhost:8080/api/clients/1
```

---

## 🧠 Points clés techniques

* **Spring Boot 3+**
* **Spring Data JPA** (base de données **H2** en mémoire pour les tests)
* **MockMvc / Mockito / JUnit 5** pour les tests REST
* **ModelMapper** pour la conversion DTO ↔️ Entités
* Gestion claire des **statuts HTTP** :

  * `200 OK` → succès
  * `201 Created` → création
  * `404 Not Found` → ressource inexistante
  * `500 Internal Server Error` → erreur interne



## 👨‍💻 Auteur

**Mohamed IKICH**
🎓 Ingénieur diplômé de l’**ENSEEIHT Toulouse**
💻 Spécialisé en **Big Data, Cloud et développement logiciel** 
Solution Engineer chez Bizzdesign

📧 [ikich.mohamed.mpsi@gmail.com](mailto:ikich.mohamed.mpsi@gmail.com)
🌐 [LinkedIn](https://www.linkedin.com/in/mohamed-ikich-2b285b1b4)

