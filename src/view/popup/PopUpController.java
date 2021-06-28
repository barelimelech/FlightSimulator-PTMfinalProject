package view.popup;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import view.WindowController;


public class PopUpController {

    public WindowController mc;


    public PopUpController() {
        mc=new WindowController();
    }


    @FXML
    public Button zScoreAlg;
    @FXML
    public Button regAlg;
    @FXML
    public Button HybridAlg;


    public StringProperty algoName = new SimpleStringProperty();

    public void ClassLoadPop(javafx.event.ActionEvent event) {
        Button sourceButton = (Button) event.getSource();


        String name = sourceButton.getText();
        algoName.setValue(name);
        System.out.println(name);

        System.out.println(" algo name popup : "+algoName);

        if(name.equals("RegressionAlg")){
            mc.ClassLoadPop(name);

        }

        else if(name.equals("HybridAlg")){
            mc.ClassLoadPop(name);

        }else if(name.equals("zScoreAlg")){
            mc.ClassLoadPop(name);
        }


    }
}
