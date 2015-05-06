/*    */ package chatserver;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ChatServer
/*    */ {
/*    */   private final ClientListener iListener;
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public ChatServer(String pNrStr)
/*    */   {
/* 15 */     int portNr = Integer.parseInt(pNrStr);
/* 16 */     this.iListener = new ClientListener(portNr, new Server());
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public void start()
/*    */   {
/* 24 */     this.iListener.listen();
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public static void main(String[] args)
/*    */   {
/* 32 */     new ChatServer(args[0]).start();
/*    */   }
/*    */ }


/* Location:              /home/maokei/Downloads/laboration8_lama1203_Lars.Maechel-KOMPLETTERING/ChatClient.jar!/chatserver/ChatServer.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */