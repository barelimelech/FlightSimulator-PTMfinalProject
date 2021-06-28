package view.buttonsPack;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;
import java.io.IOException;


public class Buttons extends Pane {

    public ButtonsController buttonsController;

    public Buttons() {
        super();
        FXMLLoader fxl = new FXMLLoader();
        Pane hb = null;
        try {
            hb = fxl.load(getClass().getResource("Buttons.fxml").openStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (hb != null) {
            buttonsController = fxl.getController();
            this.getChildren().add(hb);
        } else {
            buttonsController = null;
        }
    }

}
