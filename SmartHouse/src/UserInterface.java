
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
		return readOptionUntilValid("Error: Invalid option selected. Rechoice!");
	}
	
	protected int readOptionUntilValid(String message) {
		int opt;
		while (true) {
			opt = readOption();
			
			if (isOptionWithinRange(opt)) {
				return opt;
			}
			
			showErrorMessage(message);
		}
	}
	
	protected abstract void showErrorMessage(String message);
	
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

		int opt = readOptionUntilValid();
		opt = executeDiscoveryOption(opt);
		
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
}
