package teachingCalcAlpha1.teachingCalcAlpha1;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class FunktionAlsVektorSyntaxbaum {
	
	@XmlElement
	static FunktionAlsVektorSyntaxbaum rootVectoren;
	@XmlElement
	char inhaltKnotenSymbol;
	@XmlElement
	String inhaltKnotenString;
	@XmlElement
	ArrayList<Double> inhaltKnotenVektor;
	@XmlElement
	FunktionAlsVektorSyntaxbaum linkesChild;
	@XmlElement
	FunktionAlsVektorSyntaxbaum rechtesChild;
	@XmlElement
	FunktionAlsVektorSyntaxbaum unaeresChild;
	@XmlElement
	FunktionAlsVektorSyntaxbaum parent;
	@XmlElement
	int index;


	FunktionAlsVektorSyntaxbaum(){
//		parent = null;
//		linkesChild = null;
//		rechtesChild = null;
//		inhaltKnotenSymbol = 0;
//		inhaltKnotenVektor = null;
	}
	
	void setLinkesChild(FunktionAlsVektorSyntaxbaum neuesLinkesChild){
		linkesChild=neuesLinkesChild;
	}
	
	void setRechtesChild(FunktionAlsVektorSyntaxbaum neuesRechtesChild){
		rechtesChild = neuesRechtesChild;
	}
	
	void setUnaeresChild(FunktionAlsVektorSyntaxbaum neuesUnaeresChild){
		unaeresChild = neuesUnaeresChild;
	}
	
	void setParent(FunktionAlsVektorSyntaxbaum neuerParent){
		parent = neuerParent;
	}
	
	FunktionAlsVektorSyntaxbaum getParent(){
		return parent;
	}
	
	void setRoot(FunktionAlsVektorSyntaxbaum neuerRootknoten){
		rootVectoren =neuerRootknoten;
	}
	
	FunktionAlsVektorSyntaxbaum getRoot(){
		return rootVectoren;
	}
	
	FunktionAlsVektorSyntaxbaum getLinkesChild(){
		return linkesChild;
	}
	
	FunktionAlsVektorSyntaxbaum getRechtesChild(){
		return rechtesChild;
	}
	
	FunktionAlsVektorSyntaxbaum getUnaeresChild(){
		return unaeresChild;
	}
	
	void setInhalt(char neuerInhalt){
		inhaltKnotenSymbol = neuerInhalt;
	}
	
	void setInhalt(ArrayList<Double> neuerInhalt){
		inhaltKnotenVektor = neuerInhalt;
	}
	
	void setInhalt(String neuerInhalt){
		inhaltKnotenString = neuerInhalt;
	}
	
	char getInhaltTChar(){
		return inhaltKnotenSymbol;
	}
	
	ArrayList<Double> getInhaltVector(){
		return inhaltKnotenVektor;
	}
	
	String getInhaltString(){
		return inhaltKnotenString;
	}
	
	void setIndex(int neuerIindex){
		index = neuerIindex;
	}
	
	int getIndex(){
		return index;
	}

}
