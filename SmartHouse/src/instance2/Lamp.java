package instance2;

public class Lamp extends Thread {
	private volatile int red;
	private volatile int green;
	private volatile int blue;
	private volatile boolean on;
	private volatile int strob_time_ms;
	
	public Lamp() {
		red = green = blue = 0;
		on = false;
		strob_time_ms = 0;
	}

	public int getRed() {
		if (isOn()) {
			return red;
		}
		return 0;
	}
	public void setRed(int red) {
		this.red = red % 256;
	}
	public int getGreen() {
		if (isOn()) {
			return green;
		}
		return 0;
	}
	public void setGreen(int green) {
		this.green = green % 256;
	}
	public int getBlue() {
		if (isOn()) {
			return blue;
		}
		return 0;
	}
	public void setBlue(int blue) {
		this.blue = blue % 256;
	}
	public boolean isOn() {
		return on;
	}
	public void setOn(boolean isOn) {
		this.on = isOn;
	}
	public boolean isStroboscope() {
		return strob_time_ms > 0;
	}
	public void setStroboscope(boolean isStroboscope) {
		strob_time_ms = (isStroboscope) ? 100 : 0;
	}
	
	public int getStrob_time_ms() {
		return strob_time_ms;
	}

	public void setStrob_time_ms(int strob_time_ms) {
		if (strob_time_ms >= 0) {
			this.strob_time_ms = strob_time_ms;
		}
	}
	
	@Override
	public void run() {
		int prev_red, prev_blue, prev_green, swift;
		prev_red = prev_green = prev_blue = 0;
		while (true) {
			if (on && isStroboscope()) {
				swift = red;
				red = prev_red;
				prev_red = swift;
				
				swift = green;
				green = prev_green;
				prev_green = swift;
				
				swift = blue;
				blue = prev_blue;
				prev_blue = swift;
				
				try {
					Thread.sleep(strob_time_ms);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	@Override
	public void interrupt() {
		super.interrupt();
	}
}
