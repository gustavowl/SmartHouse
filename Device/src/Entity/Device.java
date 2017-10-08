package Entity;

import java.util.Timer;
import java.util.TimerTask;

//Ask: what each device knows [?]
public abstract class Device {
	
	public static int interval; //what is this[?]
	
	private String productID; 
	private Boolean ligada; 	//does every device have a on/off state?
	private int id; 		//why int[?] no need if it's not used for artithmetic operations 
	private String nome;

	//Onde está o construtor [?]
	
	//zera o timer caso ativo
		//Funciona mesmo/como esperado [?]
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
	
	public void toggleLigar() { //pra q [?]
		
	}
	
	public void toggleDesligar() { //pra q [?]
		
	}

    private static int setInterval() { //but y [?]

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
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}
    
 	
}
