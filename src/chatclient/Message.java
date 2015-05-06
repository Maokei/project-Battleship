/*    */ package chatclient;
/*    */ 
/*    */ import java.util.Scanner;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class Message
/*    */ {
/*    */   private static final String STX = "\002";
/*    */   protected String iSender;
/*    */   protected String iMess;
/*    */   
/*    */   public Message() {}
/*    */   
/*    */   public Message(String aSender, String aMess)
/*    */   {
/* 21 */     set(aSender, aMess);
/*    */   }
/*    */   
/*    */   public void set(String aSender, String aMess) {
/* 25 */     this.iSender = aSender;
/* 26 */     this.iMess = aMess;
/*    */   }
/*    */   
/*    */   public String getSender() {
/* 30 */     return this.iSender;
/*    */   }
/*    */   
/*    */   public String getMessage() {
/* 34 */     return this.iMess;
/*    */   }
/*    */   
/*    */   public void decode(String msgStr)
/*    */   {
/* 39 */     Scanner scanner = new Scanner(msgStr);
/* 40 */     scanner.useDelimiter("\002");
/* 41 */     this.iSender = scanner.next();
/* 42 */     this.iMess = scanner.next();
/* 43 */     scanner.close();
/*    */   }
/*    */   
/*    */   public String encode()
/*    */   {
/* 48 */     return this.iSender + "\002" + this.iMess;
/*    */   }
/*    */   
/*    */ 
/*    */   public String toString()
/*    */   {
/* 54 */     return this.iSender + "> " + this.iMess;
/*    */   }
/*    */ }


/* Location:              /home/maokei/Downloads/laboration8_lama1203_Lars.Maechel-KOMPLETTERING/ChatClient.jar!/chatclient/Message.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */