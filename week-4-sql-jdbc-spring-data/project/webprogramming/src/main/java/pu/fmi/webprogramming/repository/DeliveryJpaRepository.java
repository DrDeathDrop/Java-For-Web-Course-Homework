package pu.fmi.webprogramming.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import pu.fmi.webprogramming.model.Delivery;
import pu.fmi.webprogramming.model.enums.DeliveryStatusEnum;

import java.util.List;

public interface DeliveryJpaRepository extends JpaRepository<Delivery, Long> {

  List<Delivery> findByDeliveryStatus(DeliveryStatusEnum status, Pageable pageable);

  // TODO: 1. Добавете query метод за извличане на всички доставки, свързани с конкретен клиент
  // Използвайте Spring Data JPA query method naming conventions (derived queries)
  // Методът трябва да поддържа pagination, за да се ограничава броят на резултатите

  // TODO: 2. Добавете query метод за извличане на доставки по комбинация от:
  // - статус на доставката
  // - клиент
  // Методът трябва да използва Spring Data JPA derived query syntax и да поддържа pagination

  // Примери:
  // * https://docs.spring.io/spring-data/jpa/reference/repositories/query-methods-details.html
  // * https://www.baeldung.com/spring-data-jpa-pagination-sorting
}
