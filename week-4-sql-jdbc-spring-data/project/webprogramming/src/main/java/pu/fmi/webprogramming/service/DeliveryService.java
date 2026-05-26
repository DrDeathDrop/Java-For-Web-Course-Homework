package pu.fmi.webprogramming.service;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import pu.fmi.webprogramming.exception.DeliveryCustomException;
import pu.fmi.webprogramming.model.*;
import pu.fmi.webprogramming.model.enums.DeliveryStatusEnum;
import pu.fmi.webprogramming.repository.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static pu.fmi.webprogramming.model.enums.DeliveryStatusEnum.*;

@Service
public class DeliveryService implements DeliveryServiceInterface {

  private final DeliveryJpaRepository deliveryRepository;
  private final CourierJpaRepository courierRepository;
  private final WarehouseJpaRepository warehouseRepository;
  private final DeliveryEstimator deliveryEstimator;

  public DeliveryService(
      DeliveryJpaRepository deliveryRepository,
      CourierJpaRepository courierRepository,
      WarehouseJpaRepository warehouseRepository,
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

    Optional<Courier> courierOptional = courierRepository.findFirstByAvailableTrue();
    Warehouse warehouse = warehouseRepository.findByCity(customer.getCity());

    delivery.setCreatedAt(LocalDateTime.now());
    delivery.setCustomer(customer);
    delivery.setDeliveredAt(null);
    delivery.setWarehouse(warehouse);

    if (courierOptional.isPresent()) {
      delivery.setCourier(courierOptional.get());
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

    Delivery delivery = deliveryRepository.findById(id).orElse(null);

    if (delivery == null) {
      return false;
    }

    if (isStatusValid(delivery.getDeliveryStatus(), newStatus)) {
      delivery.setDeliveryStatus(newStatus);
      deliveryRepository.save(delivery);
      return true;
    }

    return false;
  }

  @Override
  public List<Delivery> getAllDeliveries() {
    return deliveryRepository.findAll();
  }

  @Override
  public List<Delivery> getDeliveriesBy(DeliveryFilter filter) {

    // TODO: Довършване на имплементацията на метода
    // (използвайте логика за филтриране, pagination и сортиране)

    // * Създай Pageable обект от подадения филтър:
    //    → page (номер на страница)
    //    → size (размер на страница)
    //    * Създайте Sort обект oт подадените във филтъра:
    //        → sortBy (поле за сортиране) - ако не е подадено, то по подразбиране трябва да е createdBy
    //        → direction (asc / desc) - ако не е подадено, то по подразбиране трябва да е низходящо

    // * Имайте предвид всички възможни случаи за филтриране:
    //    → Ако няма подадени филтри (status и customerId са null):
    //       - върни всички доставки
    //    → Ако е подаден само customerId:
    //       - върни доставки само за този клиент
    //    → Ако е подаден само status:
    //       - върни доставки само със съответния статус
    //    → Ако са подадени и двата филтъра:
    //       - върни доставки, които отговарят едновременно на status и customerId

    // * Уверите се, че резултатът винаги е ограничен чрез Pageable
    // * Уверите се, че резултатът е сортиран според подадения sortBy и direction
    // * Методът трябва да връща само списък (List<Delivery>), без Page обект

    // ВАЖНО:
    // * Всички предоставени Unit тестове (GetDeliveriesByDeliveryApiTest) трябва да минават успешно
    // * Не променяйте сигнатурата на метода
    // * Не променяй поведението на API-то

    Sort sort = null;
    Pageable pageable = PageRequest.of(-1, -1, sort);

    if (filter.getStatus() != null && filter.getCustomerId() == null) {
      return deliveryRepository.findByDeliveryStatus(filter.getStatus(), pageable);
    }

    return null;
  }

  @Override
  public Delivery assignCourier(Long id, Long courierId) {

    Delivery delivery =
        deliveryRepository
            .findById(id)
            .orElseThrow(() -> new DeliveryCustomException("Delivery not found"));
    Courier selectedCourier =
        courierRepository
            .findById(courierId)
            .orElseThrow(() -> new DeliveryCustomException("Courier not found"));

    if (!selectedCourier.isAvailable()) {
      throw new DeliveryCustomException("Courier is not available");
    }

    delivery.setCourier(selectedCourier);
    delivery.setDeliveryStatus(DeliveryStatusEnum.ASSIGNED);
    delivery.setEstimatedArrivalAt(deliveryEstimator.estimateArrivalTime(delivery));

    selectedCourier.setAvailable(false);

    return deliveryRepository.save(delivery);
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
    if ((CREATED.equals(currentStatus) || ASSIGNED.equals(currentStatus))
        && CANCELED.equals(newStatus)) {
      return true;
    }

    return false;
  }
}
