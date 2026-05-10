package pu.fmi.webprogramming;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import pu.fmi.webprogramming.model.Customer;
import pu.fmi.webprogramming.model.Delivery;
import pu.fmi.webprogramming.service.DeliveryServiceInterface;

import static pu.fmi.webprogramming.model.enums.DeliveryStatusEnum.IN_PROGRESS;

@SpringBootApplication
public class WebprogrammingApplication {

  private static final Logger LOG = LoggerFactory.getLogger(WebprogrammingApplication.class);

  public static void main(String[] args) {

    ApplicationContext springContainer =
        SpringApplication.run(WebprogrammingApplication.class, args);

    String[] springBeans = springContainer.getBeanDefinitionNames();

    for (String beanName : springBeans) {
      LOG.info("Bean: {}", beanName);
    }

    String myFirstBean = springContainer.getBean(String.class);
    String myFirstBeanFetchedByName = (String) springContainer.getBean("myFirstBean");

    LOG.info(myFirstBean);
    LOG.info(myFirstBeanFetchedByName);

    DeliveryServiceInterface deliveryServiceInterface =
        springContainer.getBean(DeliveryServiceInterface.class);

    LOG.info("Deliver Service: {}", deliveryServiceInterface);

    DeliveryServiceInterface deliveryServiceInterface2 =
        springContainer.getBean(DeliveryServiceInterface.class);

    LOG.info("Deliver Service 2: {}", deliveryServiceInterface2);

    Customer customer =
        new Customer(1L,
                "Petar",
                "Ivanov",
                "petar.ivanov",
                "0123456789", "Plovdiv");

    Delivery delivery = deliveryServiceInterface.createDelivery(customer);

    LOG.info("ID: {}", delivery.getId());
    LOG.info("Customer First Name: {}", delivery.getCustomer().getFirstName());
    LOG.info("Created At: {}", delivery.getCreatedAt());
    LOG.info("Courier First Name: {}", delivery.getCourier().getFirstName());

    boolean updated = deliveryServiceInterface.updateDeliveryStatus(1L, IN_PROGRESS);

    LOG.info("updated: {}", updated);

    //		AbstractApplicationContext abstractSpringContext
    //				= (AbstractApplicationContext) springContainer;
    //
    //		abstractSpringContext.close();
  }

  // Създаване на bean:
  // 1. <bean name="myBean" class="package.myBean
  // 2. @Bean трябва да се намира над метод - в клас, който е анотиран с @Configuration
  // 3. Използване на анотациите @Component, @Service, @Repository

  @Bean
  //	@Bean(name = "springBean")
  public String myFirstBean() {
    return "This is my first bean";
  }

//  @Bean
//  @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
//  public DeliveryServiceInterface deliveryService(
//      DeliveryRepository deliveryRepository,
//      CourierRepository courierRepository,
//      WarehouseRepository warehouseRepository) {
//    return new DeliveryService(deliveryRepository, courierRepository, warehouseRepository);
//  }
}
