package application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ServerGUI extends Application{

	
	@Override
	public void start(Stage primaryStage) throws Exception {
		try{
			
			Parent root = FXMLLoader.load(getClass().getResource("C:\\Users\\Admin\\Downloads\\workspaceP2P\\ServerVersion1\\src\\main\\java\\P2P_Server\\ServerVersion1\\MainScreenServer.fxml"));
			Scene scene = new Scene(root);
			primaryStage.setScene(scene);
			primaryStage.show();
			
		}catch(Exception e){
			System.out.println(e);
		}
	}
	
//	public static void main(String[] args) {
//		launch(args);
//	}

}
