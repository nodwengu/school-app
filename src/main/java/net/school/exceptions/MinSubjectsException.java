package net.school.exceptions;

public class MinSubjectsException extends Exception{
   private String msg;

   public MinSubjectsException(String msg) {
      this.msg = msg;
   }

   @Override
   public String getMessage() {
      return msg;
   }
}
