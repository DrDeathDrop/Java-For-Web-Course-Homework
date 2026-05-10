package pu.fmi.webprogramming.service;

import pu.fmi.webprogramming.model.Customer;
import pu.fmi.webprogramming.model.Delivery;
import pu.fmi.webprogramming.model.enums.DeliveryStatusEnum;

public interface DeliveryServiceInterface {

  Delivery createDelivery(Customer customer);

  boolean updateDeliveryStatus(Long id, DeliveryStatusEnum status);
}
