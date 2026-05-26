package pu.fmi.webprogramming.contoller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import pu.fmi.webprogramming.exception.DeliveryCustomException;
import pu.fmi.webprogramming.model.Customer;
import pu.fmi.webprogramming.model.Delivery;
import pu.fmi.webprogramming.repository.CustomerJpaRepository;
import pu.fmi.webprogramming.service.DeliveryServiceInterface;

import java.util.ArrayList;
import java.util.List;

@Controller
public class DeliveryController {

  private final DeliveryServiceInterface deliveryService;
  private final CustomerJpaRepository customerRepository;


  public DeliveryController(DeliveryServiceInterface deliveryService, CustomerJpaRepository customerRepository) {
    this.deliveryService = deliveryService;
    this.customerRepository = customerRepository;
  }

  @RequestMapping(value = "/createDelivery", method = RequestMethod.GET)
  public String getCreateDeliveryPage(Model model) {
    model.addAttribute("customers", customerRepository.findAll());
    return "create-delivery.html";
  }

  @PostMapping("/createDelivery")
  public String createDelivery(@RequestParam Long customerId, Model model) {
    Customer customerFound = customerRepository.findById(customerId).orElse(null);

    if (customerFound == null) {
      throw new DeliveryCustomException("Customer with id: " + customerId + " not found");
    }

    Delivery delivery = deliveryService.createDelivery(customerFound);
    model.addAttribute("delivery", delivery);
    return "delivery-created.html";
  }

  @GetMapping("/getDeliveries")
  public ModelAndView getDeliveries() {
      ModelAndView modelAndView = new ModelAndView("deliveries");
      List<Delivery> deliveries = deliveryService.getAllDeliveries();
      modelAndView.setViewName("deliveries");
      modelAndView.addObject("deliveries", deliveries);
      return modelAndView;
  }
}
