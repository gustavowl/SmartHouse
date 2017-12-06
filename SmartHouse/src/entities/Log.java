package entities;

import java.awt.EventQueue;
import java.util.Date;
import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import interfaceUI.LogUI;

public class Log {
	
	public static void main(String[] args) {
		Log a = new Log();
	}

		private String descricaoLog;
		private Date time;
		DateFormat dateFormat;
		
		public Log() {
			dateFormat = new SimpleDateFormat("[ yyyy/MM/dd HH:mm:ss ]");
			Calendar cal = Calendar.getInstance();
			time = (Date) cal.getTime();
		}

		public String getDescricaoLog() {
			return descricaoLog;
		}

		public void setDescricaoLog(String descricaoLog) {
			this.descricaoLog = descricaoLog;
		}

		public Date getTime() {
			return time;
		}

		public void setTime(Date time) {
			this.time = time;
		}
		
		public String printDateInformation() {
			return dateFormat.format(time);
		}
		
}
