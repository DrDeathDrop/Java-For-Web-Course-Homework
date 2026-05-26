package pu.fmi.webprogramming.model;

import jakarta.persistence.*;

import javax.naming.Name;

@Entity
@Table(name = "CUSTOMER")
public class Customer {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "FIRST_NAME", nullable = false, length = 100)
  private String firstName;

  @Column(name = "LAST_NAME", nullable = false, length = 100)
  private String lastName;

  @Column(name = "USERNAME", nullable = false, length = 100)
  private String username;

  @Column(name = "PHONE_NUMBER", length = 100)
  private String phoneNumber;

  @Column(name = "CITY", nullable = false, length = 100)
  private String city;

  public Customer(){

  }

  public Customer(
      Long id,
      String firstName,
      String lastName,
      String username,
      String phoneNumber,
      String city) {
    this.id = id;
    this.firstName = firstName;
    this.lastName = lastName;
    this.username = username;
    this.phoneNumber = phoneNumber;
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

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPhoneNumber() {
    return phoneNumber;
  }

  public void setPhoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
  }

  public String getCity() {
    return city;
  }

  public void setCity(String city) {
    this.city = city;
  }
}
