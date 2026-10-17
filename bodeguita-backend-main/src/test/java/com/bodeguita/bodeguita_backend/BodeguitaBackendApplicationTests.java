package com.bodeguita.bodeguita_backend;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.bodeguita.bodeguita_backend.repository.CargoRepository;
import com.bodeguita.bodeguita_backend.repository.ContratoRepository;
import com.bodeguita.bodeguita_backend.repository.EmpleadoRepository;
import com.bodeguita.bodeguita_backend.repository.TipoIdentidadRepository;
import com.bodeguita.bodeguita_backend.repository.UsuarioRepository;

@SpringBootTest
class BodeguitaBackendApplicationTests {

    @Autowired
    private TipoIdentidadRepository tipoIdentidadRepository;

    @Autowired
    private CargoRepository cargoRepository;

    @Autowired
    private ContratoRepository contratoRepository;

    @Autowired
    private EmpleadoRepository empleadoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

	@Test
	void contextLoads() {
	}

    @Test
    void consultasDeCatalogosSeEjecutan() {
        tipoIdentidadRepository.findAllActivosOrdenados();
        cargoRepository.findAllActivosOrdenados();
        contratoRepository.findAllActivosOrdenados();
    }

    @Test
    void consultasDeEmpleadosYUsuariosSeEjecutan() {
        empleadoRepository.findAllActivosConRelaciones();
        usuarioRepository.findAllActivosConRelaciones();
    }
}
