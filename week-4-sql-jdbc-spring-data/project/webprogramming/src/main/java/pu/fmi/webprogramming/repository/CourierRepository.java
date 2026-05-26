package pu.fmi.webprogramming.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import pu.fmi.webprogramming.model.Courier;
import pu.fmi.webprogramming.model.Customer;

import java.util.ArrayList;
import java.util.List;

@Repository
public class CourierRepository {

  private final JdbcTemplate jdbcTemplate;

  public CourierRepository(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  public Courier findAvailableCourier() {
    Courier availableCourier = jdbcTemplate.queryForObject(
            "SELECT * FROM COURIER WHERE AVAILABLE = true LIMIT 1",
            (rs, rowNum) ->
                    new Courier(
                            rs.getLong("ID"),
                            rs.getString("FIRST_NAME"),
                            rs.getString("LAST_NAME"),
                            rs.getBoolean("AVAILABLE"),
                            rs.getString("CITY")
                    )
            );

    return availableCourier;
  }

  public List<Courier> getAllCouriers() {
    return jdbcTemplate.query(
            "SELECT * FROM COURIER",
            (rs, rowNum) ->
                    new Courier(
                            rs.getLong("ID"),
                            rs.getString("FIRST_NAME"),
                            rs.getString("LAST_NAME"),
                            rs.getBoolean("AVAILABLE"),
                            rs.getString("CITY")
                    )
    );
  }
}
