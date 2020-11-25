package net.school.model;

public class Day {
   private Long id;
   private String dayName;

   public Day(){}

   public Day(Long id, String dayName) {
      this.id = id;
      this.dayName = dayName;
   }

   public void setId(Long id) {
      this.id = id;
   }

   public void setDayName(String dayName) {
      this.dayName = dayName;
   }

   public Long getId() {
      return id;
   }

   public String getDayName() {
      return dayName;
   }

   @Override
   public String toString() {
      return "Day{" +
              "id=" + id +
              ", dayName='" + dayName + '\'' +
              '}';
   }
}
