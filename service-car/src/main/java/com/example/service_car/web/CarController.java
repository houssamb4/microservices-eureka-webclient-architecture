package com.example.service_car.web;


import com.example.service_car.entities.Car;
import com.example.service_car.entities.Client;
import com.example.service_car.repositories.CarRepository;
import com.example.service_car.services.ClientApi;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cars")
public class CarController {

    private final CarRepository repo;
    private final ClientApi clientApi;

    public CarController(CarRepository repo, ClientApi clientApi) {
        this.repo = repo;
        this.clientApi = clientApi;
    }

    @PostMapping
    public Car save(@RequestBody Car car) {
        // Sauvegarde uniquement la voiture (DB locale carservicedb)
        return repo.save(car);
    }

    @GetMapping
    public List<Car> findAll() {
        List<Car> cars = repo.findAll();

        // Enrichissement : pour chaque voiture, récupérer le client
        for (Car car : cars) {
            if (car.getClientId() != null) {
                car.setClient(clientApi.findClientById(car.getClientId()));
            }
        }
        return cars;
    }

    @GetMapping("/byClient/{clientId}")
    public List<Car> findByClient(@PathVariable Long clientId) {
        List<Car> cars = repo.findByClientId(clientId);

        // Même client pour toutes les voitures de ce clientId
        for (Car car : cars) {
            car.setClient(clientApi.findClientById(clientId));
        }
        return cars;
    }
    @GetMapping("/{id}")
    public Car findById(@PathVariable Long id) {
        System.out.println("=== DEBUT findById ===");

        Car car = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Car not found with id: " + id));

        System.out.println("Car trouvée - ID: " + car.getId());
        System.out.println("ClientId: " + car.getClientId());
        System.out.println("Client avant appel API: " + car.getClient());

        if (car.getClientId() != null) {
            System.out.println("Appel ClientApi pour clientId: " + car.getClientId());

            try {
                Client client = clientApi.findClientById(car.getClientId());
                System.out.println("Client reçu: " + client);
                System.out.println("Client nom: " + (client != null ? client.getNom() : "NULL"));

                car.setClient(client);
                System.out.println("Client après setClient: " + car.getClient());

            } catch (Exception e) {
                System.err.println("ERREUR lors de l'appel ClientApi: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.out.println("ClientId est NULL - pas d'appel API");
        }

        System.out.println("=== FIN findById - Client final: " + car.getClient() + " ===");
        return car;
    }
}