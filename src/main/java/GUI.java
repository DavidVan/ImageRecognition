import com.clarifai.api.ClarifaiClient;
import com.clarifai.api.RecognitionRequest;
import com.clarifai.api.RecognitionResult;
import com.clarifai.api.Tag;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.nio.file.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by David on 4/2/2016.
 */
public class GUI extends Application {

    public List<File> lists; //Saves the lists into an array so we can reaccess the names
    List<RecognitionResult> results;

    @Override
    public void start(Stage primaryStage) throws IOException {


        // Read in the API keys.
        FileReader reader = new FileReader("secrets.txt");
        BufferedReader br = new BufferedReader(reader);
        String[] secrets = new String[2];

        for (int i = 0; i < secrets.length; i++) {
            secrets[i] = br.readLine();
        }

        // Create the client.
        ClarifaiClient client = new ClarifaiClient(secrets[0], secrets[1]);

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
                    selectFolder.setText("Selected - " + folder.getAbsolutePath());
                    selectFolder.setDisable(true);
                    //Platform.exit(); // Exits the application.
                    FlowPane root = new FlowPane();
                    results = Container(client, folder.getAbsolutePath());
                    System.out.println(results.size());
                    for (int i = 0; i < results.size(); i++) {
                        StackPane pane = new StackPane();
                        List<Tag> tag = results.get(i).getTags();
                        System.out.println(lists.get(i).getName()); //Prints out name of image.
                        ImageView viewer = new ImageView();
                        viewer.setFitHeight(300);
                        viewer.setFitWidth(300);
                        Image image = new Image(lists.get(i).toURI().toString());
                        viewer.setImage(image);
                        pane.getChildren().add(viewer);
                        for (int j = 0; j < tag.size(); j++) {
                            System.out.println(tag.get(j).getName() + ": " + tag.get(j).getProbability());
                            Text text = new Text(tag.get(j).getName() + ": " + tag.get(j).getProbability());
                            pane.getChildren().add(text);
                            break; // Add only one tag.
                        }
                        root.getChildren().add(pane);
                    }
                    // Open a new window with the images and tags.
                    Scene scene = new Scene(root, 800, 600);
                    Stage test = new Stage();
                    test.setScene(scene);
                    test.show();
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
                    selectDestination.setText("Selected - " + folder.getAbsolutePath());
                    selectDestination.setDisable(true);
                    makeDir(results, folder.getAbsolutePath());
                    //Platform.exit(); // Exits the application.
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

    public List<RecognitionResult> Container(ClarifaiClient client, String selectedPath) {
        File file = new File(selectedPath);
        List<File> files = new LinkedList(Arrays.asList(file.listFiles()));
        for (int i = 0; i < files.size(); i++) {
            if (!files.get(i).getName().contains("jpg") && !files.get(i).getName().contains("png")) {
                files.remove(i);
            }
        }
        lists = files;
        File[] temp = new File[files.size()];
        for (int i = 0; i < files.size(); i++) {
            temp[i] = files.get(i);
        }
        List<RecognitionResult> results = client.recognize(new RecognitionRequest(temp));
        if (results != null) {
            return results;
        }
        else {
            System.out.println("Folder is empty");
            return null;
        }
    }

    public void makeDir(List<RecognitionResult> results, String selectedDestination) {
        try {
            String mainDir = selectedDestination;//Directory to hold all the files.
            ArrayList<String> subDir = new ArrayList<String>();//Arraylist to hold the tags for folder making.
            for (int i = 0; i < results.size(); i++) {//For all results get the best tag name for each
                List<Tag> tag = results.get(i).getTags();
                if (!subDir.contains(tag.get(0).getName())) {//Adds all unique tags to array
                    subDir.add(tag.get(0).getName());
                    File dir = new File(mainDir, tag.get(0).getName());
                    dir.mkdirs();
                }
                String destPath = mainDir + "\\" + subDir.get(subDir.indexOf(tag.get(0).getName())) + "\\" + lists.get(i).getName();//gets name of folder
                Path destination = Paths.get(destPath).toAbsolutePath(); //gets pathname of folder?

                String sourcePath = lists.get(i).getPath();// Takes file and gets path of it
                Path source = Paths.get(sourcePath).toAbsolutePath();// gets source path?

                Files.move(source, destination);
            }
        } catch (Exception e) {
            System.out.println("Error");
            e.printStackTrace();
        }
    }

}
