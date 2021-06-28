package view.popup;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;

import java.io.IOException;

public class PopUp extends Pane {

    public PopUpController popUpController;

    public PopUp(){
        super();


        FXMLLoader fxl = new FXMLLoader();

        Pane hb = null;
        try {

            hb = fxl.load(getClass().getResource("popupClass.fxml").openStream());

        } catch (IOException e) {
            e.printStackTrace();
        }
        if (hb != null) {
            popUpController = fxl.getController();
            this.getChildren().add(hb);

        } else {
            popUpController = null;
        }
    }
}
