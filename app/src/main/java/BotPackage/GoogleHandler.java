package BotPackage;

import com.google.gson.JsonElement;
import com.google.gson.*;
import com.google.gson.JsonParser;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.Objects;

public class GoogleHandler {
    String GeoCoder(String[] args) throws IOException {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < args.length; i++){
            sb.append(args[i]);
            if((i+1)< args.length) {
                sb.append("+");
            }
        }

        String URL = "https://maps.googleapis.com/maps/api/geocode/json?address=" + sb + "&key=AIzaSyDsTd5gbZvjddXD7oidLqfPu00ydIb6JNc";

        OkHttpClient client = new OkHttpClient().newBuilder()
            .build();
        Request request = new Request.Builder()
                .url(URL)
                .method("GET", null)
                .build();
        Response response = client.newCall(request).execute();

        JsonElement responseElement = JsonParser.parseString(Objects.requireNonNull(response.body()).string());
        JsonObject test = responseElement.getAsJsonObject();

        JsonArray testArray = test.get("results").getAsJsonArray();
        JsonObject jsonObject = testArray.get(0).getAsJsonObject();

        JsonObject geometryObject = jsonObject.get("geometry").getAsJsonObject();
        JsonObject locationObject = geometryObject.get("location").getAsJsonObject();

        double latCoordinate = locationObject.get("lat").getAsDouble();
        double lngCoordinate = locationObject.get("lng").getAsDouble();
        return locationObject.toString();
    }

    public static Double[] getGPSCoordinates(String[] args) throws IOException {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < args.length; i++){
            sb.append(args[i]);
            if((i+1)< args.length) {
                sb.append("+");
            }
        }

        String URL = "https://maps.googleapis.com/maps/api/geocode/json?address=" + sb + "&key=AIzaSyDsTd5gbZvjddXD7oidLqfPu00ydIb6JNc";
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        Request request = new Request.Builder()
                .url(URL)
                .method("GET", null)
                .build();
        Response response = client.newCall(request).execute();
        JsonElement responseElement = JsonParser.parseString(Objects.requireNonNull(response.body()).string());
        JsonObject test = responseElement.getAsJsonObject();
        JsonArray testArray = test.get("results").getAsJsonArray();
        JsonObject jsonObject = testArray.get(0).getAsJsonObject();

        JsonObject geometryObject = jsonObject.get("geometry").getAsJsonObject();
        JsonObject locationObject = geometryObject.get("location").getAsJsonObject();
        double latCoordinate = locationObject.get("lat").getAsDouble();
        double lngCoordinate = locationObject.get("lng").getAsDouble();

        return new Double[]{latCoordinate, lngCoordinate};
    }
}
