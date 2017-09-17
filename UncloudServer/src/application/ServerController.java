package application;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;

public class ServerController implements Initializable {
	
	public static ArrayList<ObservableList<Peer>> tmpList = new ArrayList<>();


	@FXML private Label lblStatus;
	@FXML private TableView<Peer> clientTable;
	@FXML private TableColumn<Peer, Integer> id;
	@FXML private TableColumn<Peer, String> socket;
	@FXML private TableColumn<Peer, String> name;
	@FXML private TableColumn<Peer, String> os;
	


	public ObservableList<Peer> list = FXCollections.observableArrayList();
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		id.setCellValueFactory(new PropertyValueFactory<Peer, Integer>("id"));
		socket.setCellValueFactory(new PropertyValueFactory<Peer, String>("socket"));
		name.setCellValueFactory(new PropertyValueFactory<Peer, String>("name"));
		os.setCellValueFactory(new PropertyValueFactory<Peer, String>("os"));
		clientTable.setItems(list);
		
	}
	
	public void setTableView(Peer peer){
		tmpList.get(0).add(peer);
	}
	
	public void removeFromTableView(int index){
		tmpList.get(0).remove(index);
	}
	
	public void starteServer(ActionEvent event){
		if(!tmpList.contains(list)){
			tmpList.add(list);
		}

		RegisterServer rc = new RegisterServer();
		if(rc.serverStarten()){
			try {
				String ipString="";
				Enumeration e = NetworkInterface.getNetworkInterfaces();
				while(e.hasMoreElements())
				{
				    NetworkInterface n = (NetworkInterface) e.nextElement();
				    Enumeration ee = n.getInetAddresses();
				    while (ee.hasMoreElements())
				    {
				        InetAddress i = (InetAddress) ee.nextElement();
				        if(i.getHostAddress().startsWith("192")){
				        	ipString=i.getHostAddress();
				        }
				       
				    }
				}
				
			
				lblStatus.setTextFill(Color.DARKGREEN);
				lblStatus.setText("Online " + ipString+":"+RegisterServer.port );
			} catch (SocketException e) {
				lblStatus.setTextFill(Color.web("#ae0404"));
				lblStatus.setText("Offline " + rc.getServerInitStatus());
				e.printStackTrace();
			}
		}else{
			lblStatus.setTextFill(Color.web("#ae0404"));
			lblStatus.setText("Offline " + rc.getServerInitStatus());
		}
	}

}
