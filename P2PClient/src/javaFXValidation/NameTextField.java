package javaFXValidation;

import javafx.scene.control.TextField;

public class NameTextField extends TextField{
	public NameTextField(){
		this.setText("Ben");
	}
	
	@Override
	public void replaceText(int i, int i1, String string){
		if(i>32){
			return;
		}
		if(string.equals("")){
			super.replaceText(i, i1, string);
			return;
		}
		if(string.matches("[A-Za-z0-9]")){
			super.replaceText(i, i1, string);
			return;
		}

	}
	
	@Override
	public void replaceSelection(String string){
		super.replaceSelection(string);
	}
}
