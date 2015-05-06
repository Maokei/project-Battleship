/*     */ package chatclient;
/*     */ 
/*     */ import java.awt.BorderLayout;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.awt.event.WindowAdapter;
/*     */ import java.awt.event.WindowEvent;
/*     */ import javax.swing.JFrame;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.JScrollPane;
/*     */ import javax.swing.JTextArea;
/*     */ import javax.swing.JTextField;
/*     */ 
/*     */ public class ChatClient extends JFrame implements ActionListener
/*     */ {
/*     */   private static final long serialVersionUID = -6760443541225713156L;
/*     */   private IPaddress iServAddr;
/*     */   private Client thisClient;
/*     */   
/*     */   public ChatClient(String[] args)
/*     */   {
/*  22 */     super("Chat client");
/*  23 */     this.iMyChatName = args[0];
/*  24 */     this.iServerHostname = args[1];
/*  25 */     this.iPortNr = Integer.parseInt(args[2]);
/*     */     
/*  27 */     setTitle("Chat client for " + this.iMyChatName);
/*     */     
/*  29 */     setDefaultCloseOperation(3);
/*  30 */     addWindowListener(new WindowAdapter()
/*     */     {
/*     */       public void windowClosing(WindowEvent e)
/*     */       {
/*  34 */         ChatClient.this.thisClient.leave();
/*     */       }
/*     */       
/*  37 */     });
/*  38 */     setLayout(new BorderLayout());
/*     */     
/*  40 */     upperPanel.setLayout(new BorderLayout());
/*     */     
/*     */ 
/*  43 */     JScrollPane scrollPane = new JScrollPane(textArea);
/*  44 */     textArea.setLineWrap(true);
/*  45 */     textArea.setEditable(false);
/*  46 */     scrollPane.setWheelScrollingEnabled(true);
/*  47 */     upperPanel.add(scrollPane, "Center");
/*     */     
/*  49 */     lowerPanel.setLayout(new BorderLayout());
/*     */     
/*  51 */     lowerPanel.add(textField, "Center");
/*  52 */     textField.addActionListener(this);
/*  53 */     add(upperPanel, "North");
/*  54 */     add(lowerPanel, "South");
/*  55 */     pack();
/*  56 */     setResizable(false);
/*  57 */     setVisible(true);
/*  58 */     textField.requestFocus();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void appendChatMessage(final String msg)
/*     */   {
/*  65 */     javax.swing.SwingUtilities.invokeLater(new Runnable() {
/*     */       public void run() {
/*  67 */         ChatClient.textArea.append(msg + '\n');
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   public void actionPerformed(ActionEvent e) {
/*  73 */     if (e.getSource() == textField) {
/*  74 */       String msgStr = textField.getText();
/*  75 */       if (msgStr.length() > 0) {
/*  76 */         this.thisClient.sendMessage(msgStr);
/*  77 */         textField.setText("");
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void run()
/*     */   {
/*  86 */     this.iServAddr = new IPaddress(this.iServerHostname, this.iPortNr);
/*  87 */     this.thisClient = new Client(this.iMyChatName, this.iServAddr, this);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 100 */   private static final JTextArea textArea = new JTextArea(15, 40);
/* 101 */   private static final JTextField textField = new JTextField(40);
/* 102 */   private static final JPanel upperPanel = new JPanel();
/* 103 */   private static final JPanel lowerPanel = new JPanel();
/*     */   
/*     */   private String iServerHostname;
/*     */   
/*     */   private String iMyChatName;
/*     */   
/*     */   private int iPortNr;
/*     */   
/*     */   public static void main(String[] args)
/*     */   {
/* 113 */     if (args.length != 3) {
/* 114 */       System.out.println("Usage: ChatClient username server port");
/* 115 */       System.exit(1);
/*     */     }
/*     */     
/* 118 */     ChatClient cc = new ChatClient(args);
/* 119 */     cc.run();
/*     */   }
/*     */ }


/* Location:              /home/maokei/Downloads/laboration8_lama1203_Lars.Maechel-KOMPLETTERING/ChatClient.jar!/chatclient/ChatClient.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */