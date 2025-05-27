package Client;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

//methods front end uses to communicate with back end
public class API {
    // create HTTP client
    private static HttpClient client = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_1_1)
            .build();

    // user specific token to validate requests
    private static String token, username;

    public static void sendChat(String to, String message) {
        try {
            String body = String.format("{\"username\":\"%s\",\"token\":\"%s\",\"to\":\"%s\",\"message\":\"%s\"}", username, token, to, message);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:3000/send"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();
                    
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString()); // send request

        } catch (IOException | InterruptedException e) {
            // handle error
            e.printStackTrace();
        }
    }

    public static String[] getChats(String from) {
        try {
            String url = "http://localhost:3000/chats?username="
                    + URLEncoder.encode(username, StandardCharsets.UTF_8)
                    + "&token=" + URLEncoder.encode(token, StandardCharsets.UTF_8)
                    + "&fromUser=" + URLEncoder.encode(from, StandardCharsets.UTF_8);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            return response.body().split("~");

        } catch (IOException | InterruptedException e) {
            // handle error
            e.printStackTrace();
            return null;
        }
    }

    // get the user token in order for request to be validated
    public static int getToken(String inUsername, String inPassword) {
        try {
            String body = String.format("{\"username\":\"%s\",\"password\":\"%s\"}", inUsername, inPassword); // send
                                                                                                              // username
                                                                                                              // and
                                                                                                              // password
                                                                                                              // as JSON

            // create HTTP request
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:3000/getToken"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString()); // send request

            if (response.statusCode() == 200) {
                token = response.body();
                username = inUsername;
            }

            return response.statusCode();
        } catch (IOException | InterruptedException e) {
            // handle error
            e.printStackTrace();
            return 0;
        }
    }

    // get a list of users to display
    public static String[] getUsers() {
        try {
            String url = "http://localhost:3000/users?username="
                    + URLEncoder.encode(username, StandardCharsets.UTF_8) // encode username
                    + "&token=" + URLEncoder.encode(token, StandardCharsets.UTF_8); // encode token

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            String[] users = response.body().split("~");
            return users;

        } catch (IOException | InterruptedException e) {
            // handle error
            e.printStackTrace();
            return null;
        }
    }
}
