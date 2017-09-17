/*
 * Uncloud Server
 * Version 0.01 
 * Created on: 17.09.2017 
 * Author: Benjamin Leonhardt
 */

package application;
	
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.fxml.FXMLLoader;


public class Main extends Application {
	@Override
	public void start(Stage primaryStage) {
		try {
			BorderPane root = (BorderPane)FXMLLoader.load(getClass().getResource("Sample.fxml"));
			Scene scene = new Scene(root);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.getIcons().add(new Image("/icons/UncloudIcon.PNG"));
			primaryStage.show();
			primaryStage.setOnCloseRequest(e -> {
				e.consume();
				if(RegisterServer.registerServer!=null){
					RegisterServer.registerServer.shutdownNow();
				}
				primaryStage.close();
				System.exit(0);
			});
			
		} catch(Exception e) {
			e.printStackTrace();
		}

	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
