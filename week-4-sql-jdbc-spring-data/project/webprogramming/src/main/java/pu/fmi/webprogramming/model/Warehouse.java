package pu.fmi.webprogramming.model;

import jakarta.persistence.*;

@Entity
@Table(name = "WAREHOUSE")
public class Warehouse {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "ID")
  private Long id;

  @Column(name = "CITY", nullable = false, length = 100, unique = true)
  private String city;

  public Warehouse() {}

  public Warehouse(Long id, String city) {
    this.id = id;
    this.city = city;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getCity() {
    return city;
  }

  public void setCity(String city) {
    this.city = city;
  }
}
