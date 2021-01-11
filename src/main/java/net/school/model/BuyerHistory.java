package net.school.model;

public class BuyerHistory {
   private Long buyerId;
   private Long productId;
   private Long dayId;

   public BuyerHistory() {}

   public void setBuyerId(Long buyerId) {
      this.buyerId = buyerId;
   }

   public void setProductId(Long productId) {
      this.productId = productId;
   }

   public void setDayId(Long dayId) {
      this.dayId = dayId;
   }

   public Long getDayId() {
      return dayId;
   }

   public Long getBuyerId() {
      return buyerId;
   }

   public Long getProductId() {
      return productId;
   }
}
