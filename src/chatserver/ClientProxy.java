/*    */ package chatserver;
/*    */ 
/*    */ 
/*    */ 
/*    */ class ClientProxy
/*    */   implements ChatObserver
/*    */ {
/*    */   private ClientConnection iClientCon;
/*    */   
/*    */ 
/*    */   private Server iServer;
/*    */   
/*    */   private Thread iClientThread;
/*    */   
/*    */ 
/*    */   public ClientProxy(ClientConnection aClientCon, Server aServer)
/*    */   {
/* 18 */     this.iClientCon = aClientCon;
/* 19 */     this.iServer = aServer;
/*    */   }
/*    */   
/*    */   public void listen()
/*    */   {
/* 24 */     final ClientProxy thisClient = this;
/* 25 */     this.iClientThread = new Thread()
/*    */     {
/*    */       public void run() {
/* 28 */         while (!Thread.interrupted())
/*    */         {
/* 30 */           ChatMessage msg = new ChatMessage();
/* 31 */           ClientProxy.this.iClientCon.receive(msg);
/* 32 */           if (msg.getSender().equals("dummy")) {
/*    */             break;
/*    */           }
/* 35 */           if (msg.getMessage().equals("__ATTACH__")) {
/* 36 */             ClientProxy.this.iServer.attach(thisClient, msg.getSender());
/* 37 */           } else if (msg.getMessage().equals("__DETACH__")) {
/* 38 */             ClientProxy.this.iServer.detach(thisClient, msg.getSender());
/*    */           } else
/* 40 */             ClientProxy.this.iServer.addMessage(msg);
/*    */         }
/* 42 */         ClientProxy.this.iClientCon.disconnect();
/*    */       }
/* 44 */     };
/* 45 */     this.iClientThread.start();
/*    */   }
/*    */   
/*    */   public void update(ChatMessage aMsg)
/*    */   {
/* 50 */     this.iClientCon.send(aMsg);
/*    */   }
/*    */ }


/* Location:              /home/maokei/Downloads/laboration8_lama1203_Lars.Maechel-KOMPLETTERING/ChatClient.jar!/chatserver/ClientProxy.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */