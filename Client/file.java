package Client;

import java.io.FileWriter;
import java.io.IOException;

public class file {
    public static void write(String name, String text) {
        try (FileWriter writer = new FileWriter(name + ".txt", true)) { // true = append mode
            writer.write(text + "\n");
        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }
    }
}
