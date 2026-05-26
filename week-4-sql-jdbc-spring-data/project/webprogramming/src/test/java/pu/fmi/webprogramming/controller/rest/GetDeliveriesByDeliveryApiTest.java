package pu.fmi.webprogramming.controller.rest;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import pu.fmi.webprogramming.model.Customer;
import pu.fmi.webprogramming.model.Delivery;
import pu.fmi.webprogramming.model.enums.DeliveryStatusEnum;
import pu.fmi.webprogramming.repository.CustomerJpaRepository;
import pu.fmi.webprogramming.repository.DeliveryJpaRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = RANDOM_PORT)
@AutoConfigureWebTestClient
public class GetDeliveriesByDeliveryApiTest {

  @Autowired WebTestClient client;
  @Autowired private DeliveryJpaRepository deliveryRepository;
  @Autowired private CustomerJpaRepository customerRepository;

  private Customer customer;
  private Customer customer2;

  private final ParameterizedTypeReference<List<Delivery>> type =
      new ParameterizedTypeReference<>() {};

  @BeforeEach
  void setUp() {
    customer = customerRepository.findById(1L).get();
    customer2 = customerRepository.findById(2L).get();
  }

  @Test
  void shouldReturnDeliveries() {

    deliveryRepository.save(new Delivery());

    List<Delivery> response =
        client
            .get()
            .uri("/api/deliveries")
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(type)
            .returnResult()
            .getResponseBody();

    assertNotNull(response);
    assertEquals(1, response.size());
  }

  @Test
  void shouldReturnPaginatedDeliveries() {

    for (int i = 0; i < 15; i++) {
      Delivery d = new Delivery();
      d.setCustomer(customer);
      d.setCreatedAt(LocalDateTime.now());
      deliveryRepository.save(d);
    }

    List<Delivery> response =
        client
            .get()
            .uri(
                uriBuilder ->
                    uriBuilder
                        .path("/api/deliveries")
                        .queryParam("page", 0)
                        .queryParam("size", 10)
                        .build())
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(type)
            .returnResult()
            .getResponseBody();

    assertNotNull(response);
    assertEquals(10, response.size());
  }

  @Test
  void shouldFilterByStatus() {

    Delivery d1 = new Delivery();
    d1.setCustomer(customer);
    d1.setDeliveryStatus(DeliveryStatusEnum.DELIVERED);

    Delivery d2 = new Delivery();
    d2.setCustomer(customer2);
    d2.setDeliveryStatus(DeliveryStatusEnum.IN_PROGRESS);

    deliveryRepository.saveAll(List.of(d1, d2));

    List<Delivery> response =
        client
            .get()
            .uri(
                uriBuilder ->
                    uriBuilder.path("/api/deliveries").queryParam("status", "DELIVERED").build())
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(type)
            .returnResult()
            .getResponseBody();

    assertNotNull(response);

    assertEquals(1, response.size());
    assertTrue(
        response.stream().allMatch(d -> d.getDeliveryStatus() == DeliveryStatusEnum.DELIVERED));
  }

  @Test
  void shouldFilterByCustomerId() {

    Delivery d1 = new Delivery();
    d1.setDeliveryStatus(DeliveryStatusEnum.CREATED);
    d1.setCustomer(customer);

    Delivery d2 = new Delivery();
    d2.setDeliveryStatus(DeliveryStatusEnum.DELIVERED);
    d2.setCustomer(customer2);

    deliveryRepository.saveAll(List.of(d1, d2));

    List<Delivery> response =
        client
            .get()
            .uri(
                uriBuilder ->
                    uriBuilder
                        .path("/api/deliveries")
                        .queryParam("customerId", customer.getId())
                        .build())
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(type)
            .returnResult()
            .getResponseBody();

    assertNotNull(response);
    assertEquals(1, response.size());
    assertEquals(customer.getId(), response.get(0).getCustomer().getId());
  }

  @Test
  void shouldSortByCreatedAtDescByDefault() {

    Delivery old = new Delivery();
    old.setCustomer(customer);
    old.setCreatedAt(LocalDateTime.now().minusDays(5));

    Delivery newD = new Delivery();
    newD.setCustomer(customer);
    newD.setCreatedAt(LocalDateTime.now());

    deliveryRepository.saveAll(List.of(old, newD));

    List<Delivery> response =
        client
            .get()
            .uri(uriBuilder -> uriBuilder.path("/api/deliveries").build())
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(type)
            .returnResult()
            .getResponseBody();

    assertNotNull(response);

    assertTrue(response.get(0).getCreatedAt().isAfter(response.get(1).getCreatedAt()));
  }

  @Test
  void shouldCombineFilters() {

    Delivery delivery = new Delivery();
    delivery.setCustomer(customer2);
    delivery.setDeliveryStatus(DeliveryStatusEnum.DELIVERED);
    delivery.setCreatedAt(LocalDateTime.now());
    deliveryRepository.save(delivery);

    Delivery delivery2 = new Delivery();
    delivery2.setCustomer(customer);
    delivery2.setDeliveryStatus(DeliveryStatusEnum.IN_PROGRESS);
    delivery2.setCreatedAt(LocalDateTime.now());
    deliveryRepository.save(delivery2);

    for (int i = 0; i < 4; i++) {
      Delivery d = new Delivery();
      d.setCustomer(customer);
      d.setDeliveryStatus(DeliveryStatusEnum.DELIVERED);
      d.setCreatedAt(LocalDateTime.now().minusMinutes(i));
      deliveryRepository.save(d);
    }

    List<Delivery> response =
        client
            .get()
            .uri(
                uriBuilder ->
                    uriBuilder
                        .path("/api/deliveries")
                        .queryParam("page", 0)
                        .queryParam("size", 5)
                        .queryParam("status", "DELIVERED")
                        .queryParam("customerId", customer.getId())
                        .build())
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(type)
            .returnResult()
            .getResponseBody();

    assertNotNull(response);
    assertEquals(4, response.size());
    assertTrue(
        response.stream().allMatch(d -> d.getDeliveryStatus() == DeliveryStatusEnum.DELIVERED));
    assertTrue(
            response.stream()
                    .allMatch(d -> d.getCustomer().getId().equals(customer.getId()))
    );
  }

  @AfterEach
  void cleanUp() {
    deliveryRepository.deleteAll();
  }
}
