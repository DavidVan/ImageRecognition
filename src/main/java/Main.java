import com.clarifai.api.ClarifaiClient;
import com.clarifai.api.RecognitionRequest;
import com.clarifai.api.RecognitionResult;
import com.clarifai.api.Tag;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class Main {
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
        List<RecognitionResult> test = Container(client);
        List<RecognitionResult> results = client.recognize(new RecognitionRequest(new File("test.jpg")));

        for (Tag tag : results.get(0).getTags()) {
            System.out.println(tag.getName() + ": " + tag.getProbability());
        }

    }
    public  static List<RecognitionResult> Container(ClarifaiClient client){
        File file = new File("C:\\Images");
        File[] files = file.listFiles();
        // These statements check to see if the files are readable since Windows denies access to it's files.
        if(file.canRead())
            System.out.println("I made it here");
        if(!file.canRead())
            file.setReadable(true);
        // End of checking.
        List<RecognitionResult> results = client.recognize(new RecognitionRequest(files));
        if(results != null) {
            //Checking Size of List to make sure every file was saved.
            int z = results.size();
            System.out.println("I made it?" + "  " + z);
            return results;
        }
        else {
            System.out.println("Folder is empty");
            return null;
        }
    }

}