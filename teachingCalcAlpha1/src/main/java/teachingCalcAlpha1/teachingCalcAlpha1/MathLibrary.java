package teachingCalcAlpha1.teachingCalcAlpha1;

import java.util.ArrayList;

public class MathLibrary {
	
	public MathLibrary(){
		
	}

	static ArrayList<ArrayList<Double>> polynomdivision(ArrayList<Double> zaehler, ArrayList<Double> nenner) {
		ArrayList<Double> ergebnis = new ArrayList<Double>();
		for(int i=0;i<zaehler.size() - (nenner.size() - 1);i++){
			ergebnis.add(0.0);
		}
		
		ArrayList<Double> tmp = new ArrayList<Double>();
		for(int i=0;i<zaehler.size();i++){
			tmp.add(0.0);
		}
		
		ArrayList<Double> rest = new ArrayList<Double>();
		for(int i=0;i<zaehler.size();i++){
			rest.add(0.0);
		}
		
		int round = 0;
		int i, j, k;
		for (i = zaehler.size() - 1, j = nenner.size() - 1, k = ergebnis.size() - 1; i > -1
				+ (nenner.size() - 1); i--) {
			ergebnis.set(k - round, zaehler.get(i) / nenner.get(j));
			for (int g = tmp.size() - 1; j > -1; g--, j--) {
				tmp.set(g - round, nenner.get(j) * ergebnis.get(k - round));
			}
			j = nenner.size() - 1;

			for (int g = zaehler.size() - 1 - round; g > zaehler.size() - 1 - round - nenner.size(); g--) {
				zaehler.set(g, zaehler.get(g) - tmp.get(g));
				if ((-1 + (nenner.size() - 1)) < i) {
					tmp.set(g, 0.0);
				}
			}
			round++;
		}
		for (i = 0; i < zaehler.size(); i++) {
			rest.set(i, zaehler.get(i) - tmp.get(i));
		}
		ArrayList<ArrayList<Double>> ErgebnisMitRest = new ArrayList<ArrayList<Double>>();
		ErgebnisMitRest.add(ergebnis);
		ErgebnisMitRest.add(rest);
		return ErgebnisMitRest;
	}

	static ArrayList<Double> polynomAddition(ArrayList<Double> summand1, ArrayList<Double> summand2) {
		int groesserePolynom = 0;
		if (summand1.size() > summand2.size()) {
			groesserePolynom = summand1.size();
		} else {
			groesserePolynom = summand2.size();
		}
		ArrayList<Double> ergebnis = new ArrayList<Double>();
		for(int i=0;i<groesserePolynom;i++){
			ergebnis.add(0.0);
		}

		for (int i = 0; i < groesserePolynom; i++) {
			if (i >= summand1.size()) {
				ergebnis.set(i, summand2.get(i));
			} else if (i >= summand2.size()) {
				ergebnis.set(i, summand1.get(i));
			} else {
				ergebnis.set(i, summand1.get(i) + summand2.get(i));
			}
		}
		return ergebnis;
	}

	static ArrayList<Double> polynomSubtraktion(ArrayList<Double> subtrahend1, ArrayList<Double> subtrahend2) {
		int groesserePolynom = 0;
		if (subtrahend1.size() > subtrahend2.size()) {
			groesserePolynom = subtrahend1.size();
		} else {
			groesserePolynom = subtrahend2.size();
		}
		ArrayList<Double> ergebnis = new ArrayList<Double>();
		for(int i=0;i<groesserePolynom;i++){
			ergebnis.add(0.0);
		}

		for (int i = 0; i < groesserePolynom; i++) {
			if (i >= subtrahend1.size()) {				
				ergebnis.set(i, subtrahend2.get(i));
			} else if (i >= subtrahend2.size()) {
				ergebnis.set(i, subtrahend1.get(i));
			} else {
				ergebnis.set(i, subtrahend1.get(i)-subtrahend2.get(i));
			}

		}
		return ergebnis;
	}

	static ArrayList<Double> polynomMultiplikation(ArrayList<Double> poly1, ArrayList<Double> poly2) {
		ArrayList<Double> ergebnis = new ArrayList<Double>();
		for(int i=0;i<(poly1.size() + poly2.size() - 1);i++){
			ergebnis.add(0.0);
		}
		/*
		 * for(int i=0;i<poly1.size()+ poly2.size()-1;i++){ ergebnis.add(0.0); }
		 */
		// ergebnis.resize(poly1.size()+ poly2.size()-1);
		for (int i = 0; i < poly1.size(); i++) {
			for (int j = 0; j < poly2.size(); j++) {
				ergebnis.set(i + j, poly1.get(i) * poly2.get(j));
			}
		}
		return ergebnis;
	}

}
