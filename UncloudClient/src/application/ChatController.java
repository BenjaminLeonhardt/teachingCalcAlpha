
package application;

import java.io.IOException;
import java.util.ArrayList;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;

public class ChatController {

	@FXML private TextArea chatEingabefeld;
	@FXML private TextArea chatVerlauf;
	@FXML private Button btnChatMsgSenden;
	
	public ArrayList<String> chatInhaltList= new ArrayList<>();
	
	private Chat chat;
	
	public ChatController(){
		chat=Client.getChatListe().get(Client.getChatListe().size()-1);
		chat.setChatController(this);
	}
	
	
	public void nachrichtSenden(){
		try{
			chat.getChatObserver().onNext(grpcDatenpakete.Text.newBuilder().setNachricht(Client.getEigeneIp()+"__"+chatEingabefeld.getText()).build());
		}catch(Exception ex){
			if(Client.verbindeChatZuAnderemPeer(chat.getPeer())){
				chat.getChatObserver().onNext(grpcDatenpakete.Text.newBuilder().setNachricht(Client.getEigeneIp()+"__"+chatEingabefeld.getText()).build());
			}
		}
		if(chat.getChatVerlauf()!=chatVerlauf){
			String tmp=chatVerlauf.getText()+chat.getChatVerlauf().getText();
			chatVerlauf.setText(chatVerlauf.getText()+chat.getChatVerlauf().getText());
		
			chat.setChatVerlauf(chatVerlauf);
		}
		chatVerlauf.setText(chatVerlauf.getText()+Client.getName() + ": "+chatEingabefeld.getText()+"\n");
		chatVerlauf.setScrollTop(Double.MAX_VALUE);
		chat.addTextChatHistoryFile(Client.getName() + ": "+chatEingabefeld.getText()+"\n");
		chatEingabefeld.setText("");
	}


	public TextArea getChatEingabefeld() {
		return chatEingabefeld;
	}


	public void setChatEingabefeld(TextArea chatEingabefeld) {
		this.chatEingabefeld = chatEingabefeld;
	}


	public TextArea getChatVerlauf() {
		return chatVerlauf;
	}


	public void setChatVerlauf(TextArea chatVerlauf) {
		this.chatVerlauf = chatVerlauf;
		chatVerlauf.setScrollTop(Double.MAX_VALUE);
	}


	public Button getBtnChatMsgSenden() {
		return btnChatMsgSenden;
	}


	public void setBtnChatMsgSenden(Button btnChatMsgSenden) {
		this.btnChatMsgSenden = btnChatMsgSenden;
	}


	public ArrayList<String> getChatInhaltList() {
		return chatInhaltList;
	}


	public void setChatInhaltList(ArrayList<String> chatInhaltList) {
		this.chatInhaltList = chatInhaltList;
	}


	public Chat getChat() {
		return chat;
	}


	public void setChat(Chat chat) {
		this.chat = chat;
	}
}
