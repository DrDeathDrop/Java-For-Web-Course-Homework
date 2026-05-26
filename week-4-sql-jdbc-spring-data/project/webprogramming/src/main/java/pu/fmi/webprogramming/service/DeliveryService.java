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

    String sortField = (filter.getSortBy() != null && !filter.getSortBy().isBlank())
            ? filter.getSortBy()
            : "createdAt";

    Sort.Direction sortDirection = (filter.getDirection() != null
            && filter.getDirection().equalsIgnoreCase("asc"))
            ? Sort.Direction.ASC
            : Sort.Direction.DESC;

    Sort sort = Sort.by(sortDirection, sortField);
    Pageable pageable = PageRequest.of(filter.getPage(), filter.getSize(), sort);

    boolean hasStatus     = filter.getStatus() != null;
    boolean hasCustomerId = filter.getCustomerId() != null;

    if (!hasStatus && !hasCustomerId) {
      return deliveryRepository.findAll(pageable).getContent();
    }

    if (hasStatus && !hasCustomerId) {
      return deliveryRepository.findByDeliveryStatus(filter.getStatus(), pageable);
    }

    if (!hasStatus) {
      return deliveryRepository.findByCustomerId(filter.getCustomerId(), pageable);
    }

    return deliveryRepository.findByDeliveryStatusAndCustomerId(
            filter.getStatus(), filter.getCustomerId(), pageable);
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
      return (CREATED.equals(currentStatus) || ASSIGNED.equals(currentStatus))
              && CANCELED.equals(newStatus);
  }
}
