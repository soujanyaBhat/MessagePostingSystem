import java.io.*;
import java.util.Scanner;
import java.net.*;


public class MyClient
{
   BufferedReader in = null;
   static int s=0;
   static int flag=1;
   Socket sock = null;
   static PrintWriter out = null;
 
 //Function to message to the server  
   public int communicate_server(int s)
   {
	   int count_users,count_message;
	   String user;
	   String enter_recipient,recipient_name,message,message_line;
		Scanner sc=new Scanner(System.in);
		out.println(s);
		out.flush();
      
      try
      {
			String line = in.readLine();
		
		 switch(s)
		 {
			 //Display known users
          case 1:
			 count_users=Integer.parseInt(line);
			 System.out.println("\n Known users are: ");
			 for(int i=0;i<count_users;i++)
			 {
				 user = in.readLine();
				 if(i<(count_users-1))
				 System.out.println((i+1)+". "+user);
			 }
			 break;
			 
			 //Display connected users
          case 2:
			 count_users=Integer.parseInt(line);
			 System.out.println("\n Currently connected users are: ");
			 for(int i=0;i<count_users;i++)
			 {
				 user = in.readLine();
				 if(i<(count_users-1))
				 System.out.println((i+1)+". "+user);
			 }
			 break;
			 
			 //Send message to particular user
          case 3:
			 enter_recipient=(line);
			 System.out.println(enter_recipient);
			 recipient_name = sc.nextLine();
			 out.println(recipient_name); //sending the receipient's name to server
			 message_line=in.readLine();
			 System.out.println(message_line);
			 message=sc.nextLine();
			 out.println(message); //sending the message to server
          System.out.println("Message posted to "+recipient_name);
			 break;				 
			 
          //Send message to all connected users
			 case 4: message_line=line;
				   System.out.println(message_line);
				   message=sc.nextLine();
				   out.println(message); //sending the message to server
               System.out.println("Your message was successfully posted to all currently connected users.");
               
               break;

			 //Send message to all known users
          case 5:
				 message_line=line;
				 System.out.println(message_line);
				 message=sc.nextLine();
				 out.println(message);  // sending the message to server
             System.out.println("Your message was successfully posted to all known users.");
			 break;
			 
			 //Retrieve the user's message
          case 6:
				 count_message=Integer.parseInt(line);
				 System.out.println("\nYour messages: ");
             System.out.println(count_message);
				 for(int i=0;i<count_message;i++)
				 {
					 message = in.readLine(); //getting the list of messages from the server
					 if(i<(count_message-1))
					 System.out.println((i+1)+". "+message);
				 }
			 break;
			 
          //Exit the session
          case 7:
            System.exit(0);
			 default:
			 break;
		 }
      } 
      catch (Exception e)
      {
         System.out.println(e);
         System.exit(1);
      }
      return 1;
   }

//Function to create socket connection  
   public void listenSocket(String host, int port)
   {
      try
      {
	 sock = new Socket(host, port);
	 out = new PrintWriter(sock.getOutputStream(), true);
	 in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
      } 
      catch (UnknownHostException e) 
      {
	 System.out.println("Error: Unknown host!");
	 System.exit(1);
      } 
      catch (IOException e) 
      {
	 System.out.println("Oops! No I/O");
	 System.exit(1);
      }
   }

//Function to get username from the server.
   public void getUserFromServer(String user)
   {
	   try
	   {
	   out.println(user);
	   String userAdded= in.readLine();
	      
		  if(userAdded.contains("Error: This user is already connected. Try again with a different username."))
		  {
			  flag=0;
		  }
		  
		  System.out.println("\n"+userAdded); 
   		}catch(IOException e)
   		{
		System.out.println("Oops! No I/O.");
		System.exit(1);
   		}
   }
   
//Function to display the menu   
   public static void menu(int num,Scanner sc,MyClient client)
   {
    
    try
    {
     if (flag==1)
     {
	   System.out.println();
	   System.out.println("1. Display the names of all known users.");
      System.out.println("2. Display the names of all currently connected users.");
      System.out.println("3. Send a text message to a particular user.");
      System.out.println("4. Send a text message to all currently connected users.");
      System.out.println("5. Send a text message to all known users.");
      System.out.println("6. Get my message.");
      System.out.println("7. Exit.");
      System.out.println("Enter your command: ");
      num = sc.nextInt();
	   
      flag=client.communicate_server(num);
      }
      }
  
      catch (Exception e)
      {
      //System.out.println(e); // For debugging purpose
      System.out.println("Invalid input, input has to be an integer value.");
      sc.nextLine();
      }
	  }
   
 //Main Function  
   public static void main(String[] args)
   {
	   
	   Scanner sc = new Scanner(System.in);
	   flag=1;
      
	   //getting the arguments
      if (args.length != 2)
      {
         System.out.println("Error: Not enough arguments");
	 System.exit(1);
      }
      //Creating client thread
      MyClient client = new MyClient();

      String host = args[0];
      int port = Integer.valueOf(args[1]);
      
      client.listenSocket(host, port); // establishing connection on the given port
      
      //Getting the user name
      System.out.println("Enter user name:");
      String username=sc.nextLine();
      
      client.getUserFromServer(username);
      
      //Displaying the menu function until the user wants to exit
      while(s!=7)
      {
      menu(s,sc,client);
      }
   }
}