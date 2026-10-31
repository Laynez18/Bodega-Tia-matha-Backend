package com.bodeguita.bodeguita_backend.config;

import com.bodeguita.bodeguita_backend.dto.DniResponse;
import com.bodeguita.bodeguita_backend.dto.RucResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.List;

@Component
public class SunatApiClient {

    private static final Logger log = LoggerFactory.getLogger(SunatApiClient.class);

    private final RestTemplate restTemplate;
    private final SunatApiProperties properties;

    public SunatApiClient(SunatApiProperties properties) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(properties.getTimeout());
        factory.setReadTimeout(properties.getTimeout());
        this.restTemplate = new RestTemplate(factory);
        this.properties = properties;
        String token = properties.getToken();
        log.info("SUNAT init -> urlPresente={}, tokenPresente={}, timeout={}",
                properties.getUrl() != null && !properties.getUrl().isBlank(),
                token != null && !token.isBlank(),
                properties.getTimeout());
    }

    private HttpHeaders headers() {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.set("User-Agent", "bodeguita-backend/1.0");
        String token = properties.getToken();
        if (token != null && !token.isBlank()) {
            String masked = token.length() > 8 ? token.substring(0, 4) + "..." + token.substring(token.length() - 4) : token;
            headers.set("Authorization", "Bearer " + token);
            log.info("SUNAT headers -> Authorization: Bearer [{}]", masked);
        } else {
            log.warn("SUNAT headers -> Authorization: AUSENTE (token nulo o vacio)");
        }
        return headers;
    }

    public RucResponse consultarRuc(String ruc) {
        if (properties.getUrl() == null || properties.getUrl().isBlank()) {
            throw new IllegalStateException("factiliza.api.url no configurada");
        }
        log.debug("FACTILIZA RUC -> url={}, tokenPresente={}", properties.getUrl(), properties.getToken() != null && !properties.getToken().isBlank());
        String url = properties.getUrl().replaceAll("/+$", "") + properties.getPathRuc() + ruc;
        try {
            ResponseEntity<RucResponse> res = restTemplate.getForEntity(url, RucResponse.class, new HttpEntity<>(headers()));
            return res.getBody();
        } catch (RestClientException e) {
            String cuerpo = extraerCuerpoError(e, url);
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Error consultando RUC " + ruc + ": " + e.getMessage() + (cuerpo != null ? " | Cuerpo: " + cuerpo : ""), e);
        }
    }

    public DniResponse consultarDni(String dni) {
        if (properties.getUrl() == null || properties.getUrl().isBlank()) {
            throw new IllegalStateException("factiliza.api.url no configurada");
        }
        log.debug("FACTILIZA DNI -> url={}, tokenPresente={}", properties.getUrl(), properties.getToken() != null && !properties.getToken().isBlank());
        String url = properties.getUrl().replaceAll("/+$", "") + properties.getPathDni() + dni;
        try {
            ResponseEntity<DniResponse> res = restTemplate.getForEntity(url, DniResponse.class, new HttpEntity<>(headers()));
            return res.getBody();
        } catch (RestClientException e) {
            String cuerpo = extraerCuerpoError(e, url);
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Error consultando DNI " + dni + ": " + e.getMessage() + (cuerpo != null ? " | Cuerpo: " + cuerpo : ""), e);
        }
    }

    private String extraerCuerpoError(RestClientException e, String url) {
        try {
            HttpHeaders h = headers();
            ResponseEntity<String> res = restTemplate.execute(url, org.springframework.http.HttpMethod.GET,
                    new HttpEntity<>(h), new ResponseExtractor<ResponseEntity<String>>() {
                        @Override
                        public ResponseEntity<String> extractData(org.springframework.http.client.ClientHttpResponse response) throws IOException {
                            String body = new String(response.getBody().readAllBytes(), java.nio.charset.StandardCharsets.UTF_8);
                            return new ResponseEntity<>(body, response.getHeaders(), response.getStatusCode());
                        }
                    });
            return res.getBody();
        } catch (RestClientException ex) {
            return null;
        }
    }
}
