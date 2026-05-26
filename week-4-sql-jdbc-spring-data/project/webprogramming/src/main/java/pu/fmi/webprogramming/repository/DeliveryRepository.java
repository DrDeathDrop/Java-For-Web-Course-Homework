package pu.fmi.webprogramming.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import pu.fmi.webprogramming.model.Delivery;
import pu.fmi.webprogramming.model.enums.DeliveryStatusEnum;

import java.util.List;

@Repository
public class DeliveryRepository {

  private final JdbcTemplate jdbcTemplate;

  public DeliveryRepository(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  public void save(Delivery delivery) {
    jdbcTemplate.update(
        "INSERT INTO DELIVERY ("
            + "CREATED_AT, "
            + "CUSTOMER_ID, "
            + "COURIER_ID, "
            + "WAREHOUSE_ID, DELIVERY_STATUS, ESTIMATED_ARRIVAL_AT )"
            + " VALUES (?, ?, ?, ?, ?, ?)",
        delivery.getCreatedAt(),
        delivery.getCustomer().getId(),
        delivery.getCourier().getId(),
        delivery.getWarehouse().getId(),
        delivery.getDeliveryStatus().toString(),
        delivery.getEstimatedArrivalAt());
  }

  public Delivery findById(Long id) {
    return jdbcTemplate.queryForObject(
        "SELECT * FROM DELIVERY WHERE ID = ?",
        (rs, rowNum) -> {
          Delivery delivery = new Delivery();
          delivery.setId(rs.getLong("ID"));
          delivery.setDeliveryStatus(DeliveryStatusEnum.valueOf(rs.getString("DELIVERY_STATUS")));
          delivery.setCreatedAt(rs.getTimestamp("CREATED_AT").toLocalDateTime());
          return delivery;
        },
        id);
  }

  public List<Delivery> findAllDeliveries() {
    return null;
  }
}
