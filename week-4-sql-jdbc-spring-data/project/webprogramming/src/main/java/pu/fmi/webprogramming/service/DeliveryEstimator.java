package pu.fmi.webprogramming.service;

import org.springframework.stereotype.Component;
import pu.fmi.webprogramming.exception.DeliveryCustomException;
import pu.fmi.webprogramming.model.Delivery;

import java.time.LocalDateTime;

@Component
public class DeliveryEstimator {

  public LocalDateTime estimateArrivalTime(Delivery delivery) {

    if (delivery == null) {
      throw new DeliveryCustomException("Delivery cannot be null");
    }

    LocalDateTime estimateAtTime = delivery.getCreatedAt();

    if (delivery.getWarehouse().getCity().equals(delivery.getCustomer().getCity())) {
      estimateAtTime = estimateAtTime.plusDays(1);
    } else {
      estimateAtTime = estimateAtTime.plusDays(3);
    }

    if (delivery.getCourier() == null) {
      estimateAtTime = estimateAtTime.plusDays(2);
    }

    return estimateAtTime;
  }
}
