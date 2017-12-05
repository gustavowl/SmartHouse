package framework;
import java.util.regex.Pattern;


public abstract class UserInterface {
	
	protected IotManager iotManager;
	protected int validOptionMin;
	protected int validOptionMax;
	
	public UserInterface(IotManager iotManager) {
		this.iotManager = iotManager;
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
		iotManager.discoveryStart();
		ThreadWriter tw = new ThreadWriter("Writer");
		tw.start();

		int opt = readOptionUntilValid();
		opt = executeDiscoveryOption(opt);
		
		tw.interrupt();
		iotManager.discoveryFinish(opt);
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
		showListSelectedIotsOptions(iotManager.getConnectedIots());
		
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
				String name = iotManager.getNewDiscoveredDevice(i);
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
		showListIotStandardFunctionalities(iotManager.listSelectIotOptions());
		
		int opt = readOptionUntilValid();
		opt = executeListIotStandardFunctionalities(opt);
		
		if (opt != -1) {
			StringBuilder message = new StringBuilder();
			if (iotManager.executeIotStandardFunctionality(iotIndex, opt, message)) {
				String[] iotFunctionalities = message.toString().split(
						Pattern.quote(ProtocolMessage.getSeparator()));
				
				listIotSpecificFunctionalities(iotFunctionalities, iotIndex);
			}
			else {
				showMessage(message.toString());
			}
		}
		setOptionRangeInvalid();
	}
	
	protected abstract void showListIotStandardFunctionalities(String[] functionalities);
	
	//runs specific options if necessary. Returns the index of
	//selected option if that's the case or -1 otherwise
	protected abstract int executeListIotStandardFunctionalities(int option);
	
	//Returns the index of selected specific functionality.
	//Returns -1 if the option selected was not a functionality (i.e. UI inner option)
	public void listIotSpecificFunctionalities(String[] functionalities, int funcIndex) {
		showIotSpecificFunctionalities(functionalities);
		
		int opt = readOptionUntilValid();
		opt = executeListIotSpecificFunctionality(opt);
		
		if (opt != -1) {
			String str = functionalities[opt];
			str = str.substring(str.indexOf('(') + 1, str.indexOf(')'));
			String[] argsDescription;
			if (!str.equals("")) {
				 argsDescription = str.split(",");
			}
			else {
				argsDescription = new String[0];
			}
			
			str = functionalities[opt];
			str = str.substring(0, str.indexOf('('));
			
			String[] args = readArgs(argsDescription);
			
			String str_args = "(";
			int length_minus_one = args.length - 1;
			if (length_minus_one >= 0) {
				for (int i = 0; i < length_minus_one; i++) {
					str_args += args[i] + ",";
				}
				str_args += args[length_minus_one];
			}
			str_args += ")";
			
			showMessage(iotManager.executeIotSpecificFunctionality(funcIndex,
					functionalities[opt], str_args));
		}
		
		setOptionRangeInvalid();
	}
	
	protected abstract void showIotSpecificFunctionalities(String[] functionalities);
	
	protected abstract int executeListIotSpecificFunctionality(int option);
	
	protected abstract String[] readArgs(String[] argsDescription);
}
