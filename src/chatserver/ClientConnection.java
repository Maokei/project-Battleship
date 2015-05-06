/*    */ package chatserver;
/*    */ 
/*    */ import java.io.BufferedReader;
/*    */ import java.io.IOException;
/*    */ import java.io.InputStreamReader;
/*    */ import java.io.PrintStream;
/*    */ import java.io.PrintWriter;
/*    */ import java.net.Socket;
/*    */ 
/*    */ class ClientConnection
/*    */ {
/*    */   private Socket iClientSock;
/*    */   private BufferedReader iReader;
/*    */   private PrintWriter iWriter;
/*    */   
/*    */   public ClientConnection(Socket aSock)
/*    */   {
/* 18 */     this.iClientSock = aSock;
/*    */     try {
/* 20 */       this.iReader = new BufferedReader(
/* 21 */         new InputStreamReader(this.iClientSock.getInputStream()));
/* 22 */       this.iWriter = new PrintWriter(this.iClientSock.getOutputStream(), true);
/*    */     }
/*    */     catch (IOException e) {
/* 25 */       System.err.println("Exception in ClientConnection: " + e);
/* 26 */       System.exit(-1);
/*    */     }
/*    */   }
/*    */   
/*    */   public void disconnect() {
/*    */     try {
/* 32 */       if (!this.iClientSock.isClosed()) {
/* 33 */         this.iClientSock.close();
/*    */       }
/*    */     }
/*    */     catch (IOException e)
/*    */     {
/* 38 */       System.err.println("Error closing socket: " + e);
/* 39 */       System.exit(-1);
/*    */     }
/*    */   }
/*    */   
/*    */   /* Error */
/*    */   public void receive(ChatMessage msg)
/*    */   {
/*    */     // Byte code:
/*    */     //   0: aconst_null
/*    */     //   1: astore_2
/*    */     //   2: aload_0
/*    */     //   3: getfield 35	chatserver/ClientConnection:iReader	Ljava/io/BufferedReader;
/*    */     //   6: invokevirtual 100	java/io/BufferedReader:readLine	()Ljava/lang/String;
/*    */     //   9: astore_2
/*    */     //   10: goto +39 -> 49
/*    */     //   13: astore_3
/*    */     //   14: ldc 103
/*    */     //   16: astore_2
/*    */     //   17: aload_2
/*    */     //   18: ifnonnull +6 -> 24
/*    */     //   21: ldc 103
/*    */     //   23: astore_2
/*    */     //   24: aload_1
/*    */     //   25: aload_2
/*    */     //   26: invokevirtual 105	chatserver/ChatMessage:decode	(Ljava/lang/String;)V
/*    */     //   29: goto +32 -> 61
/*    */     //   32: astore 4
/*    */     //   34: aload_2
/*    */     //   35: ifnonnull +6 -> 41
/*    */     //   38: ldc 103
/*    */     //   40: astore_2
/*    */     //   41: aload_1
/*    */     //   42: aload_2
/*    */     //   43: invokevirtual 105	chatserver/ChatMessage:decode	(Ljava/lang/String;)V
/*    */     //   46: aload 4
/*    */     //   48: athrow
/*    */     //   49: aload_2
/*    */     //   50: ifnonnull +6 -> 56
/*    */     //   53: ldc 103
/*    */     //   55: astore_2
/*    */     //   56: aload_1
/*    */     //   57: aload_2
/*    */     //   58: invokevirtual 105	chatserver/ChatMessage:decode	(Ljava/lang/String;)V
/*    */     //   61: return
/*    */     // Line number table:
/*    */     //   Java source line #46	-> byte code offset #0
/*    */     //   Java source line #48	-> byte code offset #2
/*    */     //   Java source line #49	-> byte code offset #10
/*    */     //   Java source line #50	-> byte code offset #13
/*    */     //   Java source line #51	-> byte code offset #14
/*    */     //   Java source line #54	-> byte code offset #17
/*    */     //   Java source line #55	-> byte code offset #21
/*    */     //   Java source line #57	-> byte code offset #24
/*    */     //   Java source line #53	-> byte code offset #32
/*    */     //   Java source line #54	-> byte code offset #34
/*    */     //   Java source line #55	-> byte code offset #38
/*    */     //   Java source line #57	-> byte code offset #41
/*    */     //   Java source line #58	-> byte code offset #46
/*    */     //   Java source line #54	-> byte code offset #49
/*    */     //   Java source line #55	-> byte code offset #53
/*    */     //   Java source line #57	-> byte code offset #56
/*    */     //   Java source line #59	-> byte code offset #61
/*    */     // Local variable table:
/*    */     //   start	length	slot	name	signature
/*    */     //   0	62	0	this	ClientConnection
/*    */     //   0	62	1	msg	ChatMessage
/*    */     //   1	57	2	msgString	String
/*    */     //   13	2	3	e	IOException
/*    */     //   32	15	4	localObject	Object
/*    */     // Exception table:
/*    */     //   from	to	target	type
/*    */     //   2	10	13	java/io/IOException
/*    */     //   2	17	32	finally
/*    */   }
/*    */   
/*    */   public void send(ChatMessage aMsg)
/*    */   {
/* 64 */     this.iWriter.println(aMsg);
/* 65 */     this.iWriter.flush();
/*    */   }
/*    */ }


/* Location:              /home/maokei/Downloads/laboration8_lama1203_Lars.Maechel-KOMPLETTERING/ChatClient.jar!/chatserver/ClientConnection.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */