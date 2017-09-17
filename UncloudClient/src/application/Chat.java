
package application;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import io.grpc.stub.StreamObserver;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

public class Chat {
	private Peer peer;
	private StreamObserver chatObserver;
	private TextArea chatVerlauf;
	private ChatController chatController;
	private Stage chatStage;
	
	public Chat() {
	}
	
	public Chat(Peer peer, StreamObserver chatObserver) {
		super();
		this.peer = peer;
		this.chatObserver = chatObserver;
		checkChatHistoryFile();
	}
	public Peer getPeer() {
		return peer;
	}
	public void setPeer(Peer peer) {
		this.peer = peer;
	}
	public StreamObserver getChatObserver() {
		return chatObserver;
	}
	public void setChatObserver(StreamObserver chatObserver) {
		this.chatObserver = chatObserver;
	}

	public TextArea getChatVerlauf() {
		return chatVerlauf;
	}

	public void setChatVerlauf(TextArea chatVerlauf) {
		this.chatVerlauf = chatVerlauf;
		chatVerlauf.setScrollTop(Double.MAX_VALUE);
		checkChatHistoryFile();
	}

	public ChatController getChatController() {
		return chatController;
	}

	public void setChatController(ChatController chatController) {
		this.chatController = chatController;
	}
	
	public void addTextChatHistoryFile(String text){
		File folder = new File(System.getProperty("user.home")+"\\AppData\\Local\\BenShare");
		File chatHistoryFile = new File(System.getProperty("user.home")+"\\AppData\\Local\\BenShare\\" + peer.getHostName() + ".chat");
		
		if(!chatHistoryFile.exists()){
			System.out.println("Keine Chat-History Datei gefunden. Es wird eine neue Datei erstellt");
			try {
				folder.mkdir();
				BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(System.getProperty("user.home")+"\\AppData\\Local\\BenShare\\"+ peer.getHostName() + ".chat"));
				bufferedWriter.write("Chat-History" + peer.getHostName() );
				bufferedWriter.newLine();
				bufferedWriter.write(text);
				bufferedWriter.close();
				System.out.println("Neue Chat-History Datei erstellt: " + System.getProperty("user.home")+"\\AppData\\Local\\BenShare\\"+ peer.getHostName() + ".chat");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else{
			System.out.println(peer.getHostName() + ".chat wird geladen");
			try {
				FileOutputStream fileOutputStream = new FileOutputStream(chatHistoryFile,true);
				fileOutputStream.write(text.getBytes());
				fileOutputStream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		chatVerlauf.setScrollTop(Double.MAX_VALUE);
	}

	public void checkChatHistoryFile(){
		File folder = new File(System.getProperty("user.home")+"\\AppData\\Local\\BenShare");
		File chatHistoryFile = new File(System.getProperty("user.home")+"\\AppData\\Local\\BenShare\\" + peer.getHostName() + ".chat");
		
		if(!chatHistoryFile.exists()){
			System.out.println("Keine Chat-History Datei gefunden. Es wird eine neue Datei erstellt");
			try {
				folder.mkdir();
				BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(System.getProperty("user.home")+"\\AppData\\Local\\BenShare\\"+ peer.getHostName() + ".chat"));
				bufferedWriter.write("Chat-History" + peer.getHostName() );
				bufferedWriter.newLine();
				bufferedWriter.close();
				System.out.println("Neue Chat-History Datei erstellt: " + System.getProperty("user.home")+"\\AppData\\Local\\BenShare\\"+ peer.getHostName() + ".chat");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else{
			System.out.println(peer.getHostName() + ".chat wird geladen");
			try {
				BufferedReader bufferedReader = new BufferedReader(new FileReader(System.getProperty("user.home")+"\\AppData\\Local\\BenShare\\" + peer.getHostName() + ".chat"));
				String magicNumber = bufferedReader.readLine();
				String chatInhaltHistory="";
				if(magicNumber.equals("Chat-History" + peer.getHostName())){
					String tmp;
					while((tmp=bufferedReader.readLine())!=null){
						chatInhaltHistory+=tmp+"\n";
					}
				}
				if(chatVerlauf!=null){
					String tmp = chatVerlauf.getText();
					chatVerlauf.setText(chatInhaltHistory);	
					chatVerlauf.setScrollTop(Double.MAX_VALUE);
				}			
				bufferedReader.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public Stage getChatStage() {
		return chatStage;
	}

	public void setChatStage(Stage chatStage) {
		this.chatStage = chatStage;
	}
}
