package com.bodeguita.bodeguita_backend.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "factiliza.api")
public class SunatApiProperties {

    private String url;
    private String token;
    private Integer timeout = 10000;

    // Paths relativos sobre la URL base (ajustar según factiliza.com)
    private String pathDni = "/dni/info/";
    private String pathRuc = "/ruc/info/";

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Integer getTimeout() {
        return timeout;
    }

    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
    }

    public String getPathDni() {
        return pathDni;
    }

    public void setPathDni(String pathDni) {
        this.pathDni = pathDni;
    }

    public String getPathRuc() {
        return pathRuc;
    }

    public void setPathRuc(String pathRuc) {
        this.pathRuc = pathRuc;
    }
}
