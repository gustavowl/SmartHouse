package device;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import entities.Log;

public abstract class Device {
	
	public static int interval;
	
	private String productID; 
	private Boolean ligada;
	private int id;
	private String nome;
	private String tipo;
	private ArrayList<Log> logs;
	
	Device() {
		tipo = "";
		logs = new ArrayList<Log>();
	}
	
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
                    System.out.println("Time finished");
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

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getNome() {
		if(nome == null || nome.equals("")) {
			nome = this.getClass().getName();
		}
		return nome;
		
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public ArrayList<Log> getLogs() {
		return logs;
	}

	public void setLogs(ArrayList<Log> logs) {
		this.logs = logs;
	}
	
 	
}
