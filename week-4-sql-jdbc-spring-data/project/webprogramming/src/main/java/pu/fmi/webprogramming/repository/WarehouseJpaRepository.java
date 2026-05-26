package pu.fmi.webprogramming.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pu.fmi.webprogramming.model.Warehouse;

public interface WarehouseJpaRepository extends JpaRepository<Warehouse, Long> {

    Warehouse findByCity(String city);

}
