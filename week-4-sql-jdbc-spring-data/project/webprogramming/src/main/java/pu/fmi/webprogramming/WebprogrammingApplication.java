package pu.fmi.webprogramming;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import pu.fmi.webprogramming.service.DeliveryServiceInterface;

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

    DeliveryServiceInterface deliveryServiceInterface =
        springContainer.getBean(DeliveryServiceInterface.class);

    LOG.info("Deliver Service: {}", deliveryServiceInterface);

    DeliveryServiceInterface deliveryServiceInterface2 =
        springContainer.getBean(DeliveryServiceInterface.class);

    LOG.info("Deliver Service 2: {}", deliveryServiceInterface2);

    //		AbstractApplicationContext abstractSpringContext
    //				= (AbstractApplicationContext) springContainer;
    //
    //		abstractSpringContext.close();
  }
}
