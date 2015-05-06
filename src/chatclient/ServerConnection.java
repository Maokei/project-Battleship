/*    */ package chatclient;
/*    */ 
/*    */ import java.io.BufferedReader;
/*    */ import java.io.IOException;
/*    */ import java.io.InputStreamReader;
/*    */ import java.io.PrintStream;
/*    */ import java.io.PrintWriter;
/*    */ import java.net.Socket;
/*    */ import java.net.UnknownHostException;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ class ServerConnection
/*    */   extends Connection
/*    */ {
/*    */   private Socket iSocket;
/*    */   private IPaddress iIPaddr;
/*    */   private BufferedReader iReader;
/*    */   private PrintWriter iWriter;
/*    */   
/*    */   public ServerConnection(IPaddress aIPaddress)
/*    */   {
/* 30 */     this.iIPaddr = aIPaddress;
/*    */   }
/*    */   
/*    */   public void connect()
/*    */   {
/*    */     try {
/* 36 */       this.iSocket = new Socket(this.iIPaddr.getHostname(), this.iIPaddr.getPortNr());
/*    */       
/* 38 */       this.iReader = new BufferedReader(
/* 39 */         new InputStreamReader(this.iSocket.getInputStream()));
/* 40 */       this.iWriter = new PrintWriter(this.iSocket.getOutputStream(), true);
/*    */     }
/*    */     catch (UnknownHostException e)
/*    */     {
/* 44 */       System.err.println("ServerSocketConnection: " + e);
/* 45 */       System.exit(-1);
/*    */     }
/*    */     catch (IOException e) {
/* 48 */       System.err.println("ServerSocketConnection: " + e);
/* 49 */       System.exit(-1);
/*    */     }
/*    */   }
/*    */   
/*    */   public void disconnect()
/*    */   {
/*    */     try
/*    */     {
/* 57 */       if (!this.iSocket.isClosed()) {
/* 58 */         this.iSocket.close();
/*    */       }
/*    */     }
/*    */     catch (IOException e) {
/* 62 */       System.err.println("Error closing socket: " + e);
/* 63 */       System.exit(-1);
/*    */     }
/*    */   }
/*    */   
/*    */   public void receive(Message msg)
/*    */   {
/*    */     try {
/* 70 */       msg.decode(this.iReader.readLine());
/*    */     }
/*    */     catch (IOException localIOException) {}
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public void send(String aMsgStr)
/*    */   {
/* 80 */     this.iWriter.println(aMsgStr);
/*    */   }
/*    */ }


/* Location:              /home/maokei/Downloads/laboration8_lama1203_Lars.Maechel-KOMPLETTERING/ChatClient.jar!/chatclient/ServerConnection.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */