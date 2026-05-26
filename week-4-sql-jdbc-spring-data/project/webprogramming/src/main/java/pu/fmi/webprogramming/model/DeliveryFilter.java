package pu.fmi.webprogramming.model;

import pu.fmi.webprogramming.model.enums.DeliveryStatusEnum;

public class DeliveryFilter {

  private int page = 0;
  private int size = 10;
  private String sortBy;
  private String direction;
  private Long customerId;
  private DeliveryStatusEnum status;

  public int getPage() {
    return page;
  }

  public void setPage(int page) {
    this.page = page;
  }

  public int getSize() {
    return size;
  }

  public void setSize(int size) {
    this.size = size;
  }

  public String getSortBy() {
    return sortBy;
  }

  public void setSortBy(String sortBy) {
    this.sortBy = sortBy;
  }

  public String getDirection() {
    return direction;
  }

  public void setDirection(String direction) {
    this.direction = direction;
  }

  public Long getCustomerId() {
    return customerId;
  }

  public void setCustomerId(Long customerId) {
    this.customerId = customerId;
  }

  public DeliveryStatusEnum getStatus() {
    return status;
  }

  public void setStatus(DeliveryStatusEnum status) {
    this.status = status;
  }
}
