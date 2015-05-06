/*    */ package chatserver;
/*    */ 
/*    */ import java.util.HashMap;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class Server
/*    */   implements Subject
/*    */ {
/*    */   private HashMap<String, ChatObserver> observers;
/*    */   
/*    */   public Server()
/*    */   {
/* 18 */     this.observers = new HashMap();
/* 19 */     attach(new ChatLogger(), "Logger");
/*    */   }
/*    */   
/*    */   public void attach(ChatObserver obs, String name)
/*    */   {
/* 24 */     this.observers.put(name, obs);
/* 25 */     addMessage(new ChatMessage("Server", name + " has joined the chat"));
/*    */     
/* 27 */     if (this.observers.size() > 1)
/*    */     {
/* 29 */       StringBuilder sb = new StringBuilder();
/* 30 */       sb.append("Active:");
/*    */       
/* 32 */       for (String chatName : this.observers.keySet()) {
/* 33 */         sb.append(" " + chatName);
/*    */       }
/* 35 */       obs.update(new InfoMessage(sb.toString()));
/*    */     }
/*    */     else
/*    */     {
/* 39 */       obs.update(new InfoMessage("No other chatters are active."));
/*    */     }
/*    */   }
/*    */   
/*    */   public void detach(ChatObserver obs, String name)
/*    */   {
/* 45 */     this.observers.remove(name);
/* 46 */     addMessage(new ChatMessage("server", name + " has left the chat"));
/*    */   }
/*    */   
/*    */   public synchronized void addMessage(ChatMessage aMess) {
/* 50 */     notify(aMess);
/*    */   }
/*    */   
/*    */   public void notify(ChatMessage aMess) {
/* 54 */     for (ChatObserver obs : this.observers.values()) {
/* 55 */       obs.update(aMess);
/*    */     }
/*    */   }
/*    */ }


/* Location:              /home/maokei/Downloads/laboration8_lama1203_Lars.Maechel-KOMPLETTERING/ChatClient.jar!/chatserver/Server.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */