package com.bodeguita.bodeguita_backend.config;

import com.bodeguita.bodeguita_backend.dto.DniResponse;
import com.bodeguita.bodeguita_backend.dto.RucResponse;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Component
public class SunatApiClient {

    private final RestTemplate restTemplate;
    private final SunatApiProperties properties;

    public SunatApiClient(SunatApiProperties properties) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(properties.getTimeout());
        factory.setReadTimeout(properties.getTimeout());
        this.restTemplate = new RestTemplate(factory);
        this.properties = properties;
    }

    private HttpHeaders headers() {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        if (properties.getToken() != null && !properties.getToken().isBlank()) {
            headers.set("Authorization", "Bearer " + properties.getToken());
        }
        return headers;
    }

    public RucResponse consultarRuc(String ruc) {
        if (properties.getUrl() == null || properties.getUrl().isBlank()) {
            throw new IllegalStateException("sunat.api.url no configurada");
        }
        String url = properties.getUrl().replaceAll("/+$", "") + properties.getPathRuc() + ruc;
        try {
            ResponseEntity<RucResponse> res = restTemplate.getForEntity(url, RucResponse.class, new HttpEntity<>(headers()));
            return res.getBody();
        } catch (RestClientException e) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Error consultando RUC " + ruc + ": " + e.getMessage(), e);
        }
    }

    public DniResponse consultarDni(String dni) {
        if (properties.getUrl() == null || properties.getUrl().isBlank()) {
            throw new IllegalStateException("sunat.api.url no configurada");
        }
        String url = properties.getUrl().replaceAll("/+$", "") + properties.getPathDni() + dni;
        try {
            ResponseEntity<DniResponse> res = restTemplate.getForEntity(url, DniResponse.class, new HttpEntity<>(headers()));
            return res.getBody();
        } catch (RestClientException e) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Error consultando DNI " + dni + ": " + e.getMessage(), e);
        }
    }
}
