# TP 21 : Architecture Micro-services avec WebClient

Ce projet est une architecture microservices basée sur Spring Boot 3, utilisant Eureka Server pour la découverte de services et WebClient pour la communication réactive entre microservices. Chaque service utilise MySQL pour la persistance.

## Structure du projet

- **eureka-server** : Serveur Eureka (port 8761)
- **service-client** : Microservice de gestion des clients (port 8081)
- **service-car** : Microservice de gestion des voitures (port 8082)

## Technologies principales

- Spring Boot 3.5.x
- Spring Cloud 2025.x
- Eureka Server & Client
- WebClient (WebFlux)
- Spring Data JPA
- MySQL

## Démarrage rapide

1. **Lancer MySQL** (créez les bases `clientservicedb` et `carservicedb` ou laissez Spring les créer)
2. **Lancer les services dans cet ordre :**
   - `eureka-server` (`localhost:8761`)
   - `service-client` (`localhost:8081`)
   - `service-car` (`localhost:8082`)
3. Vérifiez le dashboard Eureka : les services `SERVICE-CLIENT` et `SERVICE-CAR` doivent être visibles.

## Exemples d'utilisation

### 1. Créer un client
```http
POST http://localhost:8081/api/clients
{
  "nom": "Salma",
  "age": 22
}
```
Puis :
```http
GET http://localhost:8081/api/clients
```
Notez l'id du client créé (ex: 1).

### 2. Créer une voiture liée à un client
```http
POST http://localhost:8082/api/cars
{
  "marque": "Toyota",
  "modele": "Yaris",
  "clientId": 1
}
```

### 3. Lire les voitures enrichies
```http
GET http://localhost:8082/api/cars
```
Réponse attendue :
```json
[
  {
    "id": 1,
    "marque": "Toyota",
    "modele": "Yaris",
    "clientId": 1,
    "client": {
      "id": 1,
      "nom": "Salma",
      "age": 22.0
    }
  }
]
```

## Configuration (extraits)

### eureka-server/src/main/resources/application.yml
```yaml
server:
  port: 8761
eureka:
  client:
    register-with-eureka: false
    fetch-registry: false
```

### service-client/src/main/resources/application.yml
```yaml
server:
  port: 8081
spring:
  application:
    name: SERVICE-CLIENT
  datasource:
    url: jdbc:mysql://localhost:3306/clientservicedb?createDatabaseIfNotExist=true
    username: root
    password: <votre_mot_de_passe>
eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka
  instance:
    prefer-ip-address: true
    ip-address: 127.0.0.1
```

### service-car/src/main/resources/application.yml
```yaml
server:
  port: 8082
spring:
  application:
    name: SERVICE-CAR
  datasource:
    url: jdbc:mysql://localhost:3306/carservicedb?createDatabaseIfNotExist=true
    username: root
    password: <votre_mot_de_passe>
eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka
  instance:
    prefer-ip-address: true
    ip-address: 127.0.0.1
```

## Dépendances Maven principales

### eureka-server
```xml
<dependency>
  <groupId>org.springframework.cloud</groupId>
  <artifactId>spring-cloud-starter-netflix-eureka-server</artifactId>
</dependency>
```

### service-client
```xml
<dependency>
  <groupId>org.springframework.cloud</groupId>
  <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
</dependency>
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
<dependency>
  <groupId>com.mysql</groupId>
  <artifactId>mysql-connector-j</artifactId>
  <scope>runtime</scope>
</dependency>
```

### service-car
```xml
<dependency>
  <groupId>org.springframework.cloud</groupId>
  <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
</dependency>
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-webflux</artifactId>
</dependency>
<dependency>
  <groupId>org.springframework.cloud</groupId>
  <artifactId>spring-cloud-starter-loadbalancer</artifactId>
</dependency>
<dependency>
  <groupId>com.mysql</groupId>
  <artifactId>mysql-connector-j</artifactId>
  <scope>runtime</scope>
</dependency>
```

## Problèmes fréquents & Dépannage

- **No instances available for SERVICE-CLIENT**
  - Vérifiez l'annotation `@LoadBalanced` sur le bean `WebClient.Builder`
  - Dépendance LoadBalancer manquante
  - Mauvaise configuration `defaultZone` ou service non démarré
- **Service visible dans Eureka mais WebClient échoue**
  - Endpoint incorrect ou service crashé
  - Vérifiez les logs
- **Problème MySQL au démarrage**
  - MySQL arrêté, mauvais mot de passe, base non créée
- **404 sur endpoints**
  - Mauvais chemin dans le controller ou erreur de port

## Auteur

Projet pédagogique Spring Boot & Microservices

4.1 Ordre de lancement
eureka-server (8761)
service-client (8081)
service-car (8082)
Validation
Dashboard Eureka : SERVICE-CLIENT et SERVICE-CAR visibles

4.2 Créer un client
POST http://localhost:8081/api/clients

{ "nom": "Salma", "age": 22 }
Puis GET http://localhost:8081/api/clients
Noter l’id (ex: 1)

4.3 Créer une voiture liée au client
POST http://localhost:8082/api/cars

{ "marque": "Toyota", "modele": "Yaris", "clientId": 1 }
4.4 Lire les voitures enrichies
GET http://localhost:8082/api/cars

Résultat attendu
[
  {
    "id": 1,
    "marque": "Toyota",
    "modele": "Yaris",
    "clientId": 1,
    "client": {
      "id": 1,
      "nom": "Salma",
      "age": 22.0
    }
  }
]  