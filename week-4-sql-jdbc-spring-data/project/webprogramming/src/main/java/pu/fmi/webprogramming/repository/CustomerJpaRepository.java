package pu.fmi.webprogramming.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pu.fmi.webprogramming.model.Customer;

public interface CustomerJpaRepository extends JpaRepository<Customer, Long> {}
