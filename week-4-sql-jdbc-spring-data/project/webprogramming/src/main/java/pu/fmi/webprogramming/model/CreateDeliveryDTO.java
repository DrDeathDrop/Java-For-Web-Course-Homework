package pu.fmi.webprogramming.model;

public class CreateDeliveryDTO {

    private Long customerId;

    public CreateDeliveryDTO(Long customerId) {
        this.customerId = customerId;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }
}
