package javaFXValidation;

import javafx.scene.control.TextField;

public class PortTextField extends TextField {

	public PortTextField(){
		this.setText("10010");
	}
	
	@Override
	public void replaceText(int i, int i1, String string){
		String TextOfField =this.getText();
		if(string.equals("")){
			super.replaceText(i, i1, string);
			return;
		}
		if(string.matches("[0-9]") || string.isEmpty()){
			if(Integer.valueOf(TextOfField+string)>0&&Integer.valueOf(TextOfField+string)<49152){
				super.replaceText(i, i1, string);
				return;
			}
		}
	}
	
	@Override
	public void replaceSelection(String string){
		super.replaceSelection(string);
	}
	
}
