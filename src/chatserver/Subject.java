package chatserver;

abstract interface Subject
{
  public abstract void attach(ChatObserver paramChatObserver, String paramString);
  
  public abstract void detach(ChatObserver paramChatObserver, String paramString);
  
  public abstract void addMessage(ChatMessage paramChatMessage);
  
  public abstract void notify(ChatMessage paramChatMessage);
}


/* Location:              /home/maokei/Downloads/laboration8_lama1203_Lars.Maechel-KOMPLETTERING/ChatClient.jar!/chatserver/Subject.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */