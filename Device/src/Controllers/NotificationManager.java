package Controllers;

import java.util.ArrayList;
import java.util.Arrays;

import Entity.Notification;

public class NotificationManager {

	private ArrayList<Notification> notifications = new ArrayList<Notification>();

	public ArrayList<Notification> getNotifications(){return notifications;}

	public void addNotification (Notification n) {
		notifications.add(n);
	}

	//works[?]
	public void rmvNotification (Notification n) {
		notifications.rmv(n);
	}

}
