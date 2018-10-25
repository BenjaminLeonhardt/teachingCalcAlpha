package teachingCalcAlpha1.teachingCalcAlpha1;

import java.util.ArrayList;

public class RechnerLibrary {

	public RechnerLibrary() {

	}

	int[] laengeUndKlammerFinden(int rechteGrenze, boolean hatKlammern, String functionAlsStringLokal,
			boolean schonEntfernt) {
		int i = 0;
		for (i = schonEntfernt == false ? 1 : 0; i < functionAlsStringLokal.length(); i++) {
			if (functionAlsStringLokal.charAt(i) == '(') {
				hatKlammern = true;
			}
		}
		int [] returnArray = new int[2];
		returnArray[0] = hatKlammern==true?1:0;
		returnArray[1] = rechteGrenze = i;
		return returnArray;
	}
	
	FunktionAlsVektorSyntaxbaum knotenAnhaengen(boolean links, FunktionAlsVektorSyntaxbaum aktuellerKnoten, String functionAlsStringLokal,int i) {
		FunktionAlsVektorSyntaxbaum neuerKnoten = new FunktionAlsVektorSyntaxbaum();
		neuerKnoten.setParent(aktuellerKnoten);
		neuerKnoten.setInhalt(functionAlsStringLokal.charAt(i));
		neuerKnoten.setInhalt(functionAlsStringLokal);
		neuerKnoten.setIndex(i);
		if (links) {
			aktuellerKnoten.setLinkesChild(neuerKnoten);
			
		}
		else {
			aktuellerKnoten.setRechtesChild(neuerKnoten);
		}
		return neuerKnoten;
	}
	
	String entferneUnnoetigeKlammern(String functionAlsStringLokal) {
		int rechteGrenze = 0;
		boolean hatKlammern = true;


		int klammerZaehlerAuf = 0;
		int klammerZaehlerZu = 0;
		int klammerLevel = 0;
		int indexErsteKlammerAuf = -1;
		int indexErsteKlammerZu = -1;
				
		int[] laengeUndKlammerReturnArray = laengeUndKlammerFinden(rechteGrenze, hatKlammern, functionAlsStringLokal, false);
		hatKlammern = laengeUndKlammerReturnArray[0]==1?true:false;
		rechteGrenze = laengeUndKlammerReturnArray[1];

		hatKlammern = false;
		for (int i = 0; i < rechteGrenze; i++) {
			if (functionAlsStringLokal.charAt(i) == '(') {
				hatKlammern = true;
			}
		}
		String functionAlsStringOhneKlammern=null;
		boolean hatteUnnoetigeKlammern = false;
		if (rechteGrenze > 2) {
			functionAlsStringOhneKlammern = new String();
			
			if (hatKlammern) {
				for (int i = 0; i < rechteGrenze; i++) {
					if (functionAlsStringLokal.charAt(i) == '(') {
						if (indexErsteKlammerAuf == -1) { // (x^3+2)/(x^2+4) passt
							indexErsteKlammerAuf = i;	// ((x^3+2)+4*x^3) 
						}								// (x^3+2+(4*x^3))
						klammerZaehlerAuf++;			// (x^3+2+4*x^2) passt
						klammerLevel++;					// x^3*(2*x^3)+4
					}									// x^3*((x^3+2)+4*x^3) 
					if (functionAlsStringLokal.charAt(i) == ')') {	// ((x^3+2)+4*x^3)*3*x
						if (indexErsteKlammerZu == -1) {
							indexErsteKlammerZu = i;
						}
						klammerZaehlerZu++;
						klammerLevel--;
					}
					/*if (klammerZaehlerAuf == klammerZaehlerZu && klammerLevel == 0 && klammerZaehlerAuf == 1 && klammerZaehlerZu == 1) {
						int g = 0;
						for (int j = indexErsteKlammerAuf+1; j < indexErsteKlammerZu; j++) {
							functionAlsStringOhneKlammern[g++] = functionAlsStringLokal[j];
						}
						functionAlsStringOhneKlammern[g++] = 0;
						break;
					}*/
					if (klammerZaehlerAuf == klammerZaehlerZu && klammerLevel == 0 && indexErsteKlammerAuf == 0 && indexErsteKlammerZu == rechteGrenze - 1) {						
						functionAlsStringOhneKlammern = functionAlsStringLokal.substring(indexErsteKlammerAuf + 1,indexErsteKlammerZu);
						hatteUnnoetigeKlammern = true;
					}

				}
			}
			else {
				return functionAlsStringLokal;
			}
		}
		/*for (int i = 0; i < rechteGrenze; i++) { 
			if (functionAlsString.charAt(i) == '(') {
				if (indexErsteKlammerAuf - 1) { // (x^3+2)/(x^2+4) passt
					indexErsteKlammerAuf = i;	// ((x^3+2)+4*x^3) 
				}								// (x^3+2+(4*x^3))
				klammerZaehlerAuf++;			// (x^3+2+4*x^2) passt
				klammerLevel++;					// x^3*(2*x^3)+4
			}									// x^3*((x^3+2)+4*x^3) 
			if (functionAlsString.charAt(i) == ')') {	// ((x^3+2)+4*x^3)*3*x
				if (indexErsteKlammerZu - 1) {
					indexErsteKlammerZu = i;
				}
				klammerZaehlerZu++;
				klammerLevel--;
			}
			if (klammerZaehlerAuf == klammerZaehlerZu && klammerLevel == 0 && klammerZaehlerAuf == 1 && klammerZaehlerZu == 1) {
				int g = 0;
				for (int j = indexErsteKlammerAuf; j < indexErsteKlammerZu; j++) {
					functionAlsStringOhneKlammern[g++] = functionAlsStringLokal[j];
				}
			}
		}*/
		if (hatteUnnoetigeKlammern) {
			return functionAlsStringOhneKlammern;
		} else {
			return functionAlsStringLokal;
		}
	}
	
	void stringSplitten(int i, int rechteGrenze, int klammerLevel, String functionAlsStringLokal, FunktionAlsVektorSyntaxbaum neuerKnoten) {
		String substringLinks;
		String substringRechts;
		substringLinks = functionAlsStringLokal.substring(0,i);
		substringRechts = functionAlsStringLokal.substring(i + 1,klammerLevel == 0 ? rechteGrenze : rechteGrenze - 1);

		substringLinks = entferneUnnoetigeKlammern(substringLinks);
		substringRechts = entferneUnnoetigeKlammern(substringRechts);
		linkerPartGebrochenRational(i, 0, neuerKnoten, substringLinks);
		rechterPartGebrochenRational(i, 0, neuerKnoten, substringRechts);
	}
	
	void vectorAnhaengen(String functionAlsStringLokal, FunktionAlsVektorSyntaxbaum neuerKnoten) {
		FunktionSyntaxbaum neuerFunktionSyntaxbaum = new FunktionSyntaxbaum();
		parseFuntionBuffer(neuerFunktionSyntaxbaum, functionAlsStringLokal);
		ArrayList<Double> vectorKnoten= new ArrayList<Double>();
		ausSyntaxbaumVektorErstellen(neuerFunktionSyntaxbaum, vectorKnoten);
		neuerKnoten.setInhalt(vectorKnoten);
	}
	
	void linkerPartGebrochenRational(int index, int vorzeichenLevel, FunktionAlsVektorSyntaxbaum aktuellerKnoten, String functionAlsStringLokal) {
		if (index == 0) {
			return;
		}
		int rechteGrenze = 0;

		boolean hatKlammern = false;
		int[] laengeUndKlammerReturnArray = laengeUndKlammerFinden(rechteGrenze, hatKlammern, functionAlsStringLokal, true);
		hatKlammern = laengeUndKlammerReturnArray[0]==1?true:false;
		rechteGrenze = laengeUndKlammerReturnArray[1];
		
		int klammerZaehlerAuf = 0;
		int klammerZaehlerZu = 0;
		int klammerLevel = 0;
		boolean gefunden = false;
		while (!gefunden) {
			klammerZaehlerAuf = 0;
			klammerZaehlerZu = 0;
			klammerLevel = 0;
			for (int i = 0; i < rechteGrenze; i++) {
				if (functionAlsStringLokal.charAt(i) == '(') {
					klammerZaehlerAuf++;
					klammerLevel++;
				}
				if (functionAlsStringLokal.charAt(i) == ')') {
					klammerZaehlerZu++;
					klammerLevel--;
				}
				if (((klammerZaehlerAuf == 1 && klammerZaehlerZu == 0) && klammerLevel == 1 && functionAlsStringLokal.charAt(0) == '(') || ((klammerZaehlerAuf == 0 && klammerZaehlerZu == 0) && klammerLevel == 0)) {
					if (vorzeichenLevel == 0) {
						if (functionAlsStringLokal.charAt(i) == '+' || functionAlsStringLokal.charAt(i) == '-') {
							if (i == 0) {

							}
							else if (functionAlsStringLokal.charAt(i - 1) == '+' || functionAlsStringLokal.charAt(i - 1) == '-' || functionAlsStringLokal.charAt(i - 1) == '*' || functionAlsStringLokal.charAt(i - 1) == '/') {

							}
							else {
								gefunden = true;
								FunktionAlsVektorSyntaxbaum neuerLinkerKnoten = knotenAnhaengen(true, aktuellerKnoten, functionAlsStringLokal, i);
								if (hatKlammern) {
									stringSplitten(i, rechteGrenze, klammerLevel, functionAlsStringLokal, neuerLinkerKnoten);
								}
								else {
									vectorAnhaengen(functionAlsStringLokal, neuerLinkerKnoten);
								}
								return;
							}
						}
					}
					else if (vorzeichenLevel == 1) {
						if (functionAlsStringLokal.charAt(i) == '*' || functionAlsStringLokal.charAt(i) == '/') {
							gefunden = true;
							FunktionAlsVektorSyntaxbaum neuerLinkerKnoten = knotenAnhaengen(true, aktuellerKnoten, functionAlsStringLokal, i);
							if (hatKlammern) {
								stringSplitten(i, rechteGrenze, klammerLevel, functionAlsStringLokal, neuerLinkerKnoten);
							}
							else {
								vectorAnhaengen(functionAlsStringLokal, neuerLinkerKnoten);
							}
							return;
						}
					}
					else if (vorzeichenLevel == 2) {
						if (functionAlsStringLokal.charAt(i) == '^') {
							gefunden = true;
							FunktionAlsVektorSyntaxbaum neuerLinkerKnoten = knotenAnhaengen(true, aktuellerKnoten, functionAlsStringLokal, i);
							if (hatKlammern) {
								stringSplitten(i, rechteGrenze, klammerLevel, functionAlsStringLokal, neuerLinkerKnoten);
							}
							else {
								vectorAnhaengen(functionAlsStringLokal, neuerLinkerKnoten);
							}
							return;
						}
					}
					else if (vorzeichenLevel == 3) {
						if ((functionAlsStringLokal.charAt(i) >= '0' && functionAlsStringLokal.charAt(i) <= '9') || functionAlsStringLokal.charAt(i) == 'x' || functionAlsStringLokal.charAt(i) == 'X') {
							gefunden = true;
							FunktionAlsVektorSyntaxbaum neuerLinkerKnoten = knotenAnhaengen(true, aktuellerKnoten, functionAlsStringLokal, i);
							vectorAnhaengen(functionAlsStringLokal, neuerLinkerKnoten);
							return;
						}
					}
				}
			}
			if (vorzeichenLevel > 3) {
				return;
			}
			vorzeichenLevel++;
		}
	}
	
	void rechterPartGebrochenRational(int index, int vorzeichenLevel, FunktionAlsVektorSyntaxbaum aktuellerKnoten, String functionAlsStringLokal) {
		if (index == 0) {
			return;
		}
		boolean gefunden = false;
		int klammerZaehlerAuf = 0;
		int klammerZaehlerZu = 0;
		int klammerLevel = 0;
		int rechteGrenze = 0;
		boolean hatKlammern = false;
		int[] laengeUndKlammerReturnArray = laengeUndKlammerFinden(rechteGrenze, hatKlammern, functionAlsStringLokal, true);
		hatKlammern = laengeUndKlammerReturnArray[0]==1?true:false;
		rechteGrenze = laengeUndKlammerReturnArray[1];
		
		while (!gefunden) {
			klammerZaehlerAuf = 0;
			klammerZaehlerZu = 0;
			klammerLevel = 0;
			for (int i = 0; i < rechteGrenze; i++) {
				if (functionAlsStringLokal.charAt(i) == '(') {
					klammerZaehlerAuf++;
					klammerLevel++;
				}
				if (functionAlsStringLokal.charAt(i) == ')') {
					klammerZaehlerZu++;
					klammerLevel--;
				}
				if (((klammerZaehlerAuf == 1 && klammerZaehlerZu == 0) && klammerLevel == 1 && functionAlsStringLokal.charAt(0) == '(') || ((klammerZaehlerAuf == 0 && klammerZaehlerZu == 0) && klammerLevel == 0)) {
					if (vorzeichenLevel == 0) {
						if (functionAlsStringLokal.charAt(i) == '+' || functionAlsStringLokal.charAt(i) == '-') {
							if (i == 0) {

							}
							else if (functionAlsStringLokal.charAt(i-1) == '+' || functionAlsStringLokal.charAt(i-1) == '-' || functionAlsStringLokal.charAt(i-1) == '*' || functionAlsStringLokal.charAt(i-1) == '/') {

							}
							else {
								gefunden = true;
								FunktionAlsVektorSyntaxbaum neuerRechterKnoten = knotenAnhaengen(false, aktuellerKnoten, functionAlsStringLokal, i);
								if (hatKlammern) {
									stringSplitten(i, rechteGrenze, klammerLevel, functionAlsStringLokal, neuerRechterKnoten);
								}
								else {
									vectorAnhaengen(functionAlsStringLokal, neuerRechterKnoten);
								}
								return;
							}
						}
					}
					else if (vorzeichenLevel == 1) {
						if (functionAlsStringLokal.charAt(i) == '*' || functionAlsStringLokal.charAt(i) == '/') {
							gefunden = true;
							FunktionAlsVektorSyntaxbaum neuerRechterKnoten = knotenAnhaengen(false, aktuellerKnoten, functionAlsStringLokal, i);
							if (hatKlammern) {
								stringSplitten(i, rechteGrenze, klammerLevel, functionAlsStringLokal, neuerRechterKnoten);
							}
							else {
								vectorAnhaengen(functionAlsStringLokal, neuerRechterKnoten);
							}
							return;
						}
					}
					else if (vorzeichenLevel == 2) {
						if (functionAlsStringLokal.charAt(i) == '^') {
							gefunden = true;
							FunktionAlsVektorSyntaxbaum neuerRechterKnoten = knotenAnhaengen(false, aktuellerKnoten, functionAlsStringLokal, i);
							if (hatKlammern) {
								stringSplitten(i, rechteGrenze, klammerLevel, functionAlsStringLokal, neuerRechterKnoten);
							}
							else {
								vectorAnhaengen(functionAlsStringLokal, neuerRechterKnoten);
							}
							return;
						}
					}
					else if (vorzeichenLevel == 3) {
						if ((functionAlsStringLokal.charAt(i) >= '0' && functionAlsStringLokal.charAt(i) <= '9') || functionAlsStringLokal.charAt(i) == 'x' || functionAlsStringLokal.charAt(i) == 'X') {
							gefunden = true;
							FunktionAlsVektorSyntaxbaum neuerRechterKnoten = knotenAnhaengen(false, aktuellerKnoten, functionAlsStringLokal, i);
							vectorAnhaengen(functionAlsStringLokal, neuerRechterKnoten);
							return;
						}
					}
				}
			}
			if (vorzeichenLevel > 3) {
				return;
			}
			vorzeichenLevel++;
		}
	}

	void linkerPart(int index, int vorzeichenLevel, int linkeGrenze, int rechteGrenze, FunktionSyntaxbaum aktuellerKnoten, String functionAlsStringLokal) {
		if (index == 0) {
			return;
		}
		boolean gefunden = false;
		while (!gefunden) {
			for (int i = rechteGrenze - 1; i >= linkeGrenze; i--) {
				if (vorzeichenLevel == 0) {
					if (functionAlsStringLokal.charAt(i) == '+' || functionAlsStringLokal.charAt(i) == '-') {
						if (i == 0) {

						}
						else if (functionAlsStringLokal.charAt(i-1) == '+' || functionAlsStringLokal.charAt(i-1) == '-' || functionAlsStringLokal.charAt(i-1) == '*' || functionAlsStringLokal.charAt(i-1) == '/') {

						}
						else {
							gefunden = true;
							FunktionSyntaxbaum neuerLinkerKnoten = new FunktionSyntaxbaum();
							neuerLinkerKnoten.setParent(aktuellerKnoten);
							neuerLinkerKnoten.setInhalt(functionAlsStringLokal.charAt(i));
							neuerLinkerKnoten.setIndex(i);
							aktuellerKnoten.setLinkesChild(neuerLinkerKnoten);
							linkerPart(i, vorzeichenLevel, linkeGrenze, aktuellerKnoten.getIndex(), neuerLinkerKnoten, functionAlsStringLokal);
							rechterPart(i, vorzeichenLevel, aktuellerKnoten.getIndex() + 1, rechteGrenze, neuerLinkerKnoten, functionAlsStringLokal);
						}
					}
				}
				else if (vorzeichenLevel == 1) {
					if (functionAlsStringLokal.charAt(i) == '*' || functionAlsStringLokal.charAt(i) == '/') {
						gefunden = true;
						FunktionSyntaxbaum neuerLinkerKnoten = new FunktionSyntaxbaum();
						neuerLinkerKnoten.setParent(aktuellerKnoten);
						neuerLinkerKnoten.setInhalt(functionAlsStringLokal.charAt(i));
						neuerLinkerKnoten.setIndex(i);
						aktuellerKnoten.setLinkesChild(neuerLinkerKnoten);
						linkerPart(i, vorzeichenLevel, linkeGrenze, neuerLinkerKnoten.getIndex(), neuerLinkerKnoten, functionAlsStringLokal);
						rechterPart(i, vorzeichenLevel, neuerLinkerKnoten.getIndex() + 1, rechteGrenze, neuerLinkerKnoten, functionAlsStringLokal);
						i = 0;
					}
				}
				else if (vorzeichenLevel == 2) {
					if (functionAlsStringLokal.charAt(i) == '^') {
						gefunden = true;
						FunktionSyntaxbaum neuerLinkerKnoten = new FunktionSyntaxbaum();
						neuerLinkerKnoten.setParent(aktuellerKnoten);
						neuerLinkerKnoten.setInhalt(functionAlsStringLokal.charAt(i));
						neuerLinkerKnoten.setIndex(i);
						aktuellerKnoten.setLinkesChild(neuerLinkerKnoten);
						linkerPart(i, vorzeichenLevel, linkeGrenze, neuerLinkerKnoten.getIndex(), neuerLinkerKnoten, functionAlsStringLokal);
						rechterPart(i, vorzeichenLevel, neuerLinkerKnoten.getIndex() + 1, rechteGrenze, neuerLinkerKnoten, functionAlsStringLokal);
						i = 0;
					}
				}
				else if (vorzeichenLevel == 3) {
					if ((functionAlsStringLokal.charAt(i) >= '0' && functionAlsStringLokal.charAt(i) <= '9') || functionAlsStringLokal.charAt(i) == 'x' || functionAlsStringLokal.charAt(i) == 'X') {
						gefunden = true;
						FunktionSyntaxbaum neuerLinkerKnoten = new FunktionSyntaxbaum();
						neuerLinkerKnoten.setParent(aktuellerKnoten);
						neuerLinkerKnoten.setInhalt(functionAlsStringLokal.charAt(i));
						neuerLinkerKnoten.setIndex(i);
						aktuellerKnoten.setLinkesChild(neuerLinkerKnoten);
						linkerPart(i, vorzeichenLevel, linkeGrenze, neuerLinkerKnoten.getIndex(), neuerLinkerKnoten, functionAlsStringLokal);
						rechterPart(i, vorzeichenLevel, neuerLinkerKnoten.getIndex() + 1, rechteGrenze, neuerLinkerKnoten, functionAlsStringLokal);
						i = 0;
					}
				}
			}
			if (vorzeichenLevel > 3) {
				return;
			}
			vorzeichenLevel++;
		}
	}
	
	void rechterPart(int index, int vorzeichenLevel, int linkeGrenze, int rechteGrenze, FunktionSyntaxbaum aktuellerKnoten, String functionAlsStringLokal) {
		if (index == functionAlsStringLokal.length()) {
			return;
		}
		boolean gefunden = false;
		while (!gefunden) {
			for (int i = linkeGrenze; i <= rechteGrenze; i++) {
				if (vorzeichenLevel == 0) {
					if (functionAlsStringLokal.charAt(i) == '+' || functionAlsStringLokal.charAt(i) == '-') {
						if (i == 0) {

						}
						else if (functionAlsStringLokal.charAt(i-1) == '+' || functionAlsStringLokal.charAt(i-1) == '-' || functionAlsStringLokal.charAt(i-1) == '*' || functionAlsStringLokal.charAt(i-1) == '/') {

						}
						else {
							gefunden = true;
							FunktionSyntaxbaum neuerRechterKnoten = new FunktionSyntaxbaum();
							neuerRechterKnoten.setParent(aktuellerKnoten);
							neuerRechterKnoten.setInhalt(functionAlsStringLokal.charAt(i));
							neuerRechterKnoten.setIndex(i);
							aktuellerKnoten.setRechtesChild(neuerRechterKnoten);
							linkerPart(i, vorzeichenLevel, linkeGrenze, neuerRechterKnoten.getIndex(), neuerRechterKnoten, functionAlsStringLokal);
							rechterPart(i, vorzeichenLevel, neuerRechterKnoten.getIndex() + 1, rechteGrenze, neuerRechterKnoten, functionAlsStringLokal);
						}
					}
				}
				else if (vorzeichenLevel == 1) {
					if (functionAlsStringLokal.charAt(i) == '^') {
						gefunden = true;
						FunktionSyntaxbaum neuerRechterKnoten = new FunktionSyntaxbaum();
						neuerRechterKnoten.setParent(aktuellerKnoten);
						neuerRechterKnoten.setInhalt(functionAlsStringLokal.charAt(i));
						neuerRechterKnoten.setIndex(i);
						aktuellerKnoten.setRechtesChild(neuerRechterKnoten);
						linkerPart(i, vorzeichenLevel, linkeGrenze, neuerRechterKnoten.getIndex(), neuerRechterKnoten, functionAlsStringLokal);
						rechterPart(i, vorzeichenLevel, neuerRechterKnoten.getIndex() + 1, rechteGrenze, neuerRechterKnoten, functionAlsStringLokal);
						i = functionAlsStringLokal.length();
					}
				}
				else if (vorzeichenLevel == 2) {
					if (functionAlsStringLokal.charAt(i) == '*' || functionAlsStringLokal.charAt(i) == '/') {
						gefunden = true;
						FunktionSyntaxbaum neuerRechterKnoten = new FunktionSyntaxbaum();
						neuerRechterKnoten.setParent(aktuellerKnoten);
						neuerRechterKnoten.setInhalt(functionAlsStringLokal.charAt(i));
						neuerRechterKnoten.setIndex(i);
						aktuellerKnoten.setRechtesChild(neuerRechterKnoten);
						linkerPart(i, vorzeichenLevel, linkeGrenze, neuerRechterKnoten.getIndex(), neuerRechterKnoten, functionAlsStringLokal);
						rechterPart(i, vorzeichenLevel, neuerRechterKnoten.getIndex() + 1, rechteGrenze, neuerRechterKnoten, functionAlsStringLokal);
						i = functionAlsStringLokal.length();
					}
				}
				else if (vorzeichenLevel == 3) {
					if ((functionAlsStringLokal.charAt(i) >= '0' && functionAlsStringLokal.charAt(i) <= '9') || functionAlsStringLokal.charAt(i) == 'x' || functionAlsStringLokal.charAt(i) == 'X') {
						gefunden = true;
						FunktionSyntaxbaum neuerRechterKnoten = new FunktionSyntaxbaum();
						neuerRechterKnoten.setParent(aktuellerKnoten);
						neuerRechterKnoten.setInhalt(functionAlsStringLokal.charAt(i));
						neuerRechterKnoten.setIndex(i);
						aktuellerKnoten.setRechtesChild(neuerRechterKnoten);
						linkerPart(i, vorzeichenLevel, linkeGrenze, neuerRechterKnoten.getIndex(), neuerRechterKnoten, functionAlsStringLokal);
						rechterPart(i, vorzeichenLevel, neuerRechterKnoten.getIndex() + 1, rechteGrenze, neuerRechterKnoten, functionAlsStringLokal);
						i = functionAlsStringLokal.length();
					}
				}
			}
			if (vorzeichenLevel > 3) {
				return;
			}
			vorzeichenLevel++;
		}
	}
	
	void parseFuntionBuffer(FunktionSyntaxbaum neuerFunktionSyntaxbaum,String functionAlsString) {//todo algorithmus vervollständigen. könnte evtl komplett ersetzt werden durch den Shunting-yard-Algorithmus. Ableiten ist damit allerdings nicht möglich, da nur mit dem string gearbeitet wird
		//neuerFunktionSyntaxbaum = new FunktionSyntaxbaum();//todo funktionen wie log() sqrt() pow() implementieren
		neuerFunktionSyntaxbaum.setLinkesChild(null);//todo algorithmus ausgiebieg auf fehler/richtigkeit überprüfen
		neuerFunktionSyntaxbaum.setRechtesChild(null);
		boolean gefunden = false;
		int vorzeichenLevel = 0;
		int rechteGrenze = functionAlsString.length()-1;
		boolean hatKlammern = false;

		for (int i = 0; i < functionAlsString.length(); i++) {
			if (functionAlsString.charAt(i) == '(') {
				hatKlammern = true;
			}
		}
		while (!gefunden) {
			if (rechteGrenze == 0) {
				neuerFunktionSyntaxbaum.setRoot(neuerFunktionSyntaxbaum);
				neuerFunktionSyntaxbaum.setInhalt(functionAlsString.charAt(0));
				neuerFunktionSyntaxbaum.setLinkesChild(null);
				neuerFunktionSyntaxbaum.setRechtesChild(null);
				neuerFunktionSyntaxbaum.setIndex(0);
				
			}
			for (int i = 0; i < rechteGrenze; i++) { // ^ , vorzeichen, * /, + -
				if (vorzeichenLevel == 0) {
					if (functionAlsString.charAt(i) == '+' || functionAlsString.charAt(i) == '-') {
						if (i == 0) {

						}
						else if (functionAlsString.charAt(i-1) == '+' || functionAlsString.charAt(i-1) == '-' || functionAlsString.charAt(i-1) == '*' || functionAlsString.charAt(i-1) == '/') {

						}
						else {
							gefunden = true;
							neuerFunktionSyntaxbaum.setRoot(neuerFunktionSyntaxbaum);
							neuerFunktionSyntaxbaum.setInhalt(functionAlsString.charAt(i));
							neuerFunktionSyntaxbaum.setIndex(i);
							linkerPart(i, 0, 0, i, neuerFunktionSyntaxbaum, functionAlsString);
							rechterPart(i, 0, i + 1, rechteGrenze, neuerFunktionSyntaxbaum, functionAlsString);
							i = rechteGrenze;

						}
					}
				}
				else if (vorzeichenLevel == 1) {
					if (functionAlsString.charAt(i) == '*' || functionAlsString.charAt(i) == '/') {
						gefunden = true;
						neuerFunktionSyntaxbaum.setRoot(neuerFunktionSyntaxbaum);
						neuerFunktionSyntaxbaum.setInhalt(functionAlsString.charAt(i));
						neuerFunktionSyntaxbaum.setIndex(i);
						linkerPart(i, 1, 0, i, neuerFunktionSyntaxbaum, functionAlsString);
						rechterPart(i, 1, i + 1, rechteGrenze, neuerFunktionSyntaxbaum, functionAlsString);
						i = rechteGrenze;
					}
				}
				else if (vorzeichenLevel == 2) {
					if (functionAlsString.charAt(i) == '^') {
						gefunden = true;
						neuerFunktionSyntaxbaum.setRoot(neuerFunktionSyntaxbaum);
						neuerFunktionSyntaxbaum.setInhalt(functionAlsString.charAt(i));
						neuerFunktionSyntaxbaum.setIndex(i);
						linkerPart(i, 2, 0, i, neuerFunktionSyntaxbaum, functionAlsString);
						rechterPart(i, 2, i + 1, rechteGrenze, neuerFunktionSyntaxbaum, functionAlsString);
						i = rechteGrenze;
					}
				}
				else if (vorzeichenLevel == 3) {
					if ((functionAlsString.charAt(i) >= '0' && functionAlsString.charAt(i) <= '9') || functionAlsString.charAt(i) == 'x' || functionAlsString.charAt(i) == 'X') {
						gefunden = true;
						neuerFunktionSyntaxbaum.setRoot(neuerFunktionSyntaxbaum);
						neuerFunktionSyntaxbaum.setInhalt(functionAlsString.charAt(i));
						neuerFunktionSyntaxbaum.setIndex(i);
						linkerPart(i, 2, 0, i, neuerFunktionSyntaxbaum, functionAlsString);
						rechterPart(i, 2, i + 1, rechteGrenze, neuerFunktionSyntaxbaum, functionAlsString);
						i = rechteGrenze;
					}
				}
			}
			if (vorzeichenLevel > 3) {
				return;
			}
			vorzeichenLevel++;
		}
	}
	
	FunktionAlsVektorSyntaxbaum splitFunctionBufferGebrochenRational(FunktionAlsVektorSyntaxbaum neuerFunktionSyntaxbaumGebrochenRational, String functionAlsString){
		neuerFunktionSyntaxbaumGebrochenRational = new FunktionAlsVektorSyntaxbaum();
		neuerFunktionSyntaxbaumGebrochenRational.setLinkesChild(null);
		neuerFunktionSyntaxbaumGebrochenRational.setRechtesChild(null);
		boolean gefunden = false;
		int vorzeichenLevel = 0;
		int rechteGrenze = 0;
		int klammerZaehlerAuf = 0;
		int klammerZaehlerZu = 0;
		int klammerLevel = 0;
		boolean hatKlammern = false;
		laengeUndKlammerFinden(rechteGrenze, hatKlammern, functionAlsString, false);

		while (!gefunden) {
			if (rechteGrenze == 0) {
				neuerFunktionSyntaxbaumGebrochenRational.setRoot(neuerFunktionSyntaxbaumGebrochenRational);
				neuerFunktionSyntaxbaumGebrochenRational.setInhalt(functionAlsString.charAt(0));
				neuerFunktionSyntaxbaumGebrochenRational.setLinkesChild(null);
				neuerFunktionSyntaxbaumGebrochenRational.setRechtesChild(null);
				neuerFunktionSyntaxbaumGebrochenRational.setUnaeresChild(null);
			}
			for (int i = 0; i < rechteGrenze; i++) { // ^ , vorzeichen, * /, + -
				if (functionAlsString.charAt(i) == '(') {
					klammerZaehlerAuf++;
					klammerLevel++;
				}
				if (functionAlsString.charAt(i) == ')') {
					klammerZaehlerZu++;
					klammerLevel--;
				}
				if ((klammerZaehlerAuf == klammerZaehlerZu) && klammerLevel == 0) {
					if (vorzeichenLevel == 0) {
						if (functionAlsString.charAt(i) == '+' || functionAlsString.charAt(i) == '-') {
							if (i == 0) {

							}
							else if (functionAlsString.charAt(i - 1) == '+' || functionAlsString.charAt(i - 1) == '-' || functionAlsString.charAt(i - 1) == '*' || functionAlsString.charAt(i - 1) == '/') {

							}
							else {
								gefunden = true;
								neuerFunktionSyntaxbaumGebrochenRational.setRoot(neuerFunktionSyntaxbaumGebrochenRational);
								neuerFunktionSyntaxbaumGebrochenRational.setInhalt(functionAlsString.charAt(i));
								neuerFunktionSyntaxbaumGebrochenRational.setInhalt(functionAlsString);
								if (hatKlammern) {
									String substringLinks=null;
									String substringRechts=null;
									int g = 0;
									substringLinks = functionAlsString.substring(klammerLevel == 0 ? 0 : 1, i);
									/*for (int j = klammerLevel == 0 ? 0 : 1; j < i; j++, g++) {										
										substringLinks.charAt(g) = functionAlsString.charAt(j);
									}
									substringLinks.charAt(g) = 0; g = 0;*/
									substringLinks = functionAlsString.substring(i+1, klammerLevel == 0 ? rechteGrenze : rechteGrenze - 1);
									/*int bis = klammerLevel == 0 ? rechteGrenze : rechteGrenze - 1;
									for (int j = i + 1; j < bis; j++, g++) {
										substringRechts.charAt(g) = functionAlsString.charAt(j);
									}
									substringRechts.charAt(g) = 0;*/
									linkerPartGebrochenRational(i, 0, neuerFunktionSyntaxbaumGebrochenRational, substringLinks);
									rechterPartGebrochenRational(i, 0, neuerFunktionSyntaxbaumGebrochenRational, substringRechts);
								}
								else {
									FunktionSyntaxbaum neuerFunktionSyntaxbaum=null;
									parseFuntionBuffer(neuerFunktionSyntaxbaum, functionAlsString);
									ArrayList<Double> vectorKnoten=new ArrayList<Double>();
									ausSyntaxbaumVektorErstellen(neuerFunktionSyntaxbaum, vectorKnoten);
									neuerFunktionSyntaxbaumGebrochenRational.setInhalt(vectorKnoten);
								}
								i = rechteGrenze;

							}
						}
					}
					else if (vorzeichenLevel == 1) {
						if (functionAlsString.charAt(i) == '*' || functionAlsString.charAt(i) == '/') {
							gefunden = true;
							neuerFunktionSyntaxbaumGebrochenRational.setRoot(neuerFunktionSyntaxbaumGebrochenRational);
							neuerFunktionSyntaxbaumGebrochenRational.setInhalt(functionAlsString.charAt(i));
							neuerFunktionSyntaxbaumGebrochenRational.setInhalt(functionAlsString);
							if (hatKlammern) {
							
								String substringLinks;
								String substringRechts;
								substringLinks = functionAlsString.substring(functionAlsString.charAt(0) == '(' ? 1 : 0,(functionAlsString.charAt(i-1) == ')' ? i - 1 : i));
								substringRechts = functionAlsString.substring(functionAlsString.charAt(i + 1) == '(' ? i + 2 : i + 1,functionAlsString.charAt(rechteGrenze-1) == ')' ? rechteGrenze - 1 : rechteGrenze);							
								
								linkerPartGebrochenRational(i, 0, neuerFunktionSyntaxbaumGebrochenRational, substringLinks); //wenn 2 klammern gefunden, vorzeichenlevel wieder auf 0 für in der klammer
								rechterPartGebrochenRational(i, 0, neuerFunktionSyntaxbaumGebrochenRational, substringRechts);
							}
							else {
								FunktionSyntaxbaum neuerFunktionSyntaxbaum = new FunktionSyntaxbaum();
								parseFuntionBuffer(neuerFunktionSyntaxbaum, functionAlsString);
								ArrayList<Double> vectorKnoten = new ArrayList<Double>();
								ausSyntaxbaumVektorErstellen(neuerFunktionSyntaxbaum, vectorKnoten);
								neuerFunktionSyntaxbaumGebrochenRational.setInhalt(vectorKnoten);
							}
							i = rechteGrenze;
						}
					}
					else if (vorzeichenLevel == 2) {
						if (functionAlsString.charAt(i) == '^') {
							gefunden = true;
							neuerFunktionSyntaxbaumGebrochenRational.setRoot(neuerFunktionSyntaxbaumGebrochenRational);
							neuerFunktionSyntaxbaumGebrochenRational.setInhalt(functionAlsString.charAt(i));
							neuerFunktionSyntaxbaumGebrochenRational.setInhalt(functionAlsString);
							if (hatKlammern) {
								String substringLinks;
								String substringRechts;
								substringLinks = functionAlsString.substring(klammerLevel == 0 ? 0 : 1,i);
								substringRechts = functionAlsString.substring(i + 1,klammerLevel == 0 ? rechteGrenze : rechteGrenze - 1);

								linkerPartGebrochenRational(i, 0, neuerFunktionSyntaxbaumGebrochenRational, substringLinks);
								rechterPartGebrochenRational(i, 0, neuerFunktionSyntaxbaumGebrochenRational, substringRechts);
							}else {
								FunktionSyntaxbaum neuerFunktionSyntaxbaum = new FunktionSyntaxbaum();
								parseFuntionBuffer(neuerFunktionSyntaxbaum, functionAlsString);
								ArrayList<Double> vectorKnoten = new ArrayList<Double>();
								ausSyntaxbaumVektorErstellen(neuerFunktionSyntaxbaum, vectorKnoten);
								neuerFunktionSyntaxbaumGebrochenRational.setInhalt(vectorKnoten);
							}
							i = rechteGrenze;
						}
					}
					else if (vorzeichenLevel == 3) {
						if ((functionAlsString.charAt(i) >= '0' && functionAlsString.charAt(i) <= '9') || functionAlsString.charAt(i) == 'x' || functionAlsString.charAt(i) == 'X') {
							gefunden = true;
							neuerFunktionSyntaxbaumGebrochenRational.setRoot(neuerFunktionSyntaxbaumGebrochenRational);
							neuerFunktionSyntaxbaumGebrochenRational.setInhalt(functionAlsString.charAt(i));
							neuerFunktionSyntaxbaumGebrochenRational.setInhalt(functionAlsString);
							FunktionSyntaxbaum neuerFunktionSyntaxbaum = new FunktionSyntaxbaum();
							parseFuntionBuffer(neuerFunktionSyntaxbaum, functionAlsString);
							ArrayList<Double> vectorKnoten = new ArrayList<Double>();
							ausSyntaxbaumVektorErstellen(neuerFunktionSyntaxbaum, vectorKnoten);
							neuerFunktionSyntaxbaumGebrochenRational.setInhalt(vectorKnoten);
							/*String substringLinks = new WCHAR.charAt(i);
							String substringRechts = new WCHAR[rechteGrenze - i];
							int g = 0;
							for (int j = klammerLevel == 0 ? 0 : 1; j < i; j++, g++) {
								substringLinks.charAt(g) = functionAlsString.charAt(j);
							}substringLinks.charAt(g) = 0; g = 0;
							int bis = klammerLevel == 0 ? rechteGrenze : rechteGrenze - 1;
							for (int j = i + 1; j < bis; j++, g++) {
								substringRechts.charAt(g) = functionAlsString.charAt(j);
							}substringRechts.charAt(g) = 0;
							linkerPartGebrochenRational(i, 0, &neuerFunktionSyntaxbaumGebrochenRational, substringLinks);
							rechterPartGebrochenRational(i, 0, &neuerFunktionSyntaxbaumGebrochenRational, substringRechts);
							i = rechteGrenze;*/
						}
					}
					if (vorzeichenLevel > 3) {
						return null;
					}
				}
			}
			vorzeichenLevel++;
		}
		return null;
	}

	void ausSyntaxbaumVektorErstellen(FunktionSyntaxbaum aktuellerKnoten, ArrayList<Double> functionAlsVectorLokal) {
		if (aktuellerKnoten.getLinkesChild() == null && aktuellerKnoten.getRechtesChild() == null) {
			if (aktuellerKnoten.getInhalt() == 'x' || aktuellerKnoten.getInhalt() == 'X') {
				if (functionAlsVectorLokal.size() < 2) {
					for(int i=0;i<2;i++){
						functionAlsVectorLokal.add(0.0);
					}
//functionAlsVectorLokal.resize(2);
				}
				functionAlsVectorLokal.set(1, functionAlsVectorLokal.get(1)+1);
//functionAlsVectorLokal[1] += 1;	
			}
			else if (aktuellerKnoten.getInhalt() >= '0' || aktuellerKnoten.getInhalt() <= '9') {
				if (functionAlsVectorLokal.size() < 1) {
					functionAlsVectorLokal.add(0.0);
//functionAlsVectorLokal.resize(1);
				}
				functionAlsVectorLokal.set(0, functionAlsVectorLokal.get(0) + aktuellerKnoten.getInhalt() - 48);
//functionAlsVectorLokal[0] += aktuellerKnoten.getInhalt() - 48;
			}
		}
		if (aktuellerKnoten.getLinkesChild() != null) {
			if (aktuellerKnoten.getLinkesChild().getLinkesChild() != null) {
				ausSyntaxbaumVektorErstellen(aktuellerKnoten.getLinkesChild(), functionAlsVectorLokal);
			}
		}
		if (aktuellerKnoten.getRechtesChild() != null) {
			if (aktuellerKnoten.getRechtesChild().getRechtesChild() != null&&(aktuellerKnoten.getInhalt() == '+' || aktuellerKnoten.getInhalt() == '^' || aktuellerKnoten.getInhalt() == '*' )) {
				ausSyntaxbaumVektorErstellen(aktuellerKnoten.getRechtesChild(), functionAlsVectorLokal);
			}
		
			if (aktuellerKnoten.getRechtesChild().getRechtesChild() != null && aktuellerKnoten.getInhalt() == '-' ) {
				ausSyntaxbaumVektorErstellen(aktuellerKnoten.getRechtesChild(), functionAlsVectorLokal);
			}
		}
		if (aktuellerKnoten.getInhalt() == '^') {
			if ((aktuellerKnoten.getLinkesChild().getInhalt() == 'x' || aktuellerKnoten.getLinkesChild().getInhalt() == 'X') && (aktuellerKnoten.getRechtesChild().getInhalt() >= '1' && aktuellerKnoten.getRechtesChild().getInhalt() <= '9')) {
				int hochZahl = aktuellerKnoten.getRechtesChild().getInhalt() - 48;
				if (functionAlsVectorLokal.size() < hochZahl + 1) {
					for(int i=0;i<hochZahl+1;i++){
						functionAlsVectorLokal.add(0.0);
					}
//functionAlsVectorLokal.resize(hochZahl + 1);
				}
				double multiplyer = 1.0;
				if (aktuellerKnoten.getParent() != null) {
					if (aktuellerKnoten.getParent().getInhalt() == '*' && aktuellerKnoten.getParent().getRechtesChild() == aktuellerKnoten) {
						multiplyer = aktuellerKnoten.getParent().getLinkesChild().getInhalt() - 48;
					}
				}
				boolean vorzeichen = false;
				FunktionSyntaxbaum next = aktuellerKnoten;
				while (next.getParent() != null) {
					/*if ((next.getParent().getInhalt() == '+'|| next.getParent().getInhalt() == '-') && next.getParent().getLinkesChild() == next) {
						next = next.getParent();
					}
					else if ((next.getParent().getInhalt() == '*' || next.getParent().getInhalt() == '/')) {
						next = next.getParent();
					}*/
					
					if ((next.getParent().getInhalt() == '+' || next.getParent().getInhalt() == '-') && next.getParent().getRechtesChild() == next) {
						if (next.getParent().getInhalt() == '+') {
							vorzeichen = false;
							break;
						}
						else if (next.getParent().getInhalt() == '-') {
							vorzeichen = true;
							break;
						}
					}
					else {
						next = next.getParent();
					}
				}
				//if (aktuellerKnoten.getParent().getRechtesChild().getInhalt() >= '1' && aktuellerKnoten.getParent().getRechtesChild().getInhalt() >= '9' && aktuellerKnoten.getParent().getParent().getInhalt() == '-') {				
				if(vorzeichen){
					functionAlsVectorLokal.set(hochZahl,functionAlsVectorLokal.get(hochZahl) - multiplyer);
				}
				else {
					functionAlsVectorLokal.set(hochZahl,functionAlsVectorLokal.get(hochZahl) + multiplyer);
				}			
			}
		}

		if (aktuellerKnoten.getInhalt() == '*') {
			if ((aktuellerKnoten.getRechtesChild().getInhalt() == 'x' || aktuellerKnoten.getRechtesChild().getInhalt() == 'X') && (aktuellerKnoten.getLinkesChild().getInhalt() >= '1' && aktuellerKnoten.getLinkesChild().getInhalt() <= '9')) {

				if (functionAlsVectorLokal.size() < 2) {
					for(int i=0;i<2;i++){
						functionAlsVectorLokal.add(0.0);
					}
//functionAlsVectorLokal.resize(2);
				}
				if (aktuellerKnoten.getParent() != null) {
					if (aktuellerKnoten.getParent().getParent() != null) {
						if (aktuellerKnoten.getParent().getRechtesChild().getInhalt() >= '1' && aktuellerKnoten.getParent().getRechtesChild().getInhalt() <= '9' && aktuellerKnoten.getParent().getParent().getInhalt() == '-') {
							functionAlsVectorLokal.set(1,functionAlsVectorLokal.get(1) - (aktuellerKnoten.getLinkesChild().getInhalt() - 48));
//functionAlsVectorLokal[1] -= aktuellerKnoten.getLinkesChild().getInhalt() - 48;
						}
						if (aktuellerKnoten.getParent().getRechtesChild().getInhalt() >= '1' && aktuellerKnoten.getParent().getRechtesChild().getInhalt() <= '9' && aktuellerKnoten.getParent().getParent().getInhalt() == '+') { //hier könnte noch was falsch sein
							functionAlsVectorLokal.set(1,functionAlsVectorLokal.get(1) + aktuellerKnoten.getLinkesChild().getInhalt() - 48);
//functionAlsVectorLokal[1] += aktuellerKnoten.getLinkesChild().getInhalt() - 48;
						}
					}
				
					else if (aktuellerKnoten.getParent().getInhalt() == '-' && aktuellerKnoten.getParent().getRechtesChild() == aktuellerKnoten) {
						functionAlsVectorLokal.set(1,functionAlsVectorLokal.get(1) - aktuellerKnoten.getLinkesChild().getInhalt() - 48);
//functionAlsVectorLokal[1] -= aktuellerKnoten.getLinkesChild().getInhalt() - 48;
					}
				}
				else {
					functionAlsVectorLokal.set(1,functionAlsVectorLokal.get(1) + aktuellerKnoten.getLinkesChild().getInhalt() - 48);
//functionAlsVectorLokal[1] += aktuellerKnoten.getLinkesChild().getInhalt() - 48;
				}			
			}
		}
		if (aktuellerKnoten.getInhalt() == '+') {
			if (aktuellerKnoten.getLinkesChild().getInhalt() == '^' && (aktuellerKnoten.getRechtesChild().getInhalt() >= '1' && aktuellerKnoten.getRechtesChild().getInhalt() <= '9')) {
				if (functionAlsVectorLokal.size() < 1) {
					functionAlsVectorLokal.add(0.0);
//functionAlsVectorLokal.resize(1);
				}
				functionAlsVectorLokal.set(0,functionAlsVectorLokal.get(0) + aktuellerKnoten.getRechtesChild().getInhalt() - 48);
//functionAlsVectorLokal[0] += aktuellerKnoten.getRechtesChild().getInhalt() - 48;
			}
			if (aktuellerKnoten.getLinkesChild().getInhalt() == '*' && (aktuellerKnoten.getRechtesChild().getInhalt() >= '1' && aktuellerKnoten.getRechtesChild().getInhalt() <= '9')) {
				if (functionAlsVectorLokal.size() < 1) {
					functionAlsVectorLokal.add(0.0);
//functionAlsVectorLokal.resize(1);
				}
				functionAlsVectorLokal.set(0,functionAlsVectorLokal.get(0) + aktuellerKnoten.getRechtesChild().getInhalt() - 48);
//functionAlsVectorLokal[0] += aktuellerKnoten.getRechtesChild().getInhalt() - 48;
			}
			if (aktuellerKnoten.getLinkesChild().getInhalt() == 'x' || aktuellerKnoten.getLinkesChild().getInhalt() == 'X' && (aktuellerKnoten.getRechtesChild().getInhalt() >= '1' && aktuellerKnoten.getRechtesChild().getInhalt() <= '9')) {
				if (functionAlsVectorLokal.size() < 2) {
					for(int i=0;i<2;i++){
						functionAlsVectorLokal.add(0.0);
					}
//functionAlsVectorLokal.resize(2);
				}
				functionAlsVectorLokal.set(0,functionAlsVectorLokal.get(0) + aktuellerKnoten.getRechtesChild().getInhalt() - 48);
//functionAlsVectorLokal[0] += aktuellerKnoten.getRechtesChild().getInhalt() - 48;
				functionAlsVectorLokal.set(1,functionAlsVectorLokal.get(1) + 1);
//functionAlsVectorLokal[1] += 1;
			}
			if ((aktuellerKnoten.getLinkesChild().getInhalt() >= '1' && aktuellerKnoten.getLinkesChild().getInhalt() <= '9') && (aktuellerKnoten.getRechtesChild().getInhalt() >= '1' && aktuellerKnoten.getRechtesChild().getInhalt() <= '9')) {
				if (functionAlsVectorLokal.size() < 1) {
					functionAlsVectorLokal.add(0.0);
//functionAlsVectorLokal.resize(1);
				}
				functionAlsVectorLokal.set(0,functionAlsVectorLokal.get(0) + (aktuellerKnoten.getLinkesChild().getInhalt() - 48) + (aktuellerKnoten.getRechtesChild().getInhalt() - 48));
//functionAlsVectorLokal[0] += (aktuellerKnoten.getLinkesChild().getInhalt() - 48) + (aktuellerKnoten.getRechtesChild().getInhalt() - 48);
			}
		}
		else if (aktuellerKnoten.getInhalt() == '-') {
			if (aktuellerKnoten.getLinkesChild().getInhalt() == '^' && (aktuellerKnoten.getRechtesChild().getInhalt() >= '1' && aktuellerKnoten.getRechtesChild().getInhalt() <= '9')) {
				if (functionAlsVectorLokal.size() < 1) {
					functionAlsVectorLokal.add(0.0);
//functionAlsVectorLokal.resize(1);
				}
				functionAlsVectorLokal.set(0,functionAlsVectorLokal.get(0) - aktuellerKnoten.getRechtesChild().getInhalt() - 48);
//functionAlsVectorLokal[0] -= aktuellerKnoten.getRechtesChild().getInhalt() - 48;
			}
			if (aktuellerKnoten.getLinkesChild().getInhalt() == '*' && (aktuellerKnoten.getRechtesChild().getInhalt() >= '1' && aktuellerKnoten.getRechtesChild().getInhalt() <= '9')) {
				if (functionAlsVectorLokal.size() < 1) {
					functionAlsVectorLokal.add(0.0);
//functionAlsVectorLokal.resize(1);
				}
				functionAlsVectorLokal.set(0,functionAlsVectorLokal.get(0) - aktuellerKnoten.getRechtesChild().getInhalt() - 48);
//functionAlsVectorLokal[0] -= aktuellerKnoten.getRechtesChild().getInhalt() - 48;
			}
			if (aktuellerKnoten.getLinkesChild().getInhalt() == 'x' || aktuellerKnoten.getLinkesChild().getInhalt() == 'X' && (aktuellerKnoten.getRechtesChild().getInhalt() >= '1' && aktuellerKnoten.getRechtesChild().getInhalt() <= '9')) {
				if (functionAlsVectorLokal.size() < 2) {
					for(int i=0;i<2;i++){
						functionAlsVectorLokal.add(0.0);
					}
//functionAlsVectorLokal.resize(2);
				}
				functionAlsVectorLokal.set(0,functionAlsVectorLokal.get(0) - aktuellerKnoten.getRechtesChild().getInhalt() - 48);
//functionAlsVectorLokal[0] -= aktuellerKnoten.getRechtesChild().getInhalt() - 48;
				functionAlsVectorLokal.set(1,functionAlsVectorLokal.get(1) + 1);
//functionAlsVectorLokal[1] += 1;
			}
		}
	}
	
	void splitFuntionBufferGebrochenRational(FunktionAlsVektorSyntaxbaum neuerFunktionSyntaxbaumGebrochenRational, String functionAlsString) {
		//neuerFunktionSyntaxbaumGebrochenRational = new FunktionAlsVektorSyntaxbaum();
		neuerFunktionSyntaxbaumGebrochenRational.setLinkesChild(null);
		neuerFunktionSyntaxbaumGebrochenRational.setRechtesChild(null);
		boolean gefunden = false;
		int vorzeichenLevel = 0;
		int rechteGrenze = 0;
		int klammerZaehlerAuf = 0;
		int klammerZaehlerZu = 0;
		int klammerLevel = 0;
		boolean hatKlammern = false;
		int[] laengeUndKlammerReturnArray = laengeUndKlammerFinden(rechteGrenze, hatKlammern, functionAlsString, false);
		hatKlammern = laengeUndKlammerReturnArray[0]==1?true:false;
		rechteGrenze = laengeUndKlammerReturnArray[1];
		

		while (!gefunden) {
			if (rechteGrenze == 0) {
				neuerFunktionSyntaxbaumGebrochenRational.setRoot(neuerFunktionSyntaxbaumGebrochenRational);
				neuerFunktionSyntaxbaumGebrochenRational.setInhalt(functionAlsString.charAt(0));
				neuerFunktionSyntaxbaumGebrochenRational.setLinkesChild(null);
				neuerFunktionSyntaxbaumGebrochenRational.setRechtesChild(null);
				neuerFunktionSyntaxbaumGebrochenRational.setUnaeresChild(null);
			}
			for (int i = 0; i < rechteGrenze; i++) { // ^ , vorzeichen, * /, + -
				if (functionAlsString.charAt(i) == '(') {
					klammerZaehlerAuf++;
					klammerLevel++;
				}
				if (functionAlsString.charAt(i) == ')') {
					klammerZaehlerZu++;
					klammerLevel--;
				}
				if ((klammerZaehlerAuf == klammerZaehlerZu) && klammerLevel == 0) {
					if (vorzeichenLevel == 0) {
						if (functionAlsString.charAt(i) == '+' || functionAlsString.charAt(i) == '-') {
							if (i == 0) {

							}
							else if (functionAlsString.charAt(i - 1) == '+' || functionAlsString.charAt(i - 1) == '-' || functionAlsString.charAt(i - 1) == '*' || functionAlsString.charAt(i - 1) == '/') {

							}
							else {
								gefunden = true;
								neuerFunktionSyntaxbaumGebrochenRational.setRoot(neuerFunktionSyntaxbaumGebrochenRational);
								neuerFunktionSyntaxbaumGebrochenRational.setInhalt(functionAlsString.charAt(i));
								neuerFunktionSyntaxbaumGebrochenRational.setInhalt(functionAlsString);
								if (hatKlammern) {
									String substringLinks = new String();
									String substringRechts = new String();
									substringLinks = functionAlsString.substring(klammerLevel == 0 ? 0 : 1,i);
									substringRechts = functionAlsString.substring(i + 1,klammerLevel == 0 ? rechteGrenze : rechteGrenze - 1);

									linkerPartGebrochenRational(i, 0, neuerFunktionSyntaxbaumGebrochenRational, substringLinks);
									rechterPartGebrochenRational(i, 0, neuerFunktionSyntaxbaumGebrochenRational, substringRechts);
								}
								else {
									FunktionSyntaxbaum neuerFunktionSyntaxbaum = new FunktionSyntaxbaum();
									parseFuntionBuffer(neuerFunktionSyntaxbaum, functionAlsString);
									ArrayList<Double> vectorKnoten = new ArrayList<Double>();
									ausSyntaxbaumVektorErstellen(neuerFunktionSyntaxbaum, vectorKnoten);
									neuerFunktionSyntaxbaumGebrochenRational.setInhalt(vectorKnoten);
								}
								i = rechteGrenze;

							}
						}
					}
					else if (vorzeichenLevel == 1) {
						if (functionAlsString.charAt(i) == '*' || functionAlsString.charAt(i) == '/') {
							gefunden = true;
							neuerFunktionSyntaxbaumGebrochenRational.setRoot(neuerFunktionSyntaxbaumGebrochenRational);
							neuerFunktionSyntaxbaumGebrochenRational.setInhalt(functionAlsString.charAt(i));
							neuerFunktionSyntaxbaumGebrochenRational.setInhalt(functionAlsString);
							if (hatKlammern) {
							
								String substringLinks = new String();
								String substringRechts = new String();
								substringLinks = functionAlsString.substring( functionAlsString.charAt(0) == '(' ? 1 : 0,(functionAlsString.charAt(i-1) == ')' ? i - 1 : i));
								substringRechts = functionAlsString.substring(functionAlsString.charAt(i+1) == '(' ? i + 2 : i + 1,functionAlsString.charAt(rechteGrenze-1) == ')' ? rechteGrenze - 1 : rechteGrenze);

								linkerPartGebrochenRational(i, 0, neuerFunktionSyntaxbaumGebrochenRational, substringLinks); //wenn 2 klammern gefunden, vorzeichenlevel wieder auf 0 für in der klammer
								rechterPartGebrochenRational(i, 0, neuerFunktionSyntaxbaumGebrochenRational, substringRechts);
							}
							else {
								FunktionSyntaxbaum neuerFunktionSyntaxbaum = new FunktionSyntaxbaum();
								parseFuntionBuffer(neuerFunktionSyntaxbaum, functionAlsString);
								ArrayList<Double> vectorKnoten = new ArrayList<Double>();
								ausSyntaxbaumVektorErstellen(neuerFunktionSyntaxbaum, vectorKnoten);
								neuerFunktionSyntaxbaumGebrochenRational.setInhalt(vectorKnoten);
							}
							i = rechteGrenze;
						}
					}
					else if (vorzeichenLevel == 2) {
						if (functionAlsString.charAt(i) == '^') {
							gefunden = true;
							neuerFunktionSyntaxbaumGebrochenRational.setRoot(neuerFunktionSyntaxbaumGebrochenRational);
							neuerFunktionSyntaxbaumGebrochenRational.setInhalt(functionAlsString.charAt(i));
							neuerFunktionSyntaxbaumGebrochenRational.setInhalt(functionAlsString);
							if (hatKlammern) {
								String substringLinks = new String();
								String substringRechts = new String();
								substringLinks = functionAlsString.substring(klammerLevel == 0 ? 0 : 1,i);
								substringRechts = functionAlsString.substring(i + 1,klammerLevel == 0 ? rechteGrenze : rechteGrenze - 1);
								
								linkerPartGebrochenRational(i, 0, neuerFunktionSyntaxbaumGebrochenRational, substringLinks);
								rechterPartGebrochenRational(i, 0, neuerFunktionSyntaxbaumGebrochenRational, substringRechts);
							}else {
								FunktionSyntaxbaum neuerFunktionSyntaxbaum = new FunktionSyntaxbaum();
								parseFuntionBuffer(neuerFunktionSyntaxbaum, functionAlsString);
								ArrayList<Double> vectorKnoten = new ArrayList<Double>();
								ausSyntaxbaumVektorErstellen(neuerFunktionSyntaxbaum, vectorKnoten);
								neuerFunktionSyntaxbaumGebrochenRational.setInhalt(vectorKnoten);
							}
							i = rechteGrenze;
						}
					}
					else if (vorzeichenLevel == 3) {
						if ((functionAlsString.charAt(i) >= '0' && functionAlsString.charAt(i) <= '9') || functionAlsString.charAt(i) == 'x' || functionAlsString.charAt(i) == 'X') {
							gefunden = true;
							neuerFunktionSyntaxbaumGebrochenRational.setRoot(neuerFunktionSyntaxbaumGebrochenRational);
							neuerFunktionSyntaxbaumGebrochenRational.setInhalt(functionAlsString.charAt(i));
							neuerFunktionSyntaxbaumGebrochenRational.setInhalt(functionAlsString);
							FunktionSyntaxbaum neuerFunktionSyntaxbaum = new FunktionSyntaxbaum();
							parseFuntionBuffer(neuerFunktionSyntaxbaum, functionAlsString);
							ArrayList<Double> vectorKnoten = new ArrayList<Double>();
							ausSyntaxbaumVektorErstellen(neuerFunktionSyntaxbaum, vectorKnoten);
							neuerFunktionSyntaxbaumGebrochenRational.setInhalt(vectorKnoten);
							/*String substringLinks = new WCHAR[i];
							String substringRechts = new WCHAR[rechteGrenze - i];
							int g = 0;
							for (int j = klammerLevel == 0 ? 0 : 1; j < i; j++, g++) {
								substringLinks[g] = functionAlsString[j];
							}substringLinks[g] = 0; g = 0;
							int bis = klammerLevel == 0 ? rechteGrenze : rechteGrenze - 1;
							for (int j = i + 1; j < bis; j++, g++) {
								substringRechts[g] = functionAlsString[j];
							}substringRechts[g] = 0;
							linkerPartGebrochenRational(i, 0, &neuerFunktionSyntaxbaumGebrochenRational, substringLinks);
							rechterPartGebrochenRational(i, 0, &neuerFunktionSyntaxbaumGebrochenRational, substringRechts);
							i = rechteGrenze;*/
						}
					}
					if (vorzeichenLevel > 3) {
						return;
					}
				}
			}
			vorzeichenLevel++;
		}
	}
	
	void kuerzeSyntaxbaumGebrochenRational(FunktionAlsVektorSyntaxbaum aktuellerKnoten) {
		if (aktuellerKnoten.getLinkesChild() == null && aktuellerKnoten.getRechtesChild() == null) {
			return;
		}
		if (aktuellerKnoten.getLinkesChild() != null) {
			if (aktuellerKnoten.getLinkesChild().getLinkesChild() != null) {
				kuerzeSyntaxbaumGebrochenRational(aktuellerKnoten.getLinkesChild());
			}
		}
		if (aktuellerKnoten.getRechtesChild() != null) {
			if (aktuellerKnoten.getRechtesChild().getRechtesChild() != null) {
				kuerzeSyntaxbaumGebrochenRational(aktuellerKnoten.getRechtesChild());
			}
		}

		if (aktuellerKnoten.getInhaltTChar() == '^') {

		}


		if (aktuellerKnoten.getInhaltTChar() == '*') {
			if (aktuellerKnoten.getRechtesChild() != null&&aktuellerKnoten.getLinkesChild() != null) {
				if ((aktuellerKnoten.getRechtesChild().getRechtesChild() == null && aktuellerKnoten.getRechtesChild().getLinkesChild() == null) && (aktuellerKnoten.getLinkesChild().getLinkesChild() == null && aktuellerKnoten.getLinkesChild().getRechtesChild() == null)) {
					ArrayList<Double> rechtesPolynom = aktuellerKnoten.getRechtesChild().getInhaltVector();
					ArrayList<Double> linkesPolynom = aktuellerKnoten.getLinkesChild().getInhaltVector();
					aktuellerKnoten.setInhalt(MathLibrary.polynomMultiplikation(linkesPolynom, rechtesPolynom));
					aktuellerKnoten.setInhalt(funktionVectorToString(aktuellerKnoten.getInhaltVector(),false));
					aktuellerKnoten.setRechtesChild(null);
					aktuellerKnoten.setLinkesChild(null);
				}
			}
		}

		if (aktuellerKnoten.getInhaltTChar() == '+') {
			if (aktuellerKnoten.getRechtesChild() != null&&aktuellerKnoten.getLinkesChild() != null) {
				if ((aktuellerKnoten.getRechtesChild().getRechtesChild() == null && aktuellerKnoten.getRechtesChild().getLinkesChild() == null)&& (aktuellerKnoten.getLinkesChild().getLinkesChild() == null && aktuellerKnoten.getLinkesChild().getRechtesChild() == null)) {
					ArrayList<Double> rechtesPolynom = aktuellerKnoten.getRechtesChild().getInhaltVector();
					ArrayList<Double> linkesPolynom = aktuellerKnoten.getLinkesChild().getInhaltVector();
					aktuellerKnoten.setInhalt(MathLibrary.polynomAddition(linkesPolynom, rechtesPolynom));
					aktuellerKnoten.setInhalt(funktionVectorToString(aktuellerKnoten.getInhaltVector(), false));
					aktuellerKnoten.setRechtesChild(null);
					aktuellerKnoten.setLinkesChild(null);
				}
			}
		}

		if (aktuellerKnoten.getInhaltTChar() == '-') {
			if (aktuellerKnoten.getRechtesChild() != null&&aktuellerKnoten.getLinkesChild() != null) {
				if ((aktuellerKnoten.getRechtesChild().getRechtesChild() == null && aktuellerKnoten.getRechtesChild().getLinkesChild() == null) && (aktuellerKnoten.getLinkesChild().getLinkesChild() == null && aktuellerKnoten.getLinkesChild().getRechtesChild() == null)) {
					ArrayList<Double> rechtesPolynom = aktuellerKnoten.getRechtesChild().getInhaltVector();
					ArrayList<Double> linkesPolynom = aktuellerKnoten.getLinkesChild().getInhaltVector();
					aktuellerKnoten.setInhalt(MathLibrary.polynomSubtraktion(linkesPolynom, rechtesPolynom));
					aktuellerKnoten.setInhalt(funktionVectorToString(aktuellerKnoten.getInhaltVector(), false));
					aktuellerKnoten.setRechtesChild(null);
					aktuellerKnoten.setLinkesChild(null);
				}
			}
		}

		if (aktuellerKnoten.getInhaltTChar() == '/') {
			aktuellerKnoten.setInhalt("(" + aktuellerKnoten.getLinkesChild().getInhaltString()+")/("+ aktuellerKnoten.getRechtesChild().getInhaltString()+")");
		}
	}
	
	String funktionVectorToString(ArrayList<Double> funktion, boolean mitMalZeichenZwischenXundZahl) {
		String beschriftung = "";
		//String beschriftungWCHAR = "";
		boolean periodErkannt = false;
		String tmpBeschriftung;
		int indexLetzteNachkommastelle = 0;
		boolean hatNachkommastellen = false;
		int indexPeriod = 0;

		for (int i = funktion.size() - 1; i > -1; i--) {
			if (funktion.get(i) != 0) {
				if (i != funktion.size() - 1 && funktion.get(i) > 0) {
					beschriftung += '+';
				}
				tmpBeschriftung = String.valueOf(funktion.get(i));
				periodErkannt = false;
				hatNachkommastellen = false;
				indexLetzteNachkommastelle = 0;
				for (int j = 0; j < tmpBeschriftung.length(); j++) {
					if (periodErkannt) {
						if (tmpBeschriftung.charAt(j) != '0') {
							indexLetzteNachkommastelle = j + 1;
							hatNachkommastellen = true;
						}
					}
					if (tmpBeschriftung.charAt(j) == '.') {
						periodErkannt = true;
						indexPeriod = j;
					}
				}
				if (hatNachkommastellen) {
					for (int j = 0; j < indexLetzteNachkommastelle; j++) {
						beschriftung += tmpBeschriftung.charAt(j);
					}
					if (mitMalZeichenZwischenXundZahl) {
						beschriftung += '*';
					}	
				}
				else {
					for (int j = 0; j < indexPeriod; j++) {
						if (i == 0) {
							beschriftung += tmpBeschriftung.charAt(j);
						}
						else {
							if (tmpBeschriftung.charAt(j) != '1') {
								beschriftung += tmpBeschriftung.charAt(j);
								if (j == indexPeriod - 1) {
									if (mitMalZeichenZwischenXundZahl) {
										beschriftung += '*';
									}
								}						
							}
						}
					}
				}


				if (i >= 2) {
					beschriftung += "x^";
					beschriftung += String.valueOf(i);
				}
				if (i == 1) {
					beschriftung += "x";
				}
			}
		}
		/*int k = 0;
		while (beschriftung.charAt(k) != 0) {
			beschriftungWCHAR.[k] = beschriftung[k];
			k++;
		}
		beschriftungWCHAR[k] = 0;*/
		return beschriftung;
	}
}
