// Add this dependency to your build.gradle file
// implementation 'com.squareup.okhttp3:okhttp:4.9.1'
package com.example.va;
import org.json.JSONObject;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import java.io.IOException;

public class NetworkUtils {

    public static String fetchOEmbedHTML(String videoUrl) throws IOException {
        OkHttpClient client = new OkHttpClient();

        String oEmbedUrl = "https://www.instagram.com/oembed/?url=" + videoUrl;
        Request request = new Request.Builder()
                .url(oEmbedUrl)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                String responseBody = response.body().string();
                // Parse the response to get the HTML
                return parseHTML(responseBody);
            } else {
                throw new IOException("Unexpected code " + response);
            }
        }
    }

    private static String parseHTML(String responseBody) {
        // Parse the JSON response to get the HTML field
        // Use a JSON library like Gson or JSONObject to parse the response
        // Here is a simple example using JSONObject
        try {
            JSONObject jsonObject = new JSONObject(responseBody);
            return jsonObject.getString("html");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
