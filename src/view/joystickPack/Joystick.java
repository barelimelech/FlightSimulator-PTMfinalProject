package view.joystickPack;


import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;

import java.io.IOException;

public class Joystick extends Pane {


    public JoystickController joystickController;

    public Joystick(){
        super();


        FXMLLoader fxl = new FXMLLoader();

        Pane hb = null;
        try {

            hb = fxl.load(getClass().getResource("Joystick.fxml").openStream());

        } catch (IOException e) {
            e.printStackTrace();
        }
        if (hb != null) {
            joystickController = fxl.getController();
            this.getChildren().add(hb);

        } else {
            joystickController = null;
        }


    }
}
