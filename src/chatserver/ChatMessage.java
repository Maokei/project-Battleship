/*    */ package chatserver;
/*    */ 
/*    */ import java.util.Scanner;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ class ChatMessage
/*    */ {
/*    */   protected static final String STX = "\002";
/*    */   protected String encodedMess;
/*    */   protected String iMess;
/*    */   protected String iSender;
/*    */   
/*    */   public ChatMessage()
/*    */   {
/* 19 */     this.iSender = (this.iMess = this.encodedMess = null);
/*    */   }
/*    */   
/*    */   public ChatMessage(String s, String m) {
/* 23 */     this.iSender = s;
/* 24 */     this.iMess = m;
/* 25 */     this.encodedMess = (s + "\002" + m);
/*    */   }
/*    */   
/*    */   public ChatMessage(String msgStr)
/*    */   {
/* 30 */     decode(msgStr);
/*    */   }
/*    */   
/*    */   public String getSender()
/*    */   {
/* 35 */     return this.iSender;
/*    */   }
/*    */   
/*    */   public String getMessage() {
/* 39 */     return this.iMess;
/*    */   }
/*    */   
/*    */   public void decode(String msgStr) {
/* 43 */     Scanner scanner = new Scanner(msgStr);
/* 44 */     scanner.useDelimiter("\002");
/* 45 */     this.iSender = scanner.next();
/* 46 */     this.iMess = scanner.next();
/* 47 */     scanner.close();
/* 48 */     this.encodedMess = (this.iSender + "\002" + this.iMess);
/*    */   }
/*    */   
/*    */ 
/*    */   public String toString()
/*    */   {
/* 54 */     return this.encodedMess;
/*    */   }
/*    */ }


/* Location:              /home/maokei/Downloads/laboration8_lama1203_Lars.Maechel-KOMPLETTERING/ChatClient.jar!/chatserver/ChatMessage.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */