import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

/**
 * Created by David on 4/2/2016.
 */
public class GUI extends Application {

    public String selectedPath;

    @Override
    public void start(Stage primaryStage) {

        ImageView viewer = new ImageView();
        viewer.setFitHeight(500);
        viewer.setFitWidth(500);

        Button selectFolder = new Button();
        selectFolder.setText("Select Folder");
        selectFolder.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                DirectoryChooser chooser = new DirectoryChooser();
                File folder = chooser.showDialog(primaryStage);

                if (folder != null) {
                    Main.setPath(folder.getAbsolutePath());
                    selectFolder.setText("Selected - " + folder.getAbsolutePath());
                    selectFolder.setDisable(true);
                    //Platform.exit(); // Exits the application.
                }
            }
        });

        Button selectDestination = new Button();
        selectDestination.setText("Select Destination");
        selectDestination.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                DirectoryChooser chooser = new DirectoryChooser();
                File folder = chooser.showDialog(primaryStage);

                if (folder != null) {
                    Main.setDestination(folder.getAbsolutePath());
                    selectDestination.setText("Selected - " + folder.getAbsolutePath());
                    selectDestination.setDisable(true);
                    Platform.exit(); // Exits the application.
                }
            }
        });

        Button selectImage = new Button();
        selectImage.setText("Select Image");
        selectImage.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                FileChooser chooser = new FileChooser();
                File file = chooser.showOpenDialog(primaryStage);

                if (file != null) {
                    Main.setDestination(file.getAbsolutePath());
                    selectImage.setText("Selected - " + file.getAbsolutePath());
                    selectImage.setDisable(true);
                    Image image = new Image(file.toURI().toString());
                    viewer.setImage(image);
                }
            }
        });

        FlowPane root = new FlowPane();
        root.getChildren().addAll(selectFolder, selectDestination, selectImage, viewer);
        Scene scene = new Scene(root, 800, 600);

        primaryStage.setTitle("Please select a folder.");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
