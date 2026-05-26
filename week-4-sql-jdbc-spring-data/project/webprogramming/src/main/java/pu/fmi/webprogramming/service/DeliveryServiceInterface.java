package pu.fmi.webprogramming.service;

import org.springframework.data.domain.Page;
import pu.fmi.webprogramming.model.Customer;
import pu.fmi.webprogramming.model.Delivery;
import pu.fmi.webprogramming.model.DeliveryFilter;
import pu.fmi.webprogramming.model.enums.DeliveryStatusEnum;

import java.time.LocalDate;
import java.util.List;

public interface DeliveryServiceInterface {

  Delivery createDelivery(Customer customer);

  boolean updateDeliveryStatus(Long id, DeliveryStatusEnum status);

  List<Delivery> getAllDeliveries();

  List<Delivery> getDeliveriesBy(DeliveryFilter deliveryFilter);

  Delivery assignCourier(Long id, Long courierId);
}
