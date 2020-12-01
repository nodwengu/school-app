package net.school.exceptions;

public class InputRequiredException extends Exception {
   private String msg;

   public InputRequiredException(String msg) {
      this.msg = msg;
   }

   @Override
   public String getMessage() {
      return msg;
   }
}
