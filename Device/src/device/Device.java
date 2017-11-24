package device;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import entities.Field;
import entities.Log;
import interfaces.DevicesFields;

public abstract class Device implements DevicesFields{
	
	private static int interval;
	
	public String productID; 
	public Boolean ligada;
	public int id;
	public String nome;
	public String tipo;
	public ArrayList<Log> logs;
	public ArrayList<Field> fields;
	
	Device() {
		tipo = "";
		logs = new ArrayList<Log>();
		fields = new ArrayList<Field>();
		setFieldsValues();
		
	}
	
	public void setFieldsValues() {
		//Adiciona no array de Fieldds cada atributo da classe
		for(java.lang.reflect.Field fieldClass : this.getClass().getFields()) {
			System.out.println(fieldClass.getName());
			Field fieldTemp = new Field(fieldClass.getName(), " " + fieldClass.getType(), " ");

			switch(fieldTemp.getNome()) {
				
			case "productID":
				fieldTemp.setValor(getProductID());
				break;
			case "ligada":
				fieldTemp.setValor(""+getLigada());
				break;
			case "id":
				fieldTemp.setValor(""+getId());
				break;
			case "nome":
				fieldTemp.setValor(getNome());
				break;
			case "tipo":	
				fieldTemp.setValor(getTipo());
				break;
			default:
				fieldTemp.setValor("null");
			}
			fields.add(fieldTemp);
		}
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
		
		//Atualiza o nome no objeto Field de Device
		for(Field fieldTemp : getFields()) {
			if(fieldTemp.getNome() == "nome") {
				fieldTemp.setValor(nome);
			}
		}
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

	public static int getInterval() {
		return interval;
	}

	public static void setInterval(int interval) {
		Device.interval = interval;
	}

	public ArrayList<Field> getFields() {
		return fields;
	}

	public void setFields(ArrayList<Field> fields) {
		this.fields = fields;
	}

}
