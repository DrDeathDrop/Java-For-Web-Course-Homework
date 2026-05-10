package pu.fmi.webprogramming.model;

public class Courier {

  private Long id;
  private String firstName;
  private String lastName;
  private boolean available;
  private String city;

  public Courier(String firstName, String lastName, boolean available, String city) {
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
