package pu.fmi.webprogramming.repository;

import org.springframework.stereotype.Repository;
import pu.fmi.webprogramming.exception.DeliveryCustomException;
import pu.fmi.webprogramming.model.Delivery;

import java.util.ArrayList;
import java.util.List;

@Repository
public class DeliveryRepository {

  private List<Delivery> deliveries;

  public DeliveryRepository() {
    deliveries = new ArrayList<>();
  }

  public void save(Delivery delivery) {
    if (delivery == null) {
      throw new DeliveryCustomException("Delivery cannot be null");
    }

    Long currentId = (long) (deliveries.size() + 1);
    delivery.setId(currentId);
    deliveries.add(delivery);
  }

  public Delivery findById(Long id) {
    for (Delivery delivery : deliveries) {
      if (delivery.getId().equals(id)) {
        return delivery;
      }
    }

    //        throw new DeliveryCustomException("Delivery with such id does not exist");
    return null;
  }
}
