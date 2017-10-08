package Entity;

public class Notification {
	private String id; 		// String or int [?]
	private String text;
	private time receivedTime; //is this the right type [?]

	public String getId () {return id;}
	public void setId (String id) {this.id = id;}
	public String getText () {return text;}
	public void setText (String text) {this.text = text;}
	public time getTime () {return receivedTime;}
	public void setTime (time receivedTime) {this.receivedTime = receivedTime;}
	
}
