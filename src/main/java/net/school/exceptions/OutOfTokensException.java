package net.school.exceptions;

public class OutOfTokensException extends Exception{
   private String msg;

   public OutOfTokensException(String msg) {
      this.msg = msg;
   }

   @Override
   public String getMessage() {
      return msg;
   }
}
