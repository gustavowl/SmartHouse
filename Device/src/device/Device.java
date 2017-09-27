package device;

import java.util.Timer;
import java.util.TimerTask;

public abstract class Device {
	
	public static int interval;
	
	private String productID; 
	private Boolean ligada;
	private Integer wattage;
	
	//zera o timer caso ativo
	public void setTimer(int timed) {
	
        interval = timed;
        int delay = 1000;
        int period = 1000;
        final Timer time = new Timer();
        System.out.println(interval);
        time.scheduleAtFixedRate(new TimerTask() {

            public void run() {
                if (interval == 0) {
                    System.out.println("work finished");
                    time.cancel();
                    time.purge();
                } else {
                    System.out.println(setInterval());
                }
            }
        }, delay, period);
    }
	
	public void toggleLigar() {
		
	}
	
	public void toggleDesligar() {
		
	}

    private static int setInterval() {

        return --interval;
    }

	public String getProductID() {
		return productID;
	}

	public void setProductID(String productID) {
		this.productID = productID;
	}

	public Boolean getLigada() {
		return ligada;
	}

	public void setLigada(Boolean ligada) {
		this.ligada = ligada;
	}

	public Integer getWattage() {
		return wattage;
	}

	public void setWattage(Integer wattage) {
		this.wattage = wattage;
	}
    
 	
}
