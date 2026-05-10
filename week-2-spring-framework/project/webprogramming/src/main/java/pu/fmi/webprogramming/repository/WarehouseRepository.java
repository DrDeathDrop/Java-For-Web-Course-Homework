package pu.fmi.webprogramming.repository;

import org.springframework.stereotype.Repository;
import pu.fmi.webprogramming.model.Customer;
import pu.fmi.webprogramming.model.Warehouse;

import java.util.ArrayList;
import java.util.List;

@Repository
public class WarehouseRepository {

  private List<Warehouse> warehouses = new ArrayList<>();

  public WarehouseRepository() {
    warehouses.add(new Warehouse(1L, "Plovdiv"));
    warehouses.add(new Warehouse(2L, "Sofia"));
  }

  public Warehouse findByCustomerCity(Customer customer) {
    Warehouse foundWarehouse =
        warehouses.stream()
            .filter(warehouse -> warehouse.getCity().equals(customer.getCity()))
            .findFirst()
            .orElse(warehouses.get(1));

    return foundWarehouse;
  }
}
