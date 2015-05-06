/*    */ package chatserver;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.io.PrintStream;
/*    */ import java.net.ServerSocket;
/*    */ import java.net.Socket;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ClientListener
/*    */ {
/*    */   private int iPortNr;
/*    */   private ServerSocket serverSocket;
/*    */   private Server iServer;
/*    */   
/*    */   public ClientListener(int aPortNr, Server aServer)
/*    */   {
/* 20 */     this.iPortNr = aPortNr;
/* 21 */     this.iServer = aServer;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public void listen()
/*    */   {
/*    */     try
/*    */     {
/* 33 */       this.serverSocket = new ServerSocket(this.iPortNr);
/* 34 */       System.out.println("ChatServer listening at port " + this.iPortNr);
/*    */     }
/*    */     catch (IOException e)
/*    */     {
/* 38 */       System.err.println("ERROR: Failed to listen on port " + this.iPortNr + ' ' + e);
/* 39 */       System.exit(-1);
/*    */     }
/*    */     try
/*    */     {
/*    */       for (;;) {
/* 44 */         Socket clientSock = this.serverSocket.accept();
/* 45 */         ClientProxy proxy = new ClientProxy(new ClientConnection(clientSock), this.iServer);
/* 46 */         proxy.listen();
/*    */       }
/*    */     } catch (IOException e) {
/* 49 */       System.out.println("ERROR: Failed to accpt client");
/*    */     }
/*    */   }
/*    */ }


/* Location:              /home/maokei/Downloads/laboration8_lama1203_Lars.Maechel-KOMPLETTERING/ChatClient.jar!/chatserver/ClientListener.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */