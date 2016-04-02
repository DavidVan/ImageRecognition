import com.clarifai.api.ClarifaiClient;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {

        // Read in the API keys
        FileReader reader = new FileReader("secrets.txt");
        BufferedReader br = new BufferedReader(reader);
        String[] secrets = new String[2];

        for (int i = 0; i < secrets.length; i++) {
            secrets[i] = br.readLine();
        }

        // Create the client
        ClarifaiClient client = new ClarifaiClient(secrets[0], secrets[1]);
    }
}