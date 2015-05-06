/*    */ package chatclient;
/*    */ 
/*    */ import javax.swing.SwingUtilities;
/*    */ 
/*    */ 
/*    */ class ServerProxy
/*    */   extends Subject
/*    */ {
/*    */   private ServerConnection iServCon;
/*    */   private Client iClient;
/*    */   private Thread iReaderThread;
/*    */   
/*    */   public ServerProxy(IPaddress aServAddr)
/*    */   {
/* 15 */     this.iServCon = new ServerConnection(aServAddr);
/* 16 */     this.iServCon.connect();
/*    */   }
/*    */   
/*    */   public void attach(Client aClient)
/*    */   {
/* 21 */     this.iClient = aClient;
/* 22 */     this.iServCon.send(new Message(this.iClient.getName(), "__ATTACH__").encode());
/*    */     
/* 24 */     this.iReaderThread = new Thread()
/*    */     {
/*    */       public void run() {
/*    */         do {
/* 28 */           final Message msg = new Message();
/* 29 */           ServerProxy.this.iServCon.receive(msg);
/* 30 */           SwingUtilities.invokeLater(new Runnable() {
/*    */             public void run() {
/* 32 */               ServerProxy.this.iClient.updateChat(msg);
/*    */             }
/*    */           });
/* 35 */         } while (!Thread.interrupted());
/*    */         
/*    */ 
/*    */ 
/* 39 */         ServerProxy.this.iServCon.disconnect();
/*    */       }
/* 41 */     };
/* 42 */     this.iReaderThread.start();
/*    */   }
/*    */   
/*    */   public void detach() {
/* 46 */     this.iReaderThread.interrupt();
/* 47 */     this.iServCon.send(new Message(this.iClient.getName(), "__DETACH__").encode());
/* 48 */     this.iServCon.disconnect();
/*    */   }
/*    */   
/*    */   public void sendMessage(Message aMess)
/*    */   {
/* 53 */     this.iServCon.send(aMess.encode());
/*    */   }
/*    */ }


/* Location:              /home/maokei/Downloads/laboration8_lama1203_Lars.Maechel-KOMPLETTERING/ChatClient.jar!/chatclient/ServerProxy.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */