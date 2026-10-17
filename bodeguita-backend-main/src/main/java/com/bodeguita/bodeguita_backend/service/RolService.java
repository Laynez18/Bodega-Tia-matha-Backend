package com.bodeguita.bodeguita_backend.service;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Locale;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import com.bodeguita.bodeguita_backend.dto.*;
import com.bodeguita.bodeguita_backend.entity.Rol;
import com.bodeguita.bodeguita_backend.repository.RolRepository;
import com.bodeguita.bodeguita_backend.repository.UsuarioRolRepository;

@Service @Transactional(readOnly = true)
public class RolService {
 private final RolRepository roles; private final UsuarioRolRepository usuarioRoles;
 public RolService(RolRepository roles, UsuarioRolRepository usuarioRoles) { this.roles=roles; this.usuarioRoles=usuarioRoles; }
 public List<RolDto> listar(){ return roles.findAllOrdenados().stream().map(this::dto).toList(); }
 public RolDto obtener(Long id){ return dto(buscar(id)); }
 @Transactional public RolDto crear(RolRequest r){ String n=normalizar(r.nRol()); if(roles.existeNombre(n)) throw conflicto("El nombre del rol ya existe."); Rol rol=new Rol(); asignar(rol,r,n); rol.setEstado(true); rol.setFCreacion(LocalDateTime.now(ZoneId.of("America/Lima"))); return dto(roles.save(rol)); }
 @Transactional public RolDto actualizar(Long id,RolRequest r){ Rol rol=buscar(id); String n=normalizar(r.nRol()); if(roles.existeNombreEnOtroRol(n,id)) throw conflicto("El nombre del rol ya existe."); asignar(rol,r,n); return dto(rol); }
 @Transactional public RolDto cambiarEstado(Long id,EstadoRequest r){ Rol rol=buscar(id); if("ADMINISTRADOR".equals(rol.getNRol()) && "0".equals(r.estado())) throw conflicto("El rol ADMINISTRADOR no se puede desactivar."); if("0".equals(r.estado())) { long asignados=usuarioRoles.countByRol_IdRolAndVigenteAndEstadoTrue(id,"1"); if(asignados>0) throw conflicto("No se puede desactivar el rol porque tiene "+asignados+" usuarios vigentes asignados."); } rol.setEstado("1".equals(r.estado())); return dto(rol); }
 private Rol buscar(Long id){return roles.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Rol no encontrado: "+id+"."));}
 private void asignar(Rol rol,RolRequest r,String nombre){rol.setNRol(nombre);rol.setDescripcion(r.descripcion()==null||r.descripcion().isBlank()?null:r.descripcion().trim());rol.setNivel(r.nivel());}
 private String normalizar(String n){return n.trim().toUpperCase(Locale.ROOT);}
 private RolDto dto(Rol r){return new RolDto(r.getIdRol(),r.getNRol(),r.getDescripcion(),r.getNivel(),r.getFCreacion(),r.getEstado());}
 private ResponseStatusException conflicto(String m){return new ResponseStatusException(HttpStatus.CONFLICT,m);}
}
