package pu.fmi.webprogramming.service;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Service;
import pu.fmi.webprogramming.model.Courier;
import pu.fmi.webprogramming.model.Customer;
import pu.fmi.webprogramming.model.Delivery;
import pu.fmi.webprogramming.model.Warehouse;
import pu.fmi.webprogramming.model.enums.DeliveryStatusEnum;
import pu.fmi.webprogramming.repository.CourierRepository;
import pu.fmi.webprogramming.repository.DeliveryRepository;
import pu.fmi.webprogramming.repository.WarehouseRepository;

import java.time.LocalDateTime;

import static pu.fmi.webprogramming.model.enums.DeliveryStatusEnum.*;

@Service // или @Component
public class DeliveryService implements DeliveryServiceInterface {

  // DI-1. Field Injection - чрез добавяне на анотацията @Autowired над полето (не трябва да е final)
  private final DeliveryRepository deliveryRepository;
  private final CourierRepository courierRepository;
  private final WarehouseRepository warehouseRepository;

  // TODO: Завършете имплементацията на Constructor Injection-а на DeliveryService
  // * В момента полето 'deliveryEstimator' е дефинирано, но не се иницилизира в конструктора
  // * Добавете липсващото присвояване в конструктора
  private DeliveryEstimator deliveryEstimator;

  // DI-2. Constructor Injection - чрез добавяне на конструктор (препоръчителен начин в Spring)
  // - добра практика dependency-тата при Constructor Injection да са final
  // - сигурни сме, че стойността се задава само веднъж — в конструктора
  // - след създаване на обекта не може да бъде променяна
  // - зависимости се задават при създаване
  public DeliveryService(
      DeliveryRepository deliveryRepository,
      CourierRepository courierRepository,
      WarehouseRepository warehouseRepository,
      DeliveryEstimator deliveryEstimator) {
    this.deliveryRepository = deliveryRepository;
    this.courierRepository = courierRepository;
    this.warehouseRepository = warehouseRepository;
  }

  // DI-3. Setter Injection - чрез добавяне на setter метод (използва се за
  // опционални/незадължителни зависимости)
  //    public void setCourierRepository(CourierRepository courierRepository) {
  //        this.courierRepository = courierRepository;
  //    }

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

    // Довършване на имплементацията на метода deliveryEstimator.estimateArrivalTime(Delivery delivery);
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
    if (CREATED.equals(currentStatus)
        || ASSIGNED.equals(currentStatus) && CANCELED.equals(newStatus)) {
      return true;
    }

    return false;
  }

}
