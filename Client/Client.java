import java.net.*;
import java.io.*;

// Client Componet

/*
Client Application takes command line arguments and validates as server commands then passes that to server 
as request. Server processes them and supplies response to the client. Client displays server response.

Validity of the command such as syntax, argument etc is verified in the client

Operational validations are done in server application. For example if one item already there in the record,
and user attempt add another of same then this validations are verified by server and rejection message is
forwarded to client as response.
*/

public class Client {
	public static void main (String args[]) throws Exception {
		
		//If there is no command line argument
		if (args.length == 0) {
			System.out.println("No Command-line instruction found!");
			System.exit(1);
		}
		//Checking syntax for show command
		if (args[0].equals("show") && args.length > 1) {
			System.out.println("\"show\" command does not take any other arguments!");
			System.out.println("USAGE: java Client show");
			System.exit(1);
		}
		//Checking syntax for item command
		if (args[0].equals("item") && args.length != 2) {
			System.out.println("\"item\" command takes exactly one argument which is either missing or there are more than one");
			System.out.println("USAGE: java Client item <user-item>");
			System.exit(1);
		}
		//Checking syntax for quit command
		if (args[0].equals("quit") && args.length > 1) {
			System.out.println("\"quit\" command does not take any other arguments!");
			System.out.println("USAGE: java Client quit");
			System.exit(1);
		}
		//Checking syntax for bid command
		if (args[0].equals("bid") && args.length != 3) {
			System.out.println("\"bid\" command takes 2 arguments as below-");
			System.out.println("USAGE: java Client bid <item-name> <quote-amount");
			System.exit(1);
		}
		//Checking if the command is one of the valid server command or not [It could be show/item/bid or quit]
		if (args[0].equals("show") || args[0].equals("quit") || args[0].equals("bid") || args[0].equals("item")) {
			//Creating a new socket
			Socket socket = new Socket("localhost",6001);
			//Creating input and output streams for bi-directional communication
			DataInputStream din = new DataInputStream(socket.getInputStream());
			DataOutputStream dout = new DataOutputStream(socket.getOutputStream());

			//Making Request String to server
			
			String serverResponse = "";
			String serverRequest = "";
			for(int i=0; i<args.length; i++ ) {
				serverRequest += args[i] + " ";
			}
			
			//Sending Request to Server
			dout.writeUTF(serverRequest);
			dout.flush();
			
			//Reading response from Server and Displaying that
			serverResponse = din.readUTF();
			System.out.println(serverResponse);
		
  			din.close();
			dout.close();
			socket.close();
		}
		else {
			System.out.println("Invalid command");
			System.exit(1);
		}
	}
}