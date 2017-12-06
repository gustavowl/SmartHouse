package device;

import entities.Field;

public class BonusCard extends Device {
	
	
	

	public BonusCard() {
		super();
		setClassFields();
	}

	@Override
	public void setClassFields() {
		Field fieldTemp = new Field("Bonus", "int", "20");
		getFields().add(fieldTemp);
		
		Field fieldTemp2 = new Field("Cor", "String", "Laranja");
		getFields().add(fieldTemp);
		getFields().add(fieldTemp);
		getFields().add(fieldTemp2);


		
	}

}
