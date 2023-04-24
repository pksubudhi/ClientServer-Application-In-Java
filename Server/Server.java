import java.net.*;
import java.io.*;
import java.util.concurrent.*;
import java.time.format.*;
import java.time.*;

//Server Component

/*
At the beginig server is loaded and be ready for client communication, one the communication request is sent, then it exablishes the connection.
Once connection is done it will be ready to accept communications from connected clients. Since this application is a multi=threaded such that 
more than one client (maximum upto 30 clients) can connect to server at a time, it processes all them one by one.

Server parses user request , finds the instruction and processes accoringly. If it fails to process the command then immediately acknowledge to 
respective client.

In this bidding application items are stored and managed through an user defined single linked list. And items are stores as an object belongs
to user defined object Item.

*/

public class Server {

    private static Server server; 				// Server object reference
    private ServerSocket serverSocket;			// Server socket
  	File logFile;								// Reference to the log file 
  	FileWriter logFileWriter;					// File writer
  	
  	//The executor manages 30 clients in the pool
    private ExecutorService executorService = Executors.newFixedThreadPool(30);        

    public static void main(String[] args) throws IOException {
        server = new Server();				// Server object created
        server.runServer();						// Server starts here
    }

	//This User-defined methods runs the server
	
    private void runServer() {   
        int serverPort = 6001;					//Server port number [between 6000 to 6999]
        try {
        	logFile = new File("log.txt");		//Creating and checking if the lof file does exists
        	if (logFile.exists()) {				// If exists then create a new file for every server session starts
        		logFile.delete();
        		logFile = new File("log.txt");
        		logFileWriter = new FileWriter(logFile, true);
        	}
            System.out.println("Starting Server");
            serverSocket = new ServerSocket(serverPort); 	//Server Soket is created and started

			//Accepting client requests continiously
            while(true) {
                //Awaiting client request
                try {
                    Socket socket = serverSocket.accept();
                    
                    executorService.submit(new ServiceRequest(socket));
                }
				catch(IOException ioe) {
                    System.out.println("Error accepting connection");
                    ioe.printStackTrace();
                }
            }
        }
		catch (IOException e) {
            System.out.println("Error starting Server on "+serverPort);
            e.printStackTrace();
        }
    }

    //Reference for item objects in a single linked list
   	private Item itemList = null;
   	
   	//Inner class that handles multi-trhreaded client request
   	
	class ServiceRequest implements Runnable {

        private Socket socket;
		
		//Connected with client-server
        public ServiceRequest (Socket connection) {
            this.socket = connection;
        }

		//run() method of thread that processes coammdn
        public void run() {
			try {
			
			//Creating streams
			DataInputStream din = new DataInputStream(socket.getInputStream());
			DataOutputStream dout = new DataOutputStream(socket.getOutputStream());
			
			//Temporary variables for various internal use
			String sourceStr = "";
			String[] command;
			String str = "";
			
			//Getting IP address of the client
			InetAddress ip = socket.getInetAddress();
			String ipStr = ip.toString().substring(1);
			//Getting system date and time
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");  
   			DateTimeFormatter tmf = DateTimeFormatter.ofPattern("HH:mm:ss");
   			
			LocalDateTime now = LocalDateTime.now();
   			 
            //Reading user request
			sourceStr = din.readUTF();
			
			//Writing log data to log.txt
			logFileWriter =  new FileWriter(logFile, true);
            logFileWriter.write(dtf.format(now) + " | " + tmf.format(now) + " | " + ipStr + " | " + sourceStr +"\n");
            logFileWriter.close();
            
			//Tokenizing the string to extract meaning
			command = sourceStr.split(" ");
			System.out.println("Processing: " + sourceStr);
			
			//If the command is "item" 
			if (command[0].equals("item")) {
				
				Item newItem =  new Item(command[1], 0);
				Item temp = null;
				if (itemList == null ) {
					itemList = newItem;
					dout.writeUTF("Success.");
				}
				else {
					if(!itemFound(command[1])) {	// Adding at the end of the single linked list of items
						temp = itemList;
						while(temp.next != null) {	
							temp = temp.next;
						}
						temp.next = newItem;
						dout.writeUTF("Success.");
					}
					else {		//If the item already in the list then it won't add
						dout.writeUTF("Item already in the list!");
					}
				}
					
			}
			else if (command[0].equals("show")) {		//if the command is "show"
				if (itemList == null) {
					dout.writeUTF("There are currently no items in this auction.");
				}
				else {
					String response="";
					Item temp=itemList;
					
					//Making a string of all available items in the list before it is being responded to client
					while(temp != null) {
		
						if(temp.getClientIP().length() > 0) {
							response += String.format(" %-12s : %.2f : %s\n", temp.getItem(), temp.getQuoteAmount(), temp.getClientIP());	
						}
						else {
							response += String.format(" %-12s : %.2f : <no bids>\n", temp.getItem(), temp.getQuoteAmount());
						}
						temp = temp.next;
					}
					dout.writeUTF(response);
				}
			}
			else if (command[0].equals("bid")) {		//If the command is "bid"
				if(!itemFound(command[1])) {
					dout.writeUTF("Item not found in the list.");
				}
				else {
					Item temp = itemList;
					while ( !temp.getItem().equals(command[1]) ) {
						temp = temp.next;
					}
					
					//Updating new bid value if the item is available and the bid value is higher
					if (temp.getQuoteAmount() < Double.parseDouble(command[2]) ) {
						temp.setQuoteAmount(Double.parseDouble(command[2]) );
						temp.setClientIP(ipStr);
						dout.writeUTF("Accepted.");
					}
					else {
						dout.writeUTF("Rejected.");		//otherwise rejects
					}
				}
			}
			else if (command[0].equals("quit")) {		//If the command is "quit"
					
				dout.writeUTF("Server Shutdown!");
				System.exit(0);
			}
			dout.flush();
		}
		catch(Exception ex) {
			System.out.println("Some Issue encountered");
		}
        try {
            socket.close();
        }
		catch(IOException ioe) {
            System.out.println("Error closing client connection");
        }
    }
    
    //To find if the item is already in the list or not
	private boolean itemFound(String iname) {
		if (itemList == null ) {
			return false;
		}
		else {
			Item temp = itemList;
			while (temp != null ) {
				if (temp.getItem().equals(iname) ) {
					return true;
				}
				temp = temp.next;
			}
			return false;
		}
	}        
    }
}

