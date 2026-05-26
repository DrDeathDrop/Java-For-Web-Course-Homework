package pu.fmi.webprogramming.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import pu.fmi.webprogramming.model.*;
import pu.fmi.webprogramming.model.enums.DeliveryStatusEnum;
import pu.fmi.webprogramming.repository.*;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeliveryServiceTest {

  @Mock private DeliveryJpaRepository deliveryRepository;
  @Mock private CourierJpaRepository courierRepository;
  @Mock private WarehouseJpaRepository warehouseRepository;

  @Spy private DeliveryEstimator deliveryEstimator;

  private DeliveryService deliveryService;

  @BeforeEach
  void setUp() {
    deliveryService =
        new DeliveryService(
            deliveryRepository, courierRepository, warehouseRepository, deliveryEstimator);
  }

  @Test
  @DisplayName("Градът съвпада и има наличен куриер -> 1 ден доставка")
  void shouldCalculateCorrectEstimationForLocalCourier() {
    Customer customer = new Customer(1L, "Ivan",
            "Ivanov", "ivan_v",
            "088", "Plovdiv");
    Warehouse warehouse = new Warehouse(1L, "Plovdiv");
    Courier courier = new Courier(1L, "Ivan", "Ivanov", true, "Plovdiv");

    when(warehouseRepository.findByCity(customer.getCity())).thenReturn(warehouse);
    when(courierRepository.findFirstByAvailableTrue()).thenReturn(Optional.of(courier));

    Delivery result = deliveryService.createDelivery(customer);

    LocalDateTime expectedDate = result.getCreatedAt().plusDays(1);
    assertEquals(expectedDate, result.getEstimatedArrivalAt());
    assertEquals(DeliveryStatusEnum.ASSIGNED, result.getDeliveryStatus());
  }

  @Test
  @DisplayName("Градът съвпада, но няма свободен куриер -> 3 дни доставка (1 базов + 2 закъснение)")
  void shouldApplyDelayWhenLocalCourierIsMissing() {
    Customer customer = new Customer(1L, "Ivan",
            "Ivanov", "ivan_v",
            "088", "Plovdiv");
    Warehouse warehouse = new Warehouse(1L, "Plovdiv");

    when(warehouseRepository.findByCity(customer.getCity())).thenReturn(warehouse);
    when(courierRepository.findFirstByAvailableTrue()).thenReturn(Optional.empty());

    Delivery result = deliveryService.createDelivery(customer);

    LocalDateTime expectedDate = result.getCreatedAt().plusDays(3);
    assertEquals(expectedDate, result.getEstimatedArrivalAt());
    assertEquals(DeliveryStatusEnum.CREATED, result.getDeliveryStatus());
  }

  @Test
  @DisplayName("Различни градове и има наличен куриер -> 3 дни доставка")
  void shouldCalculateCorrectEstimationForIntercityCourier() {
    Customer customer = new Customer(2L, "Anna",
            "Petrova", "anna_p",
            "089", "Varna");
    Warehouse warehouse = new Warehouse(2L, "Sofia");
    Courier courier = new Courier(2L ,"Dragan", "Petkanov", true, "Sofia");

    when(warehouseRepository.findByCity(customer.getCity())).thenReturn(warehouse);
    when(courierRepository.findFirstByAvailableTrue()).thenReturn(Optional.of(courier));

    Delivery result = deliveryService.createDelivery(customer);

    LocalDateTime expectedDate = result.getCreatedAt().plusDays(3);
    assertEquals(expectedDate, result.getEstimatedArrivalAt());
    assertEquals(DeliveryStatusEnum.ASSIGNED, result.getDeliveryStatus());
  }

  @Test
  @DisplayName("Различни градове и няма свободен куриер -> 5 дни доставка (3 базови + 2 закъснение)")
  void shouldApplyDelayWhenIntercityCourierIsMissing() {
    Customer customer = new Customer(1L, "Ivan",
            "Ivanov", "ivan_v",
            "088", "Varna");
    Warehouse warehouse = new Warehouse(1L, "Sofia");

    when(warehouseRepository.findByCity(customer.getCity())).thenReturn(warehouse);
    when(courierRepository.findFirstByAvailableTrue()).thenReturn(Optional.empty());

    Delivery result = deliveryService.createDelivery(customer);

    LocalDateTime expected = result.getCreatedAt().plusDays(5);
    assertEquals(expected, result.getEstimatedArrivalAt());
    assertEquals(DeliveryStatusEnum.CREATED, result.getDeliveryStatus());
  }

  @Test
  @DisplayName("Dependency Injection Тест: Трябва да се използва инжектираният deliveryEstimator")
  void shouldObserveInjectedBeanBehavior() {
    Customer customer = new Customer(1L, "Ivan",
            "Ivanov", "ivan_v",
            "088", "Varna");
    Warehouse warehouse = new Warehouse(1L, "Sofia");

    when(warehouseRepository.findByCity(customer.getCity())).thenReturn(warehouse);
    when(courierRepository.findFirstByAvailableTrue()).thenReturn(Optional.empty());

    deliveryService.createDelivery(customer);

    verify(deliveryEstimator, atLeastOnce()).estimateArrivalTime(any());
  }
}
