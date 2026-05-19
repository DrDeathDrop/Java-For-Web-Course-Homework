package pu.fmi.webprogramming.service;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import pu.fmi.webprogramming.exception.DeliveryCustomException;
import pu.fmi.webprogramming.model.Courier;
import pu.fmi.webprogramming.model.Customer;
import pu.fmi.webprogramming.model.Delivery;
import pu.fmi.webprogramming.model.Warehouse;
import pu.fmi.webprogramming.model.enums.DeliveryStatusEnum;
import pu.fmi.webprogramming.repository.CourierRepository;
import pu.fmi.webprogramming.repository.DeliveryRepository;
import pu.fmi.webprogramming.repository.WarehouseRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static pu.fmi.webprogramming.model.enums.DeliveryStatusEnum.*;

@Service
public class DeliveryService implements DeliveryServiceInterface {

  private final DeliveryRepository deliveryRepository;
  private final CourierRepository courierRepository;
  private final WarehouseRepository warehouseRepository;
  private final DeliveryEstimator deliveryEstimator;

  public DeliveryService(
      DeliveryRepository deliveryRepository,
      CourierRepository courierRepository,
      WarehouseRepository warehouseRepository,
      DeliveryEstimator deliveryEstimator) {
    this.deliveryRepository = deliveryRepository;
    this.courierRepository = courierRepository;
    this.warehouseRepository = warehouseRepository;
    this.deliveryEstimator = deliveryEstimator;
  }

  @PostConstruct
  public void init() {
    System.out.println("Initializing Delivery Service");
  }

  @PreDestroy
  public void destroy() {
    System.out.println("Destroy DeliveryService");
  }

  @Override
  public Delivery createDelivery(Customer customer) {
    Delivery delivery = new Delivery();

    Courier courier = courierRepository.findAvailableCourier();
    Warehouse warehouse = warehouseRepository.findByCustomerCity(customer);

    delivery.setCreatedAt(LocalDateTime.now());
    delivery.setCustomer(customer);
    delivery.setDeliveredAt(null);
    delivery.setWarehouse(warehouse);

    if (courier != null) {
      delivery.setCourier(courier);
      delivery.setDeliveryStatus(ASSIGNED);
    } else {
      delivery.setDeliveryStatus(CREATED);
    }

    LocalDateTime estimatedArrivalAt = deliveryEstimator.estimateArrivalTime(delivery);
    delivery.setEstimatedArrivalAt(estimatedArrivalAt);

    deliveryRepository.save(delivery);

    return delivery;
  }

  @Override
  public boolean updateDeliveryStatus(Long id, DeliveryStatusEnum newStatus) {

    Delivery delivery = deliveryRepository.findById(id);

    if (delivery == null) {
      return false;
    }

    if (isStatusValid(delivery.getDeliveryStatus(), newStatus)) {
      delivery.setDeliveryStatus(newStatus);
      return true;
    }

    return false;
  }

  @Override
  public List<Delivery> getAllDeliveries() {
    return deliveryRepository.findAllDeliveries();
  }

  @Override
  public Delivery assignCourier(Long id, Long courierId) {

    Delivery delivery = deliveryRepository.findById(id);
    if (delivery == null) {
      throw new DeliveryCustomException("Delivery not found");
    }

    Courier courier = courierRepository.getAllCouriers().stream()
            .filter(c -> c.getId().equals(courierId))
            .findFirst()
            .orElse(null);
    if (courier == null) {
      throw new DeliveryCustomException("Courier not found");
    }

    if (!courier.isAvailable()) {
      throw new DeliveryCustomException("Courier is not available");
    }

    delivery.setCourier(courier);
    delivery.setDeliveryStatus(ASSIGNED);
    delivery.setEstimatedArrivalAt(deliveryEstimator.estimateArrivalTime(delivery));

    courier.setAvailable(false);

    return delivery;
  }
  private boolean isStatusValid(DeliveryStatusEnum currentStatus, DeliveryStatusEnum newStatus) {

    if (CREATED.equals(currentStatus) && ASSIGNED.equals(newStatus)) {
      return true;
    }
    if (ASSIGNED.equals(currentStatus) && IN_PROGRESS.equals(newStatus)) {
      return true;
    }
    if (IN_PROGRESS.equals(currentStatus) && DELIVERED.equals(newStatus)) {
      return true;
    }
    if ((CREATED.equals(currentStatus)
        || ASSIGNED.equals(currentStatus)) && CANCELED.equals(newStatus)) {
      return true;
    }

    return false;
  }

}
