import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class ApiClient {

    public static void sendRequest(String method, String urlString, String data, String contentType) {
        HttpURLConnection connection = null;

        try {
            // Create URL object
            URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(method);

            // Set content type if applicable
            if (contentType != null) {
                connection.setRequestProperty("Content-Type", contentType);
            }

            // For POST and PUT requests, write data to the request body
            if (method.equals("POST") || method.equals("PUT")) {
                connection.setDoOutput(true);
                if (data != null && !data.isEmpty()) {
                    try (OutputStream os = connection.getOutputStream()) {
                        os.write(data.getBytes());
                        os.flush();
                    }
                }
            }

            // Get response code and body
            int responseCode = connection.getResponseCode();
            System.out.println("Status Code: " + responseCode);

            try (BufferedReader in = new BufferedReader(new InputStreamReader(
                    responseCode >= 200 && responseCode < 300 ? connection.getInputStream() : connection.getErrorStream()))) {
                String line;
                StringBuilder response = new StringBuilder();
                while ((line = in.readLine()) != null) {
                    response.append(line);
                }
                System.out.println("Response Body:");
                System.out.println(response.toString());
            }

        } catch (Exception e) {
            System.err.println("An error occurred: " + e.getMessage());
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("API Client");
        System.out.print("Enter HTTP method (GET, POST, PUT, DELETE): ");
        String method = scanner.nextLine().toUpperCase();

        System.out.print("Enter the API URL: ");
        String url = scanner.nextLine();

        String data = null;
        String contentType = null;

        if (method.equals("POST") || method.equals("PUT")) {
            contentType = "application/json";
            System.out.print("Enter JSON data to send (e.g., {\"key\":\"value\"}): ");
            data = scanner.nextLine();
        }

        sendRequest(method, url, data, contentType);

        scanner.close();
    }
}
