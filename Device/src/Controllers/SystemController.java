package Controllers;



import Entity.User;

public class SystemController {

	private User user = new User();
	
	public User getUser(){return user;}
	public void setUser(User u){user = u;}

}
