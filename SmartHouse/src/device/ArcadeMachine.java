package device;

import entities.Field;

public class ArcadeMachine extends Device {
	
	

	public ArcadeMachine() {
		super();
		setClassFields();
	}

	@Override
	public void setClassFields() {
		Field fieldTemp = new Field("Tipo de Jogo", "String", "Arcade");

		Field fieldTemp2 = new Field("wattage", "int", "110");

		Field fieldTemp3 = new Field("Cor", "String ", "Preto");
		getFields().add(fieldTemp);
		getFields().add(fieldTemp2);
		getFields().add(fieldTemp3);

		
	}

}
