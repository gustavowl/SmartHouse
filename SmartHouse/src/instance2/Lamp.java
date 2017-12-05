package instance2;

public class Lamp extends Thread {
	private volatile int red;
	private volatile int green;
	private volatile int blue;
	
	private volatile int shown_red;
	private volatile int shown_green;
	private volatile int shown_blue;
	
	private volatile boolean on;
	private volatile boolean isStroboscope;
	
	private volatile int strob_time_ms;
	
	public Lamp() {
		red = green = blue = 0;
		on = isStroboscope = false;
		strob_time_ms = 0;
	}

	public int getRed() {
		return red;
	}
	public void setRed(int red) {
		this.red = red % 256;
	}
	public int getGreen() {
		return green;
	}
	public void setGreen(int green) {
		this.green = green % 256;
	}
	public int getBlue() {
		return blue;
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
	public void setStroboscope(boolean stroboscope) {
		this.isStroboscope = stroboscope;
	}
	public boolean isStroboscope() {
		return isStroboscope;
	}
	
	public int getStrob_time_ms() {
		return strob_time_ms;
	}

	public void setStrob_time_ms(int strob_time_ms) {
		if (strob_time_ms >= 0) {
			this.strob_time_ms = strob_time_ms;
		}
	}
	
	private void lightOn() {
		synchronized (this) {
			shown_red = red;
			shown_green = green;
			shown_blue = blue;
		}
	}
	
	private void LightOff() {
		synchronized (this) {
			shown_red = shown_green = shown_blue = 0;
		}
	}
	
	@Override
	public void run() {
		boolean flash = true;

		while (true) {
			if (on) {
				if (isStroboscope()) {
					if (flash) {
						lightOn();
					}
					else {
						LightOff();
					}
					
					try {
						Thread.sleep(strob_time_ms);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					flash = !flash;
				}
				else {
					lightOn();
				}
			}
			else {
				LightOff();
			}
		}
	}
	
	@Override
	public void interrupt() {
		super.interrupt();
	}

	public int getShownRed() {
		return shown_red;
	}

	public int getShownGreen() {
		return shown_green;
	}

	public int getShownBlue() {
		return shown_blue;
	}
}
