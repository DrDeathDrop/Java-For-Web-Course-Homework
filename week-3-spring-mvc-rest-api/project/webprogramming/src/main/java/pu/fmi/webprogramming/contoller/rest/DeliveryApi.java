package pu.fmi.webprogramming.contoller.rest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pu.fmi.webprogramming.exception.DeliveryCustomException;
import pu.fmi.webprogramming.model.CreateDeliveryDTO;
import pu.fmi.webprogramming.model.Customer;
import pu.fmi.webprogramming.model.Delivery;
import pu.fmi.webprogramming.model.enums.DeliveryStatusEnum;
import pu.fmi.webprogramming.service.DeliveryServiceInterface;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/deliveries")
public class DeliveryApi {

  private final DeliveryServiceInterface deliveryServiceInterface;
  private final List<Customer> customers = new ArrayList<>();

  public DeliveryApi(DeliveryServiceInterface deliveryServiceInterface) {
    this.deliveryServiceInterface = deliveryServiceInterface;
    Customer customer1 = new Customer(1L, "Ivan",
            "Ivanov", "ivan.ivanov",
            "000", "Plovdiv");
    Customer customer2 = new Customer(2L, "Georgi",
            "Ivanov", "georgi.ivanov",
            "000", "Plovdiv");
    customers.add(customer1);
    customers.add(customer2);
  }

  // TODO: Добавете ново REST API - PUT '/api/deliveries/{id}/courier'
  // и използвай добавената от теб логика на DeliveryService.assignCourier метода
  @PutMapping("/{id}/courier")
  public ResponseEntity<Delivery> assignCourier(@PathVariable Long id, @RequestParam Long courierId) {
    Delivery delivery = deliveryServiceInterface.assignCourier(id, courierId);
    return ResponseEntity.ok(delivery);
  }



  @PutMapping("/{id}") // PUT /api/deliveries/{id}?status=
  public boolean updateDeliveryStatus(
          @PathVariable Long id,
          @RequestParam DeliveryStatusEnum status) {
    return deliveryServiceInterface.updateDeliveryStatus(id, status);
  }

  @PostMapping("/customer") // POST "/api/deliveries/customer"
  public Delivery createDelivery(@RequestBody CreateDeliveryDTO createDeliveryDTO) {

    Long id = createDeliveryDTO.getCustomerId();

    Customer customerFound =
            customers.stream()
                    .filter(customer -> customer.getId().equals(id))
                    .findFirst()
                    .orElse(null);

    if (customerFound == null) {
      throw new DeliveryCustomException("Customer with id: " + id + " not found");
    }

    return deliveryServiceInterface.createDelivery(customerFound);
  }

  @PostMapping("/customer/{id}") // POST "/api/deliveries/customer/id"
  public Delivery createDelivery(@PathVariable Long id) {
    Customer customerFound =
            customers.stream()
                    .filter(customer -> customer.getId().equals(id))
                    .findFirst()
                    .orElse(null);

    if (customerFound == null) {
      throw new DeliveryCustomException("Customer with id: " + id + " not found");
    }

    return deliveryServiceInterface.createDelivery(customerFound);
  }

  @GetMapping // GET "/api/deliveries"
  public List<Delivery> getAllDeliveries() {
    return deliveryServiceInterface.getAllDeliveries();
  }

  @GetMapping("/{deliveryId}") // GET "/api/deliveries/{deliveryId}"
  public ResponseEntity<?> getDeliveryById(@PathVariable Long deliveryId) {
    Delivery deliveryFound =
        deliveryServiceInterface.getAllDeliveries().stream()
            .filter(delivery -> delivery.getId().equals(deliveryId))
            .findFirst()
            .orElse(null);

    if (deliveryFound == null) {
      throw new DeliveryCustomException("Delivery with id: " + deliveryId + " not found");
    }

    return ResponseEntity.status(HttpStatus.OK).body(deliveryFound);
  }
}
