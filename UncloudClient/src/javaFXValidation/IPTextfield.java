package javaFXValidation;

import java.util.ArrayList;

import javafx.scene.control.TextField;

public class IPTextfield extends TextField {
	
	public IPTextfield(){
		this.setText("192.168.2.101");
	}
	
	@Override
	public void replaceText(int i, int i1, String string){
		String TextOfField =this.getText();
		String tmpStr = "";
		for(int j=0;j<TextOfField.length();j++){
			if(j!=i){
				tmpStr+=TextOfField.charAt(j);
			}else{
				tmpStr+=string;
				j+=(i1-i);
			}
		}
		if(i==TextOfField.length()&&i1==TextOfField.length()){
			tmpStr+=string;
		}
		
		if(string.equals("")){ 
			super.replaceText(i, i1, string);
			return;
		}

		if(string.equals(".")){
			int count=0;
			for(int j=0;j<tmpStr.length();j++){
	    		if(tmpStr.charAt(j)=='.'){
	    			count++;
	    		}
	    	}
			if(count>3){
				return;
			}else{
				super.replaceText(i, i1, string);
				return;
			}
		}
		if(i>14){
			return;
		}

		int start=0;
		ArrayList<String> parts= new ArrayList<>();

    	try{
        	for(int j=0;j<tmpStr.length();j++){
        		if(tmpStr.charAt(j)=='.'){
        			parts.add(tmpStr.substring(start, j));
        			start=j+1;
        		}
        	}
        	parts.add(tmpStr.substring(start, tmpStr.length()));
        	int tmp=0;
        	for(String s:parts){
        		tmp=Integer.parseInt(s);
        		if(tmp<1||tmp>254){
        			return;
        		}
        	}
    	}catch(Exception e){
    		return;
    	}
    	super.replaceText(i, i1, string);

	}
	
	@Override
	public void replaceSelection(String string){
		super.replaceSelection(string);
	}
}
