package Interfaces;

import java.util.ArrayList;
import java.util.Vector;

import javax.media.Player;

public interface ConexaoMidia {
	
	public ArrayList<String> getMidiasDisponiveis();
	
	public void conexaoImagem(String dispositivoSelecionado, Player player);

	public void conexaoAudio(String dispositivoSelecionado, Player player);
	
	public void conexaoFilme(String dispositivoSelecionado, Player player);

}
