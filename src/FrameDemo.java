
import java.awt.*;
import javax.swing.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.awt.event.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.border.Border;



public class FrameDemo extends JFrame implements Runnable
{
        
	//LoginPanel lp;
   	ChatPanel cp;
	DrawPanel dp;
        ControlPanel conp;
        JLabel L;
  	public static JPanel container = new JPanel();
        
        JList online;
        JScrollPane SP_ONLINE=new JScrollPane();
        
        boolean connected;
        Socket s;
	ObjectOutputStream oos;
	ObjectInputStream ois;
	
        public FrameDemo()
		{
		}
	
	public FrameDemo(String s)
		{
		super(s);
                online = new JList();
                online.addMouseListener(new MouseAdapter() 
                                 {
                public void mouseClicked(MouseEvent evt) 
                {
                    
                    JList online = (JList)evt.getSource();
                    if (evt.getClickCount() == 2) 
                        {
                            int index = online.getSelectedIndex();
                            JOptionPane.showMessageDialog(null,"You Clicked Twice!"+index);
                        } 
                    else if (evt.getClickCount() == 3) 
                        {   
                            int index = online.locationToIndex(evt.getPoint());
                            JOptionPane.showMessageDialog(null,"You Clicked Thrice!"+index);
                        }
                }
                                 });
                online.setForeground(new java.awt.Color(0,0,255));
	            L=new JLabel("Online");
                SP_ONLINE.setHorizontalScrollBarPolicy(
			 ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
                SP_ONLINE.setVerticalScrollBarPolicy(
			 ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
                SP_ONLINE.setViewportView(online);
	 online.add(L);
                SP_ONLINE.setBounds(350,90,130,180);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                cp = new ChatPanel(this);
		dp = new DrawPanel(this);
		conp = new ControlPanel(this,cp,dp);
		
                container.setLayout(new BorderLayout());
		container.add(cp, BorderLayout.WEST);
		container.add(dp, BorderLayout.EAST);
                container.add(SP_ONLINE,BorderLayout.CENTER);
                

		getContentPane().add(container, BorderLayout.CENTER);
                getContentPane().add(conp, BorderLayout.SOUTH);
		
		//Display the window.
                cp.setVisible(false);
		dp.setVisible(false);
                setSize(1000,500);
		setLocationRelativeTo(null);
		conp.b2.setVisible(false);
                conp.b3.setVisible(false);
                conp.b4.setVisible(false);
                conp.b5.setVisible(false);
                conp.b6.setVisible(false);
                conp.b7.setVisible(false);
                dp.setVisible(false);
                SP_ONLINE.setVisible(false);
     		setVisible(true);
                conp.b1.setForeground(Color.red);
                conp.b2.setForeground(Color.blue);
                conp.b3.setForeground(Color.blue);
                conp.b4.setForeground(Color.blue);
                conp.b5.setForeground(Color.blue);
                conp.b6.setForeground(Color.blue);
                conp.b7.setForeground(Color.blue);
                //online.add(b8);
                SP_ONLINE.setForeground(Color.blue);
                cp.ta.setForeground(Color.blue);
                dp.setForeground(Color.black);
                }
	
///---------------------------------
	public void setConnected(boolean c)
		{
		connected = c;
		}
	public boolean isConnected()
		{
		return connected;
		}
	public void connect()
		{
                setConnected(true);
		System.out.println("......PLease wait...connecting . . . ");
			try
			{
                        s = new Socket("afsaccess2.njit.edu", 46567);
			oos = new ObjectOutputStream(s.getOutputStream());
			new Thread(this).start();
			System.out.println("*****Connected*****");
			conp.b3.setVisible(true);
			conp.b1.setVisible(false);
                        }
			catch(IOException e)
			{
			System.out.println(e.getMessage());
			}	
		}
        
	public void close()
		{
		setConnected(false);	//should send last message so others can update user list
								// and remove self from shared arralist of handlers
			try
			{
                          oos.writeObject("remove");
                          oos.flush();
                          oos.close();
                          s.close();
                          JOptionPane.showMessageDialog(null,"You disconnected!");
                          System.out.println("*******Disconnected******");
                          System.exit(0);
			}
			catch(IOException e)
			{
			System.out.println(e.getMessage());
			}
		}
	public void run()
		{
		try
			{
			ois = new ObjectInputStream(s.getInputStream());
			for(;;)
				{
					Object o = receiveMessage();
					if(o != null)
					{
                                        if(o.toString().contains("#?!"))//objects converted into string
                                            {
                                                String TEMP1=o.toString().substring(3);
                                                TEMP1=TEMP1.replace("[","");
                                                TEMP1=TEMP1.replace("]","");
                                                String[] CurrentUsers=TEMP1.split(",");
                                                online.setListData(CurrentUsers);
                                                
                                            }
                                        else if(o instanceof String)
                                            {
                                                cp.appendMessage((String)o);
                                            }
                                        else if(o instanceof StringMessage)
                                            {
                                                StringMessage sm = (StringMessage)o;
						String s = (String)sm.getMessage();
						System.out.println(s);
                                                cp.ta.append(s+" has joined the chat :)"+"\n");
                                            }
					else if(o instanceof ArrayList)
                                            {
                                                ArrayList<Line> message;
                                                message=(ArrayList)o;
                                                dp.linelist = message;
						dp.repaint();
                                            }
					else if(o instanceof UserMessage)
						{
					
						}
					}
					else
						{
						break;
						}
				}
                         
			}
		catch(FileNotFoundException e)
			{
			System.out.println(e.getMessage());
			}
		catch(IOException e)
			{
			System.out.println("IO Exception: " + e.getMessage());
			}
		finally
			{
				try
					{
						ois.close();
					}
				catch(IOException e)
					{
						System.out.println(e.getMessage());
					}
			}
		}
	
        public void savechat()
        {
            try
            {
                PrintWriter textout = new PrintWriter("History.txt");
                FileOutputStream out = new FileOutputStream("DrawHistory.txt");
                ObjectOutputStream oout = new ObjectOutputStream(out);
                textout.print(cp.ta.getText());
                oout.writeObject(dp.linelist);
                textout.close();
                oout.flush();
            }
            catch(IOException e)
            {
                System.out.println(e);
            }
        }
        public void loadchat()
        {
            
          try  
                {
                    Object myObject;
                BufferedReader br = new BufferedReader(new FileReader("History.txt"));
             ObjectInputStream ois =
                 new ObjectInputStream(new FileInputStream("DrawHistory.txt"));
            
             myObject = (Object)ois.readObject();
             
              ArrayList<Line> message;
                                                message=(ArrayList)myObject;
                                                dp.linelist = message;
						dp.repaint();
          
                
                String line = null;
             
                cp.ta.setText("");
           
                    while ((line = br.readLine()) != null) 
                        {
                            cp.ta.append(line);
                        }
                  
                }
           catch(IOException e)
                {
                System.out.println(e.getMessage());
                } catch (ClassNotFoundException ex) { 
                   Logger.getLogger(FrameDemo.class.getName()).log(Level.SEVERE, null, ex);
               } 
       
        }
	 final public void sendMessage(Object o)
		{
		
			if(isConnected())
			{
                            try
                                {
                                    if(o instanceof LineMessage) 
                                        {
					System.out.println("LineMessage written to stream");
                                        dp.setVisible(true);
                                        oos.writeObject(o);
                                        oos.flush();
                                        }
                                    else
                                        {
					oos.writeObject(o);
                                        oos.flush();}
                                        }
			catch(IOException e)
                                    {
				System.out.println(e.getMessage());
                                    }
                                }
		}
	public Object receiveMessage()
		{
	
			Object obj = null;
		try
			{
				obj = ois.readObject();//initializing the state of the object for its class for data written by writeobject method
			}
		catch(IOException e)
			{
			System.out.println("End of stream.");
			}
		catch(ClassNotFoundException e)
			{
			System.out.println(e.getMessage());
			}
		return obj;
		}
	
	public void clear()
		{
		dp.linelist = new ArrayList<Line>();
		dp.repaint();
		cp.ta.setText("");
		}
	
    private static void createAndShowGUI() 
		{
       		FrameDemo frame = new FrameDemo("<Chat System with Whiteboard>");   
    		}

    public static void main(String[] args) 
		{
       		 //Schedule a job for the event-dispatching thread:
       		 //creating and showing this application's GUI.
        
		javax.swing.SwingUtilities.invokeLater(new Runnable() 
						     {
            
							public void run() 
							{

				
                					createAndShowGUI();
            						}
        					      });
    		}
}
