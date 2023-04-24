//class Item

/*
This class is to create objects that holds information about items for bid, it has 3 fields
item-name, bid-amount and the client-IP of the last successful biddre

The initial value of bid-amount is zero

It has public accessor mutators to manipulate private fields
*/
public class Item {
	private String itemName;			//Item Name
	private double topQuoteAmount;		//Bid Amount
	private String clientIP;			//Client-IP of last bidder
	public Item next = null;
	public Item() {						//Default Constructor
		itemName = "";
		topQuoteAmount = 0;
		clientIP = "";
	}
	//Parameterized constructor
	public Item(String iname, double qval) {
		itemName = iname;
		topQuoteAmount = qval;
		clientIP = "";
	}
	public Item(String iname, double qval, String cIP) {
		itemName = iname;
		topQuoteAmount = qval;
		clientIP = cIP;
	}
	
	//Accessors and mutators
	
	//Returns Item name
	public String getItem() {
		return itemName;
	}
	
	//To change item name
	public void setItem(String iname) {
		itemName = iname;
	}
	
	//Returns bid amount
	public double getQuoteAmount() {
		return topQuoteAmount;
	}
	
	//Sets bid amount
	public void setQuoteAmount(double qval) {
		topQuoteAmount = qval;
	}
	
	//Returns Client IP
	public String getClientIP () {
		return clientIP;
	}
	
	//Sets Client IP
	public void setClientIP (String cIP) {
		clientIP = cIP;
	}
	
	//Displays Client Detail
	public void show() {
		System.out.println(itemName+" |\t"+topQuoteAmount+" |\t" + clientIP);
	}
}

