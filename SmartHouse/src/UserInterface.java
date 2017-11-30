import java.util.ArrayList;


public abstract class UserInterface {
	
	protected App app;
	protected int validOptionMin;
	protected int validOptionMax;
	
	public UserInterface() {
		this(new App());
	}
	
	public UserInterface(App app) {
		this.app = app;
		setOptionRangeInvalid();
	}
	
	public void run() {
		boolean finish = false;
		int opt;
		while (!finish) {
			showInitialOptions();
			opt = readOptionUntilValid();
			finish = executeInitialOption(opt);
		}
	}
	
	protected void setOptionRangeInvalid() {
		validOptionMin = 0;
		validOptionMax = -1;
	}
	
	protected boolean isOptionRangeValid() {
		return validOptionMax >= validOptionMin;
	}
	
	protected boolean isOptionWithinRange(int option) {
		return isOptionRangeValid() && option >= validOptionMin &&
				option <= validOptionMax;
	}
	
	protected abstract void showInitialOptions();
	
	protected abstract int readOption();
	
	protected int readOptionUntilValid() {
		return readOptionUntilValid("Invalid option selected. Rechoice!");
	}
	
	protected int readOptionUntilValid(String errorMessage) {
		int opt;
		while (true) {
			opt = readOption();
			
			if (isOptionWithinRange(opt)) {
				return opt;
			}
			
			showErrorMessage(errorMessage);
		}
	}
	
	protected abstract void showMessage(String message);
	
	protected abstract void showErrorMessage(String errorMessage);
	
	protected abstract boolean executeInitialOption(int option);
	
	protected void executeDiscovery() {
		/* Protocol outline:
		 * 1 - Sends broadcast message in order to discover new devices
		 * 2 - Receives messages from multiple devices
		 * 3 - Prints available device list to connect
		 * 4 - Sends message confirming or denying connection to devices
		 * 5 - Adds IoT to list of recognized devices
		 */
		showDiscoveryInitialOptions();
		app.discoveryStart();
		ThreadWriter tw = new ThreadWriter("Writer");
		tw.start();

		int opt = readOptionUntilValid();
		opt = executeDiscoveryOption(opt);
		
		tw.interrupt();
		app.discoveryFinish(opt);
		setOptionRangeInvalid();
	}
	
	protected abstract void showDiscoveryInitialOptions();
	
	public void showNewDiscoveredIot(String name) {
		showNewDiscoveryOption(name);
	}
	
	//i.e. show new discovered iot
	protected abstract void showNewDiscoveryOption(String name);
	
	//runs specific options if necessary. Returns the index of
	//selected iot if that's the case or -1 otherwise
	protected abstract int executeDiscoveryOption(int option);
	
	protected void listSelectedIots() {
		showListSelectedIotsOptions(app.getConnectedIots());
		
		int opt = readOptionUntilValid();
		opt = executeListSelectedIotsOption(opt);
		
		if (opt != -1) {
			listIotStandardFunctionalities(opt);
		}
		setOptionRangeInvalid();
	}
	
	class ThreadWriter extends Thread {
		//TODO: transfer to GUI
		volatile boolean finish = false;
		
		public ThreadWriter(String name) {
			super(name);
		}
		
		@Override
		public void run() {
			int i = 0;
			//STEP 3
			while (!finish) {
				String name = app.getNewDiscoveredDevice(i);
				if (name != null) {
					showNewDiscoveredIot(name);
					i++;
				}
			}
		}
		
		@Override
		public void interrupt() {
			finish = true;
			super.interrupt();
		}
	}
	
	protected abstract void showListSelectedIotsOptions(String[] connectedIots);
	
	//runs specific options if necessary. Returns the index of
	//selected iot if that's the case or -1 otherwise
	protected abstract int executeListSelectedIotsOption(int option);
	
	protected void listIotStandardFunctionalities(int iotIndex) {
		showListIotStandardFunctionalities(app.listSelectIotOptions());
		
		int opt = readOptionUntilValid();
		opt = executeListIotStandardFunctionalities(opt);
		
		if (opt != -1) {
			StringBuilder message = new StringBuilder();
			app.executeIotStandardFunctionality(iotIndex, opt, message);
			showMessage(message.toString());
		}
		setOptionRangeInvalid();
	}
	
	protected abstract void showListIotStandardFunctionalities(String[] functionalities);
	
	//runs specific options if necessary. Returns the index of
	//selected option if that's the case or -1 otherwise
	protected abstract int executeListIotStandardFunctionalities(int option);
	
	//Returns the index of selected specific functionality.
	//Returns -1 if the option selected was not a functionality (i.e. UI inner option)
	public int listIotSpecificFunctionalities(String[] functionalities) {
		showIotSpecificFunctionalities(functionalities);
		
		int opt = readOptionUntilValid();
		opt = executeListIotSpecificFunctionality(opt);
		
		setOptionRangeInvalid();
		return opt;
	}
	
	protected abstract void showIotSpecificFunctionalities(String[] functionalities);
	
	protected abstract int executeListIotSpecificFunctionality(int option);
}
