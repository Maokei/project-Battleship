/*    */ package chatserver;
/*    */ 
/*    */ 
/*    */ class IPaddress
/*    */ {
/*    */   private String iHostname;
/*    */   
/*    */   private int iPortNr;
/*    */   
/*    */ 
/*    */   public IPaddress(String aHostname, int aPortNr)
/*    */   {
/* 13 */     this.iHostname = aHostname;
/* 14 */     this.iPortNr = aPortNr;
/*    */   }
/*    */   
/* 17 */   public String getHostname() { return this.iHostname; }
/* 18 */   public int getPortNr() { return this.iPortNr; }
/*    */ }


/* Location:              /home/maokei/Downloads/laboration8_lama1203_Lars.Maechel-KOMPLETTERING/ChatClient.jar!/chatserver/IPaddress.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */