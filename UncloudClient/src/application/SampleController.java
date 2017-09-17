
package application;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

public class SampleController implements Initializable {
	
	@FXML private TextField serverIPField;
	@FXML private TextField serverPortField;
	@FXML private TextField NameField;
	@FXML private TextField ShareOrdnerField;
	@FXML private Button ShareOrdnerSuchenButton;
	@FXML private Button verbinden;
	@FXML private Button btnChat;
	@FXML private Label lblStatus;
	private static Label lblStatusStatic;
	
	@FXML private TableView<Peer> peersListe;
	@FXML private TableColumn<Peer, Integer> id;
	@FXML private TableColumn<Peer, String> socket;
	@FXML private TableColumn<Peer, String> name;
	@FXML private TableColumn<Peer, String> os;
	
	@FXML private TableView<Datei> dateienListe;
	@FXML private TableColumn<Datei, String> dateiname;
	@FXML private TableColumn<Datei, String> fortschritt;
	@FXML private TableColumn<Datei, String> groesse;
	
	public ObservableList<Datei> dateiList = FXCollections.observableArrayList();
	public static ArrayList<ObservableList<Datei>> dateiArrayList = new ArrayList<>();
	
	public ObservableList<Peer> peerList = FXCollections.observableArrayList();
	public static ArrayList<ObservableList<Peer>> tmpList = new ArrayList<>();
	public static Lock chatFenserLock = new ReentrantLock();
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		id.setCellValueFactory(new PropertyValueFactory<Peer, Integer>("id"));
		socket.setCellValueFactory(new PropertyValueFactory<Peer, String>("socket"));
		name.setCellValueFactory(new PropertyValueFactory<Peer, String>("name"));
		os.setCellValueFactory(new PropertyValueFactory<Peer, String>("os"));
		peersListe.setItems(peerList);
		if(!tmpList.contains(peerList)){
			tmpList.add(peerList);
		}
		
		
		dateiname.setCellValueFactory(new PropertyValueFactory<Datei, String>("dateiname"));
		fortschritt.setCellValueFactory(new PropertyValueFactory<Datei, String>("fortschritt"));
		groesse.setCellValueFactory(new PropertyValueFactory<Datei, String>("groesse"));

		dateienListe.setItems(dateiList);
		if(!dateiArrayList.contains(dateiList)){
			dateiArrayList.add(dateiList);
		}
		ShareOrdnerField.setText(Client.getShareOrdner());	
		NameField.setText(Client.getClientName());
		serverIPField.setText(Client.getServerIP());
		serverPortField.setText(String.valueOf(Client.getServerPort()));
		lblStatusStatic=lblStatus;
	}
	
	public void chatOeffnen(ActionEvent event){
		if(peersListe.getSelectionModel().getSelectedItem()!=null){
			Platform.runLater(new Runnable(){
				Chat chatRunnable;
	
				@Override
				public void run() {
					  try {
						  	boolean chatSchonVorhanden=false;
						  	Client.chatListeLock.lock();
						  	for(Chat c : Client.getChatListe()){
						  		if(c.getPeer().getIp().equals(peersListe.getSelectionModel().getSelectedItem().getIp())){
						  			if(c.getChatObserver()==null){
						  				Client.verbindeChatZuAnderemPeer(peersListe.getSelectionModel().getSelectedItem());
						  			}
						  			chatRunnable=c;
						  			chatSchonVorhanden=true;
						  			break;
						  		}
						  	}
						  	if(!chatSchonVorhanden){
						  		Client.verbindeChatZuAnderemPeer(peersListe.getSelectionModel().getSelectedItem());
						  		chatRunnable=Client.getChatListe().get(Client.getChatListe().size()-1);
						  	}
						  	Stage chatStage = new Stage();
						  	chatStage.toFront();
							chatRunnable.setChatStage(chatStage);
						  	AnchorPane root = (AnchorPane)FXMLLoader.load(getClass().getResource("Chat.fxml"));
							TextArea chatVerlauf = (TextArea) root.lookup("#chatVerlauf");
							Scene chatScene = new Scene(root);
							if(chatSchonVorhanden){						
								String tmp = chatVerlauf.getText();
								String tmp2 = chatRunnable.getChatVerlauf().getText();
					  			chatVerlauf.setText(chatRunnable.getChatVerlauf().getText());
					  			
							}
							chatVerlauf.setScrollTop(Double.MAX_VALUE);
							chatRunnable.setChatVerlauf(chatVerlauf);							
							chatRunnable.getChatVerlauf().setScrollTop(Double.MAX_VALUE);
						  	Client.chatListeLock.unlock();
		
							chatScene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
							chatStage.setTitle("Chat mit " + peersListe.getSelectionModel().getSelectedItem().getName());
							chatStage.setScene(chatScene);
							chatStage.show();
							chatStage.setOnCloseRequest(e -> {
								e.consume();
								try{
									chatRunnable.getChatObserver().onCompleted();
									chatRunnable.setChatObserver(null);
									chatRunnable.setChatController(null);
								}catch(Exception ex){
									ex.printStackTrace();
								}
								chatStage.close();
							});
							} catch(Exception e) {
								e.printStackTrace();
							}
						}
			});
		}
	}
	
	public void chatOeffnen(Chat chat){
		Platform.runLater(new Runnable(){
			Chat chatRunnable=chat;

			@Override
			public void run() {
				try {
						Stage chatStage = new Stage();
						chatStage.toFront();
						chatRunnable.setChatStage(chatStage);
						AnchorPane root = (AnchorPane)FXMLLoader.load(getClass().getResource("Chat.fxml"));
						Scene chatScene = new Scene(root);
						chatScene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
						chatStage.setTitle("Chat mit " + chat.getPeer().getName());
						chatStage.setScene(chatScene);
						chatStage.show();
						TextArea chatVerlauf = (TextArea) root.lookup("#chatVerlauf");	
						if(chatRunnable.getChatVerlauf()==null){
							chatRunnable.setChatVerlauf(chatVerlauf);
							chatRunnable.getChatVerlauf().setScrollTop(Double.MAX_VALUE);
						}else{
							chatVerlauf.setText(chatRunnable.getChatVerlauf().getText());
							chatVerlauf.setScrollTop(Double.MAX_VALUE);
							chatRunnable.setChatVerlauf(chatVerlauf);
							chatRunnable.getChatVerlauf().setScrollTop(Double.MAX_VALUE);
						}
						chatStage.setOnCloseRequest(e -> {
							e.consume();
							try{
								chatRunnable.getChatObserver().onCompleted();
								chatRunnable.setChatObserver(null);
								chatRunnable.setChatController(null);
							}catch(Exception ex){
								ex.printStackTrace();
							}
							chatStage.close();
						});
				} catch(Exception e) {
						e.printStackTrace();
				}
			}
		});
	}
	
	public void setChatClient(){
		if(peersListe.getSelectionModel().getSelectedItem()!=null){
			Peer andererPeer = peersListe.getSelectionModel().getSelectedItem();
			Client.verbindeChatZuAnderemPeer(andererPeer);
		}
	}
	
	public void setClient(){
		dateiArrayList.get(0).clear();
		if(peersListe.getSelectionModel().getSelectedItem()!=null){
			Peer andererPeer = peersListe.getSelectionModel().getSelectedItem();
			Client.verbindeZuAnderemPeer(andererPeer);
		}
	}
	
	public void doppelKlickAufDatei(MouseEvent event){
		if(event.getClickCount()==2){
			if(dateienListe.getSelectionModel().getSelectedItem()!=null){
				Client.downloadeDateiVonAnderemPeer(dateienListe.getSelectionModel().getSelectedItem(), peersListe.getSelectionModel().getSelectedItem());
			}
		}
	}
	public void downloadDatei(){
		if(dateienListe.getSelectionModel().getSelectedItem()!=null){
			Client.downloadeDateiVonAnderemPeer(dateienListe.getSelectionModel().getSelectedItem(), peersListe.getSelectionModel().getSelectedItem());
		}
	}
	
	public static void setDateiView(Datei datei){
		dateiArrayList.get(0).add(datei);
	}
	
	public static void changeDateiView(Datei datei){
		for(int i=0;i<dateiArrayList.get(0).size();i++){
			if(dateiArrayList.get(0).get(i).getDateiname().equals(datei.getDateiname())){
				dateiArrayList.get(0).set(i,datei);
			}
		}
		
	}
	
	public static void removeFromDateiView(Datei datei){
		dateiArrayList.get(0).remove(datei);
	}
	
	public void setTableView(Peer peer){
		tmpList.get(0).add(peer);
	}
	
	public void removeFromTableView(Peer peer){
		tmpList.get(0).remove(peer);
	}
	
	public void removeAllFromTableView(){
		tmpList.get(0).clear();
	}
	
	public void setIp(){
		Client.setServerIP(serverIPField.getText());
		configSpeichern(serverIPField.getText(),1);
	}
	
	public void setPort(){
		Client.setServerPort( Integer.parseInt(serverPortField.getText()));
		configSpeichern(serverPortField.getText(),2);
	}
	
	public void setStatus(){
		Platform.runLater(new Runnable() {
		    @Override
		    public void run() {
				try{
					lblStatusStatic.setTextFill(Color.web("#ae0404"));
					lblStatusStatic.setText("Verbindung wurde beendet");
				}
				catch(Exception e){
					e.printStackTrace();
				}
		    }
		});

	}
	
	public void connect(){
		setIp();
		setPort();
		if(Client.getName()==null||Client.getName().equals("")){
			lblStatus.setText("Fehler... Zum Verbinden bitte Name eingeben.");
		}else{
			long verbindungZeit = System.currentTimeMillis();
			if(Client.checkConnectivity()){
				while(!Client.mitServerVerbunden){
					try {
						Thread.sleep(50);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if(System.currentTimeMillis()-verbindungZeit>5000){
						lblStatus.setTextFill(Color.web("#ae0404"));
						lblStatus.setText("Verbindungsfehler Timeout...");
						break;
					}
				}
				if(Client.mitServerVerbunden){
					lblStatus.setTextFill(Color.DARKGREEN);
					lblStatus.setText("Verbunden mit " + Client.getServerIP()+":"+Client.getServerPort() );
				}
			}else{
				lblStatus.setTextFill(Color.web("#ae0404"));
				lblStatus.setText("Verbindungsfehler...");
			}
		}
	}
	
	public void setName(){
		Client.setName(NameField.getText());
		configSpeichern(NameField.getText(),3);
	}
	
	public void setOrdner(){
		Client.setShareOrdner(ShareOrdnerField.getText());
		configSpeichern(ShareOrdnerField.getText(),4);
	}
	
	private boolean configSpeichern(String neueEinstellung,int index){
		try {
			BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(System.getProperty("user.home")+"\\AppData\\Local\\BenShare\\config.ini"));
			if(index==1){
				bufferedWriter.write("Server IP:" + neueEinstellung);
			}else if(index!=1){
				bufferedWriter.write("Server IP:" + Client.getServerIP());
			}
			bufferedWriter.newLine();
			if(index==2){
				bufferedWriter.write("Server-Port:" + neueEinstellung);
			}else if(index!=2){
				bufferedWriter.write("Server-Port:" + Client.getServerPort());
			}
			bufferedWriter.newLine();
			if(index==3){
				bufferedWriter.write("Name:" + neueEinstellung);
			}else if(index!=3){
				bufferedWriter.write("Name:" + Client.getClientName());
			}
			bufferedWriter.newLine();
			if(index==4){
				bufferedWriter.write("Up/Download-Ordner:" + neueEinstellung);
			}else if(index!=4){
				bufferedWriter.write("Up/Download-Ordner:" + Client.getShareOrdner());
			}
			bufferedWriter.newLine();
			bufferedWriter.close();
			
			System.out.println("Neue Configdatei erstellt: " + System.getProperty("user.home")+"\\AppData\\Local\\BenShare\\config.ini");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}
	
	public void selectOrdner(ActionEvent event){
		DirectoryChooser fc = new DirectoryChooser();
		File ordner = fc.showDialog(null);
		if(ordner.isDirectory()){
			ShareOrdnerField.setText(ordner.getAbsolutePath());
			Client.setShareOrdner(ordner.getAbsolutePath());
			File[] fl = ordner.listFiles();
			ArrayList<File> fla=new ArrayList<File>();
			for(int i=0;i<fl.length;i++){
				if(fl[i].isFile()){
					fla.add(fl[i]);
				}
			}
			Client.setListeFreigegebenerDateien(fla);
		}
		configSpeichern(ordner.getAbsolutePath(),4);
	}
}
