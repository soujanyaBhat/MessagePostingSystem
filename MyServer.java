import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.Semaphore;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.*;


class ServerThread implements Runnable
{
	   private static final Logger logger = Logger.getLogger(ServerThread.class.getName());
      private Socket client;
	   
	   ServerThread(Socket client) 
	   {
	      this.client = client;
	   }
	   
	@Override
	   public void run()
	   {
	      String name_client;
	      BufferedReader in = null;
	      PrintWriter out = null;
		   int count=0;
	      try 
	      {
		 in = new BufferedReader(new InputStreamReader(client.getInputStream()));
		 out = new PrintWriter(client.getOutputStream(), true);
	      } 
	      catch (IOException e) 
	      {
	    	  // Debug mode - severe log level to catch exception, catches any exception and displays the message but meant to catch errors with input & output stream.
	    	  logger.log(Level.SEVERE, e.getMessage(), e);
           System.exit(-1);
	      }

	      try 
	      {
		
		 name_client = in.readLine();
		 
		 if(MyServer.user_added(name_client.trim()))
		 {
		 out.println(name_client+" user has been added");
		 out.flush();
		 
		 while(true)
		 {
			 int selection=Integer.parseInt(in.readLine());

// Switch case for menu options

			 switch(selection)
			 {
				 case 1:
					// System.out.println(name_client+" displayed all known users.");
                logger.info(name_client+" displayed all known users.");
					 ArrayList<String> copy_users=MyServer.getUsers();
					 String output_message="";
					 count=copy_users.size()+1;
					 out.println(count); //send size of the arraylist as the first argument for count
					 out.flush();
					 for(String user: copy_users) // Using advance for loop to go through copy_users list to get every use.
					 {
						 output_message=output_message+user+"\n";
					 }
					 out.println(output_message);
					 
					 break;
				 
				 case 2:
					// System.out.println(name_client+" displayed all connected users.");
               logger.info(name_client+" displayed all connected users.");
					
					 ArrayList<String> connected_users=MyServer.getConnectedUsers();
					 String output_message2="";
                
                //Displaying the total number of connected users
					 System.out.println("count is "+connected_users.size());
					 count=connected_users.size()+1;
					 out.println(count); //taking size of array list to send it as an argument for now.
					 out.flush();
                
					 for(String user: connected_users) //Advance for loop to move through arraylist - connected_users.
					 {
						 output_message2=output_message2+user+"\n";
					 }
					 out.println(output_message2);
					 
					 break;
				 
				 case 3:
			
					 out.println("Enter recipient's name: "); //client name for whom message is.
					 String recipient_name= in.readLine(); 
					 out.println("Enter the message: ");// message
					 String message= in.readLine();
					// System.out.println(name_client+" posts a message for "+recipient_name);
                logger.info(name_client+" posts a message for "+recipient_name);
					 MyServer.send_message(name_client, recipient_name, message);
					 break;
				 
				 case 4:
				
					 out.println("Enter the message: ");
					 String message_connected_users= in.readLine();
					 //System.out.println(name_client+" posted a message to all currently connected users");
                logger.info(name_client+" posted a message to all currently connected users");
					 MyServer.sendMessagetoConnectedUsers(name_client, message_connected_users);
				 break;
				 
				 case 5:
				
					 out.println("Enter the message: ");
					 String message_known_users= in.readLine();
					 //System.out.println(name_client+" posted a message to all known users");
					 logger.info(name_client+" posted a message to all known users");
                MyServer.sendMessagetoKnownUsers(name_client, message_known_users);
				 break;
				 
				 case 6:
					
					 ArrayList<String> my_message=MyServer.displayAllMessages(name_client);
					 
					 String his_message="";
					 count=my_message.size()+1;
					 out.println(count);
					 out.flush();
					 for(String currMsg:my_message)
					 {
						 his_message=his_message+currMsg+"\n";
					 }
					 out.println(his_message);
					 //System.out.println(name_client+" retrieved his/her messages");
                logger.info(name_client+" retrieved his/her messages");
                my_message.clear(); //Clear off messages once user reads the message.
				 break;
				 
				 case 7:
					logger.info(name_client+" has exited");
					// System.out.println(name_client+" exited");
				 break;
				 
				 default:
				 System.out.println("Invalid option!");
				 break;
				 
			 }
			 if(selection==7)
			 {
				 out.println("Exit received");
				 MyServer.removeConnectedUser(name_client);
				 break;
			 }
		   }
	       } 
		 
		 else
	      {
			 out.println("Error: This user is already connected. Try again with a different username.");
	      }
		 
	     }catch (IOException e) 
	      {
		// System.out.println("Read failed");
	     logger.log(Level.SEVERE, e.getMessage(), e); //Mainly it is an exception in read failure.

         }

	      try 
	      {
         //Closing the client.
         
		 client.close();
	     
         } 
	      catch (IOException e) 
	      {
		 //System.out.println("Error: Client Close failed"); 
       // Debugging point if client ould not be closed.
       logger.log(Level.SEVERE, e.getMessage(), e);

		 System.exit(-1);
	      }
	   }
	
}

public class MyServer {

	private static final Logger logger = Logger.getLogger(ServerThread.class.getName());
   ServerSocket server = null;
   //List for maintaining connected users
	static HashMap<String,String> connectedUsers= new HashMap<String,String>();
   
   //List for maintaining Known users
   static HashMap<String, ArrayList<String>> knownUsers= new HashMap<String,ArrayList<String>>();
   
	static  Semaphore mutex=new Semaphore(1);
   
	//Function to put each user that sets up a connection into the list of known users   
      public static boolean user_added(String user)
	   {
		   boolean userNotConnected=true;
		   if (connectedUsers.containsKey(user))
		   {
			   userNotConnected=false;
			   return userNotConnected;
		   }
		   else
		   {
			   connectedUsers.put(user, "");
		   }
		   if(knownUsers.containsKey(user))
		   {
			   
		   logger.info("Connection by a known user "+user);
         }
		   else
		   {
		    
          logger.info("Connection by a unknown user "+user);
		    ArrayList<String> messages=new ArrayList<>();
		    knownUsers.put(user, messages);
		   }
		   return userNotConnected;
	   }
      
	//Function to retrieve the users from the known user list   
	   public static ArrayList<String> getUsers()
	   {
		   ArrayList<String> knownUsersList=new ArrayList<>();
		   for(Entry<String, ArrayList<String>> entry : knownUsers.entrySet()) 
			{
				String key = entry.getKey(); 				
				knownUsersList.add(key);				
        				}
		   return knownUsersList;
	   }
	//Function that handles the case when a user exits the session   
	   public static void removeConnectedUser(String user)
	   {
		   connectedUsers.remove(user);
	   }
	//Function to get the list of connected users   
	   public static ArrayList<String> getConnectedUsers()
	   {
		   ArrayList<String> connUsersList=new ArrayList<>();
		   for(Entry<String, String> entry : connectedUsers.entrySet()) 
			{
				String key = entry.getKey(); 
				
				connUsersList.add(key);
				
				}
		   return connUsersList;
	   }
	 //Function to send message to user 
	   public static void send_message(String user, String recipient, String newMsg)
	   {
		  
		   try {
         
         //Implementing mutual exclusion
			mutex.acquire(); 
		
		   ArrayList<String> message_user;
		   if(knownUsers.containsKey(recipient))
		   {
			   message_user=knownUsers.get(recipient);   
		   }
		   else
		   {
			   message_user=new ArrayList<String>();
		   }
		   Date date=new Date();
			message_user.add(date+",  "+"From "+user+",  "+newMsg);
			knownUsers.put(recipient, message_user);
			mutex.release();
		   } catch (InterruptedException e) {
				
				e.printStackTrace();
			}
	   }
      
	 // Function to send message to connected users
	   public static void sendMessagetoConnectedUsers(String user, String msg)
	   {
		   for(Entry<String, String> entry : connectedUsers.entrySet()) 
			{
				String key = entry.getKey(); // Get the index key
				if(!key.equals(user))//if the value retrieved is not the same user, 
					send_message(user,key,msg); // post the message to them as recipient
				}
	   }
	   
	 // Funcrion to send message to known users 
	   public static void sendMessagetoKnownUsers(String user, String msg)
	   {
		   for(Entry<String, ArrayList<String>> entry : knownUsers.entrySet()) 
			{
				String key = entry.getKey(); // Get the index key
				if(!key.equals(user))//if the value retrieved is not the same user, 
					send_message(user,key,msg); // post the message to them as recipient
			}
	   }
	   
	//Function to display the messages of a user   
	   public static ArrayList<String> displayAllMessages(String user)
	   {
         ArrayList<String> user_message;
         user_message=knownUsers.get(user);
         return user_message;
	   }
	//Function to assign socket to server and starting the server thread 
	   public void listenSocket(int port)
	   {
	      try
	      {
		      server = new ServerSocket(port); 
		      System.out.println("Server is running on port no." + port + 
		                     "." + " Use ctrl-C to end.");
	      }catch (IOException e) 
	      {
		      System.out.println("Error creating the socket!");
		      System.exit(-1);
	      }
	      while(true)
	      {
	         ServerThread w;
	         try
	         {
	            w = new ServerThread(server.accept());
	            Thread t = new Thread(w);
	            t.start();
			   } 
			catch (IOException e) 
			{
		    System.out.println("Accept failed!");
		    System.exit(-1);
			}
	      }
	   }
      
   //Main Function
   
	   public static void main(String[] args)
	   {
	      //Checks if all the required arguments were given
         if (args.length != 1)
	      {
	         System.out.println("Error: Not enough arguments");
		 System.exit(1);
	      }

	      MyServer server = new MyServer(); //creating an instance of server thread
	      int port = Integer.valueOf(args[0]);
	      server.listenSocket(port); //establishing port and socket connection.
	   }
}
