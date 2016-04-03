import com.clarifai.api.ClarifaiClient;
import com.clarifai.api.RecognitionRequest;
import com.clarifai.api.RecognitionResult;
import com.clarifai.api.Tag;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;

public class Main {

    public static String selectedPath;
    public static String selectedDestination;
    public static File[] lists; //Saves the lists into an array so we can reaccess the names


    public static void main(String[] args) throws IOException {

        // Read in the API keys.
        FileReader reader = new FileReader("secrets.txt");
        BufferedReader br = new BufferedReader(reader);
        String[] secrets = new String[2];

        for (int i = 0; i < secrets.length; i++) {
            secrets[i] = br.readLine();
        }

        // Create the client.
        ClarifaiClient client = new ClarifaiClient(secrets[0], secrets[1]);
        List<RecognitionResult> results = client.recognize(new RecognitionRequest(new File("test.jpg")));

        GUI.launch(GUI.class);
        System.out.println(selectedPath);

        List<RecognitionResult> test = Container(client);
        for (int i = 0; i < test.size(); i++) {
            List<Tag> tag = test.get(i).getTags();
            System.out.println(lists[i].getName()); //Prints out name of image.
            for ( int j = 0; j < tag.size(); j++){
                System.out.println(tag.get(j).getName() + ": " + tag.get(j).getProbability());
            }
        }
        makeDir();
    }
    public static void makeDir(List<RecognitionResult> results){
        try {
            String mainDir = selectedDestination;//Directory to hold all the files.
            ArrayList<String> subDir = new ArrayList<String>();//Arraylist to hold the tags for folder making.
            for (int i = 0; i < results.size(); i++) {//For all results get the best tag name for each
                List<Tag> tag = results.get(i).getTags();
                if (!subDir.contains(tag.get(0).getName()))//Adds all unique tags to array
                    subDir.add(tag.get(0).getName());
            }
            for(int i=0; i<subDir.size(); i++){//Makes folders in location according to tags
                if(subDir.get(i)==null)
                    return;
                File dir = new File(mainDir, subDir.get(i));
                dir.mkdirs();
            }

            File file = new File(selectedPath);
            File[] files = file.listFiles();

            for(int i=0; i<files.length; i++){//the eventual move file to folder
                System.out.println(files[i].toString());
                //Files.move();
            }

        }
        catch(Exception e){
            System.out.println("Error");
            e.printStackTrace();
        }
    }

    public static List<RecognitionResult> Container(ClarifaiClient client) {
        File file = new File(selectedPath);
        File[] files = file.listFiles();
        lists = files;
        // These statements check to see if the files are readable since the OS denies access to its files.
//        if (file.canRead()) {
//            System.out.println("I made it here");
//        }
//        else {
//            file.setReadable(true);
//        }
        // End of checking.
        List<RecognitionResult> results = client.recognize(new RecognitionRequest(files));
        if (results != null) {
            //Checking Size of List to make sure every file was saved.
//            int z = results.size();
//            System.out.println("I made it?" + "  " + z);
            return results;
        }
        else {
            System.out.println("Folder is empty");
            return null;
        }

    }

    public static void setPath(String s) {
        selectedPath = s;
    }

    public static void setDestination(String s) {
        selectedDestination = s;
    }

}