/*    */ package chatclient;
/*    */ 
/*    */ 
/*    */ class Client
/*    */   implements ChatObserver
/*    */ {
/*    */   ChatClient iChatClient;
/*    */   private ServerProxy iServProxy;
/*    */   private String iMyName;
/*    */   
/*    */   public Client(String name, IPaddress ipAddr, ChatClient aChatClient)
/*    */   {
/* 13 */     this.iMyName = name;
/* 14 */     this.iServProxy = new ServerProxy(ipAddr);
/* 15 */     this.iServProxy.attach(this);
/* 16 */     this.iChatClient = aChatClient;
/*    */   }
/*    */   
/*    */   public void sendMessage(String messStr) {
/* 20 */     this.iServProxy.sendMessage(new Message(getName(), messStr));
/*    */   }
/*    */   
/*    */   public void leave() {
/* 24 */     this.iServProxy.detach();
/*    */   }
/*    */   
/*    */   public String getName() {
/* 28 */     return this.iMyName;
/*    */   }
/*    */   
/*    */   public void updateChat(Message aMess)
/*    */   {
/* 33 */     String mess = aMess.toString();
/*    */     
/* 35 */     this.iChatClient.appendChatMessage(mess);
/*    */   }
/*    */ }


/* Location:              /home/maokei/Downloads/laboration8_lama1203_Lars.Maechel-KOMPLETTERING/ChatClient.jar!/chatclient/Client.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */