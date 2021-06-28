package view;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import model.Model;
import view.readXML.myProperty;
import view_model.ViewModel;

import java.util.ArrayList;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader fxml = new FXMLLoader();
        Pane root = fxml.load(getClass().getResource("Window.fxml").openStream());
        WindowController wc = fxml.getController();
        wc.init();

        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root, 1000, 620));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
