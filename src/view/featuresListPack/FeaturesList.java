package view.featuresListPack;


import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;
import java.io.IOException;


public class FeaturesList extends Pane {

    public FeaturesListController featuresListController;

    public FeaturesList() {
        super();
        FXMLLoader fxl = new FXMLLoader();
        Pane hb = null;
        try {
            hb = fxl.load(getClass().getResource("FeaturesList.fxml").openStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (hb != null) {
            featuresListController = fxl.getController();
            this.getChildren().add(hb);
        } else {
            featuresListController = null;
        }
    }
}
