package pu.fmi.webprogramming.controller.rest;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import pu.fmi.webprogramming.model.Courier;
import pu.fmi.webprogramming.model.CreateDeliveryDTO;
import pu.fmi.webprogramming.model.Delivery;
import pu.fmi.webprogramming.model.enums.DeliveryStatusEnum;
import pu.fmi.webprogramming.repository.CourierJpaRepository;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@AutoConfigureWebTestClient
public class DeliveryApiTest {

  @Autowired WebTestClient client;

  @Autowired private CourierJpaRepository courierRepository;

  private Delivery testDelivery;
  private Courier availableCourier;

  @BeforeEach
  void setup() {
    // Създаване на доставка, която ще бъде използване в тестовете
    testDelivery =
        client
            .post()
            .uri("/api/deliveries/customer")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(new CreateDeliveryDTO(1L))
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(Delivery.class)
            .returnResult()
            .getResponseBody();

    // Взимане на наличен куриер
    availableCourier = courierRepository.findFirstByAvailableTrue().get();
  }

  @Test
  void testAssignAvailableCourier() {
    client
        .put()
        .uri(
            uriBuilder ->
                uriBuilder
                    .path("/api/deliveries/{id}/courier")
                    .queryParam("courierId", availableCourier.getId())
                    .build(testDelivery.getId()))
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$.deliveryStatus")
        .isEqualTo(DeliveryStatusEnum.ASSIGNED.toString())
        .jsonPath("$.courier.id")
        .isEqualTo(availableCourier.getId())
        .jsonPath("$.courier.available")
        .isEqualTo(false)
        .jsonPath("$.estimatedArrivalAt")
        .exists();
  }

  @Test
  void testAssignCourierDeliveryNotFound() {
    client
        .put()
        .uri(
            uriBuilder ->
                uriBuilder
                    .path("/api/deliveries/{id}/courier")
                    .queryParam("courierId", availableCourier.getId())
                    .build(999L)) // невалидно delivery
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus()
        .is4xxClientError()
        .expectBody()
        .jsonPath("$.message")
        .isEqualTo("Delivery not found");
  }

  @Test
  void testAssignCourierNotFound() {
    client
        .put()
        .uri(
            uriBuilder ->
                uriBuilder
                    .path("/api/deliveries/{id}/courier")
                    .queryParam("courierId", 999L) // невалиден courier
                    .build(testDelivery.getId()))
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus()
        .is4xxClientError()
        .expectBody()
        .jsonPath("$.message")
        .isEqualTo("Courier not found");
  }

  @Test
  void testAssignCourierNotAvailable() {
    // Маркиране на куриера като зает
    availableCourier.setAvailable(false);
    courierRepository.saveAndFlush(availableCourier);

    client
        .put()
        .uri(
            uriBuilder ->
                uriBuilder
                    .path("/api/deliveries/{id}/courier")
                    .queryParam("courierId", availableCourier.getId())
                    .build(testDelivery.getId()))
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus()
        .is4xxClientError()
        .expectBody()
        .jsonPath("$.message")
        .isEqualTo("Courier is not available");
  }

  @AfterEach
  void cleanup() {
    availableCourier.setAvailable(true);
    courierRepository.saveAndFlush(availableCourier);
  }

}
