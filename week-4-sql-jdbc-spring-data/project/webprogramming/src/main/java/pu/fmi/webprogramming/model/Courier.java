package pu.fmi.webprogramming.model;

import jakarta.persistence.*;

@Entity
@Table(name = "COURIER")
public class Courier {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "FIRST_NAME", nullable = false, length = 100)
  private String firstName;

  @Column(name = "LAST_NAME", nullable = false, length = 100)
  private String lastName;

  @Column(name = "AVAILABLE")
  private boolean available;

  @Column(name = "CITY")
  private String city;

  public Courier() {}

  public Courier(Long id, String firstName, String lastName, boolean available, String city) {
    this.id = id;
    this.firstName = firstName;
    this.lastName = lastName;
    this.available = available;
    this.city = city;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public boolean isAvailable() {
    return available;
  }

  public void setAvailable(boolean available) {
    this.available = available;
  }

  public String getCity() {
    return city;
  }

  public void setCity(String city) {
    this.city = city;
  }
}
