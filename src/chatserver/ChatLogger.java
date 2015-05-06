/*    */ package chatserver;
/*    */ 
/*    */ import java.io.BufferedWriter;
/*    */ import java.io.FileWriter;
/*    */ import java.io.IOException;
/*    */ import java.io.PrintStream;
/*    */ import java.io.PrintWriter;
/*    */ import java.text.SimpleDateFormat;
/*    */ import java.util.Calendar;
/*    */ 
/*    */ 
/*    */ class ChatLogger
/*    */   implements ChatObserver
/*    */ {
/*    */   public ChatLogger()
/*    */   {
/*    */     try
/*    */     {
/* 19 */       this.iPrintWriter = new PrintWriter(new BufferedWriter(new FileWriter(fileName, true)));
/*    */     }
/*    */     catch (IOException e) {
/* 22 */       System.out.println("Error writing to file " + fileName);
/*    */     }
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public void update(ChatMessage aMsg)
/*    */   {
/* 31 */     String timeStamp = new SimpleDateFormat("yy-MM-dd HH:mm").format(Calendar.getInstance().getTime());
/* 32 */     this.iPrintWriter.println(timeStamp + " " + aMsg.getSender() + "> " + aMsg.getMessage());
/* 33 */     this.iPrintWriter.flush();
/*    */   }
/*    */   
/*    */ 
/* 37 */   private static String fileName = "ChatServer.log";
/* 38 */   private PrintWriter iPrintWriter = null;
/*    */ }


/* Location:              /home/maokei/Downloads/laboration8_lama1203_Lars.Maechel-KOMPLETTERING/ChatClient.jar!/chatserver/ChatLogger.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */