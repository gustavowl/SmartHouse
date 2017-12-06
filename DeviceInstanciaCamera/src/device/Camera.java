package device;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;

import javax.media.CannotRealizeException;
import javax.media.CaptureDeviceInfo;
import javax.media.CaptureDeviceManager;
import javax.media.Manager;
import javax.media.MediaLocator;
import javax.media.NoPlayerException;
import javax.media.Player;

import Interfaces.ConexaoMidia;

public class Camera extends Device  implements ConexaoMidia  {

	public Camera() {
		super();
	}
	
	@Override
	public void setClassFields() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void conexaoImagem(String dispositivoSelecionado, Player player) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void conexaoAudio(String dispositivoSelecionado, Player player) {
		CaptureDeviceInfo device = CaptureDeviceManager.getDevice(dispositivoSelecionado);
		
		MediaLocator localizador = device.getLocator();
		try {
			player = Manager.createRealizedPlayer(localizador);
		} catch (NoPlayerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CannotRealizeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	@Override
	public void conexaoFilme(String dispositivoSelecionado, Player player) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ArrayList<String> getMidiasDisponiveis() {
		//Pega os dispositivos detectados no PC e colocar num Vector 
		ArrayList<String> nomesDispositivos = new ArrayList<>();
				Vector listaDispositivos = null;
				listaDispositivos = CaptureDeviceManager.getDeviceList(null);
				for(int i = 0; i<listaDispositivos.size(); i++) {
					CaptureDeviceInfo info = (CaptureDeviceInfo) listaDispositivos.get(i);
					String  nomeDispositivo = info.getName().toString();
					
					if(nomeDispositivo.indexOf("image") != -1 || nomeDispositivo.indexOf("Image") == -1) {
						nomesDispositivos.add(nomeDispositivo);
					}
				}
		return nomesDispositivos;
	}

}
