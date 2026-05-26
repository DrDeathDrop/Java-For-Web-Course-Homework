package pu.fmi.webprogramming.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pu.fmi.webprogramming.model.Courier;

import java.util.Optional;

public interface CourierJpaRepository extends JpaRepository<Courier, Long> {

    // SELECT * FROM COURIER WHERE ABVAILABLE = TRUE LIMIT 1
    Optional<Courier> findFirstByAvailableTrue();
}
