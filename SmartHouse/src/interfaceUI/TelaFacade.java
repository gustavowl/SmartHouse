package interfaceUI;
import java.util.ArrayList;

import framework.IotManager;
import framework.UserInterface;
import testeCam.VerMidia;

public class TelaFacade extends UserInterface {
	TelaInicial telaInicial;
	DeviceUI deviceUi;
	AddDevice addDevice;
	SobreDevice sobreDevice;
	JanelaLampada janelaLamapada;
	VerMidia verMidia;
	ArrayList<String> connectedDevices;
	boolean discovery;
	String status;
	
	
	int opcao;
	private boolean executeToggle;
	private boolean lampState;
	
	public TelaFacade() {
		this(new IotManager(new ReceiverSocket(), new SenderSocket()));
		opcao = -1;
	}

	public TelaFacade(IotManager im) {
		super(im);
		telaInicial = new TelaInicial();
		connectedDevices = new ArrayList<>();
		discovery = false;
		status = null;
		executeToggle = false;
		lampState = false;
	}

	@Override
	protected void showInitialOptions() {
		telaInicial.setVisible(true);
		telaInicial.setTelaFacade(this);
		validOptionMin = 1;
		validOptionMax = 3;
	}

	@Override
	protected int readOption() {
		if (!(executeToggle && opcao != 5)) {
			while(opcao == -1) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					/*System.out.println("confirmandooo");
					e.printStackTrace();*/
					//addDevice.dispose();
					//System.out.println(opcao);
				}
			}
			int ret = opcao;
			opcao = -1;
			return ret;
		}
		if (status.indexOf("OFF") > -1) {
			//ligar lampada
			lampState = true;
			return 1;
		}
		//desligar a lampada
		lampState = false;
		return 2;
		
	}

	@Override
	protected void showMessage(String message) {
		status = message;
		
	}

	@Override
	protected void showErrorMessage(String errorMessage) {
		new ErrorWindow(true);
		
	}

	@Override
	protected boolean executeInitialOption(int option) {
		switch (option) {
			case 1:
				deviceUi = new DeviceUI();
				deviceUi.setVisible(true);
				deviceUi.setTelaFacade(this);
				for (String nome : connectedDevices) {
					deviceUi.addNoModel(nome);
				}
				validOptionMin = 0;
				validOptionMax = 5;
				lerDeviceOptions();
				deviceUi.setVisible(false);
				deviceUi.dispose();
				break;
				
			case 2:
				
				break;
				
			case 3:
				
				break;
				
			default:
			
		}
		return false;
	}

	@Override
	protected void showDiscoveryInitialOptions() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void showNewDiscoveryOption(String name) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected int executeDiscoveryOption(int option) {
		return option - 1;
	}

	@Override
	protected void showListSelectedIotsOptions(String[] connectedIots) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected int executeListSelectedIotsOption(int option) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	protected void showListIotStandardFunctionalities(String[] functionalities) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected int executeListIotStandardFunctionalities(int option) {
		
		return option - 1;
	}

	@Override
	protected void showIotSpecificFunctionalities(String[] functionalities) {
		validOptionMin = 0;
		validOptionMax = functionalities.length;
	}

	@Override
	protected int executeListIotSpecificFunctionality(int option) {
		return option - 1;
	}

	public int getOpcao() {
		return opcao;
	}

	public void setOpcao(int opcao) {
		this.opcao = opcao;
	}
	
	public void lerDeviceOptions() {
		int opt = readOptionUntilValid();
		
		switch (opt) {
			case 0: 
				//Botar p fechar so o atual
				break;
				
			case 1:
				addDevice =  new AddDevice(deviceUi);
				addDevice.setVisible(true);
				addDevice.setTelaFacade(this);
				executeDiscovery();
/*				if(opcao > -1) {
					connectedDevices.add(addDevice.getNovoDevice());
				}*/
				
				break;
				
			case 2:
				opcao = 4;
				listIotStandardFunctionalities(deviceUi.getList().getSelectedIndex());
				System.out.println("CONTINUE");
				validOptionMin = 0;
				validOptionMax = 5;
				opcao = 5;
				executeToggle = true;
				listIotStandardFunctionalities(deviceUi.getList().getSelectedIndex());
				janelaLamapada = new JanelaLampada();
				janelaLamapada.setLampState(lampState);
				janelaLamapada.setVisible(true);
				try {
					Thread.sleep(2000);
				}
				catch (Exception e) {
					
				}
				janelaLamapada.dispose();
				executeToggle = false;
				break;
				
			case 3:
				sobreDevice = new SobreDevice();
				opcao = 4;
				listIotStandardFunctionalities(deviceUi.getList().getSelectedIndex());
				sobreDevice.setUi(status);
				sobreDevice.setVisible(true);
				break;
			case 4:
				verMidia = new VerMidia();
				verMidia.setVisible(true);
				break;

			//Remove Device
			case 5:
				opcao = 1;
				int index = deviceUi.getList().getSelectedIndex();
				listIotStandardFunctionalities(index);
				connectedDevices.remove(index);
				deviceUi.getList().remove(index);
				break;	
				
		}
		
	}

	@Override
	protected String[] readArgs(String[] argsDescription) {
		// TODO Auto-generated method stub
		return new String[0];
	}
	
	public void showNewDiscoveredIot(String name) {
		addDevice.addDeviceComboBox(name);
	}
	
	public void addConnectedDevice(String name) {
		connectedDevices.add(name);
	}
	
/*	public void showFields(int index) {
		listIotStandardFunctionalities(index);
	}*/

}
