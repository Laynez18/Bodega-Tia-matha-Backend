package com.bodeguita.bodeguita_backend.config;

import com.bodeguita.bodeguita_backend.dto.DniResponse;
import com.bodeguita.bodeguita_backend.dto.RucResponse;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class SunatApiClient {

    private final RestTemplate restTemplate;
    private final SunatApiProperties properties;

    public SunatApiClient(SunatApiProperties properties) {
        this.restTemplate = new RestTemplate();
        this.properties = properties;
    }

    private HttpHeaders headers() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(MediaType.APPLICATION_JSON);
        if (properties.getToken() != null && !properties.getToken().isBlank()) {
            headers.set("Authorization", "Bearer " + properties.getToken());
        }
        return headers;
    }

    public RucResponse consultarRuc(String ruc) {
        if (properties.getUrl() == null || properties.getUrl().isBlank()) {
            throw new IllegalStateException("sunat.api.url no configurada");
        }
        String url = UriComponentsBuilder.fromUriString(properties.getUrl())
                .path("/ruc/{ruc}")
                .buildAndExpand(ruc)
                .toUriString();
        try {
            ResponseEntity<RucResponse> res = restTemplate.postForEntity(
                    url, new HttpEntity<>(headers()), RucResponse.class);
            return res.getBody();
        } catch (RestClientException e) {
            throw new RuntimeException("Error consultando RUC " + ruc + ": " + e.getMessage(), e);
        }
    }

    public DniResponse consultarDni(String dni) {
        if (properties.getUrl() == null || properties.getUrl().isBlank()) {
            throw new IllegalStateException("sunat.api.url no configurada");
        }
        String url = UriComponentsBuilder.fromUriString(properties.getUrl())
                .path("/dni/{dni}")
                .buildAndExpand(dni)
                .toUriString();
        try {
            ResponseEntity<DniResponse> res = restTemplate.postForEntity(
                    url, new HttpEntity<>(headers()), DniResponse.class);
            return res.getBody();
        } catch (RestClientException e) {
            throw new RuntimeException("Error consultando DNI " + dni + ": " + e.getMessage(), e);
        }
    }
}
