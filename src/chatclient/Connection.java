package chatclient;

abstract class Connection
{
  public abstract void connect();
  
  public abstract void receive(Message paramMessage);
  
  public abstract void send(String paramString);
}


/* Location:              /home/maokei/Downloads/laboration8_lama1203_Lars.Maechel-KOMPLETTERING/ChatClient.jar!/chatclient/Connection.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */