package pu.fmi.webprogramming.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import pu.fmi.webprogramming.model.Customer;
import pu.fmi.webprogramming.model.Warehouse;

@Repository
public class CustomerRepository {

    private final JdbcTemplate jdbcTemplate;

    public CustomerRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Customer findById(Long id) {
        return jdbcTemplate.queryForObject(
                "SELECT * FROM CUSTOMER WHERE ID = ?",
                (rs, rowNum) ->
                        new Customer(
                                rs.getLong("ID"),
                                rs.getString("FIRST_NAME"),
                                rs.getString("LAST_NAME"),
                                rs.getString("USERNAME"),
                                rs.getString("PHONE_NUMBER"),
                                rs.getString("CITY")
                        ),
                id
        );
    }

}
