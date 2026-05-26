package pu.fmi.webprogramming.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import pu.fmi.webprogramming.model.Courier;
import pu.fmi.webprogramming.model.Customer;
import pu.fmi.webprogramming.model.Warehouse;

import java.util.ArrayList;
import java.util.List;

@Repository
public class WarehouseRepository {

  private final JdbcTemplate jdbcTemplate;

  public WarehouseRepository(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  public Warehouse findByCustomerCity(Customer customer) {
    String sql = "SELECT * FROM WAREHOUSE WHERE CITY = '" + customer.getCity() + "'";
    return jdbcTemplate.queryForObject(
            sql,
            (rs, rowNum) ->
                    new Warehouse(
                            rs.getLong("ID"),
                            rs.getString("CITY")
                    )
    );

  }
}
