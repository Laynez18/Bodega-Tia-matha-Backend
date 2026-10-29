package com.bodeguita.bodeguita_backend.service;

import com.bodeguita.bodeguita_backend.config.SunatApiClient;
import com.bodeguita.bodeguita_backend.dto.DniResponse;
import com.bodeguita.bodeguita_backend.dto.RucResponse;
import org.springframework.stereotype.Service;

@Service
public class ConsultaSunatService {

    private final SunatApiClient sunatApiClient;

    public ConsultaSunatService(SunatApiClient sunatApiClient) {
        this.sunatApiClient = sunatApiClient;
    }

    public RucResponse consultarRuc(String ruc) {
        RucResponse r = sunatApiClient.consultarRuc(ruc);
        if (r == null || !r.success()) {
            throw new IllegalArgumentException("No se pudo consultar el RUC " + ruc);
        }
        return r;
    }

    public DniResponse consultarDni(String dni) {
        DniResponse d = sunatApiClient.consultarDni(dni);
        if (d == null || !d.success()) {
            throw new IllegalArgumentException("No se pudo consultar el DNI " + dni);
        }
        return d;
    }
}
