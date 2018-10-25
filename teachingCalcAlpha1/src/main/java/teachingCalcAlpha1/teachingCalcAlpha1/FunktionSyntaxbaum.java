package teachingCalcAlpha1.teachingCalcAlpha1;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class FunktionSyntaxbaum {
	
	char inhaltKnoten;
	FunktionSyntaxbaum linkesChild;
	FunktionSyntaxbaum rechtesChild;
	FunktionSyntaxbaum parent;
	int index;
	static FunktionSyntaxbaum root;
	
	
	public FunktionSyntaxbaum(){
//		parent = null;
//		linkesChild = null;
//		rechtesChild = null;
//		inhaltKnoten = 0;
	}


	public FunktionSyntaxbaum getParent() {
		return parent;
	}

	void setLinkesChild(FunktionSyntaxbaum neuesChild) {
		linkesChild = neuesChild;
	}

	void setRechtesChild(FunktionSyntaxbaum neuesChild) {
		rechtesChild = neuesChild;
	}

	FunktionSyntaxbaum getLinkesChild() {
		return linkesChild;
	}

	FunktionSyntaxbaum getRechtesChild() {
		return rechtesChild;
	}

	void setRoot(FunktionSyntaxbaum neuesRoot) {
		root = neuesRoot;
	}

	void setParent(FunktionSyntaxbaum neuerParent) {
		parent = neuerParent;
	}

	void  setInhalt(char neuerInhalt) {
		inhaltKnoten = neuerInhalt;
	}

	char getInhalt() {
		return inhaltKnoten;
	}

	void  setIndex(int neuerIndex) {
		index = neuerIndex;
	}

	int getIndex() {
		return index;
	}
}
