package pu.fmi.webprogramming.service;

import org.springframework.stereotype.Component;
import pu.fmi.webprogramming.model.Delivery;

import java.time.LocalDateTime;

@Component
public class DeliveryEstimator {

  public LocalDateTime estimateArrivalTime(Delivery delivery) {

    if (delivery == null || delivery.getCreatedAt() == null
            || delivery.getCustomer() == null || delivery.getCustomer().getCity() == null
            || delivery.getWarehouse() == null || delivery.getWarehouse().getCity() == null) {
      return null;
    }

    LocalDateTime estimated = delivery.getCreatedAt();

    if (delivery.getWarehouse().getCity().equalsIgnoreCase(delivery.getCustomer().getCity())) {
      estimated = estimated.plusDays(1);
    } else {
      estimated = estimated.plusDays(3);
    }

    if (delivery.getCourier() == null) {
      estimated = estimated.plusDays(2);
    }

    return estimated;
  }
}