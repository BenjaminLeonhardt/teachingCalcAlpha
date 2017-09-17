
package application;

import javafx.beans.property.SimpleStringProperty;

public class Datei {
	private SimpleStringProperty dateiname;
	private SimpleStringProperty groesse;
	private SimpleStringProperty fortschritt;

	public Datei(){
		
	}

	public Datei(SimpleStringProperty dateiname, SimpleStringProperty fortschritt,
			SimpleStringProperty groesse) {
		super();
		this.dateiname = dateiname;
		this.fortschritt = fortschritt;
		long grInt=0;
		try{
			grInt = Long.parseLong(groesse.get());
		}catch(Exception e){
			grInt=0;
		}
		if(grInt>1024){
			grInt=grInt/1024;
		}else{
			grInt=1;
		}
		String tmp = String.valueOf(grInt);
		String groesseString = "";
		for(int i=tmp.length()-1,j=0;i>=0;i--,j++){
			if(j%3==0){
				if(j>1){
					groesseString+=".";
				}			
			}
			groesseString+=tmp.charAt(i);
		}
		tmp="";
		for(int i=groesseString.length()-1;i>=0;i--){
			tmp+=groesseString.charAt(i);
		}
		this.groesse = new SimpleStringProperty(tmp + " Kb");
		
	}

	public String getDateiname() {
		return dateiname.get();
	}

	public void setDateiname(SimpleStringProperty dateiname) {
		this.dateiname = dateiname;
	}

	public String getFortschritt() {
		return fortschritt.get();
	}

	public void setFortschritt(SimpleStringProperty fortschritt) {
		this.fortschritt = fortschritt;
	}

	public String getGroesse() {
		return groesse.get();
	}

	public void setGroesse(SimpleStringProperty groesse) {
		this.groesse = groesse;
	}
	
}
