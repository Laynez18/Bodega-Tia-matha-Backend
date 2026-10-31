package com.bodeguita.bodeguita_backend.config;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class FactilizaUnirestTest {

    public static void main(String[] args) throws Exception {
        String token = System.getenv("FACTILIZA_API_TOKEN");
        String url = System.getenv("FACTILIZA_API_URL");
        if (url == null || url.isBlank()) {
            url = "https://api.factiliza.com/v1";
        }
        String dni = "71813429";

        System.out.println("=== PRUEBA HTTP CLIENT FACTILIZA ===");
        System.out.println("URL base: " + url);
        System.out.println("Token presente: " + (token != null && !token.isBlank()));
        if (token != null) {
            System.out.println("Token longitud: " + token.length());
            System.out.println("Token primeros 4: " + token.substring(0, Math.min(4, token.length())));
            System.out.println("Token últimos 4: " + token.substring(Math.max(0, token.length() - 4)));
        }
        System.out.println("DNI: " + dni);

        HttpClient client = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url + "/dni/info/" + dni))
                .header("Accept", "application/json")
                .header("Authorization", "Bearer " + token)
                .GET()
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("=== RESPUESTA ===");
            System.out.println("Status: " + response.statusCode());
            System.out.println("Headers: " + response.headers());
            System.out.println("Body: " + response.body());
        } catch (Exception e) {
            System.out.println("=== ERROR ===");
            e.printStackTrace();
        }
    }
}
