package com.bodeguita.bodeguita_backend.service;
import java.util.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import com.bodeguita.bodeguita_backend.dto.*;
import com.bodeguita.bodeguita_backend.entity.*;
import com.bodeguita.bodeguita_backend.repository.*;

@Service @Transactional(readOnly=true)
public class PermisoService {
 private static final Set<String> PERMISOS_ADMIN=Set.of("SEG_USUARIO","SEG_ROL","SEG_AUDITORIA");
 private final RolRepository roles; private final PermisoRepository permisos; private final RolPermisoRepository rolPermisos;
 public PermisoService(RolRepository roles,PermisoRepository permisos,RolPermisoRepository rolPermisos){this.roles=roles;this.permisos=permisos;this.rolPermisos=rolPermisos;}
 public MatrizPermisosDto matriz(){
  List<Rol> rs=roles.findAllActivosOrdenados(); List<Permiso> ps=permisos.findAllActivosConModulo();
  List<Long> idsR=rs.stream().map(Rol::getIdRol).toList(), idsP=ps.stream().map(Permiso::getIdPermiso).toList();
  Map<String,Boolean> valores=new HashMap<>();
  if(!idsR.isEmpty()&&!idsP.isEmpty()) for(RolPermiso rp:rolPermisos.findAllParaMatriz(idsR,idsP)) valores.put(rp.getRol().getIdRol()+":"+rp.getPermiso().getIdPermiso(),Boolean.TRUE.equals(rp.getEstado())&&"1".equals(rp.getConcedido()));
  Map<Long,List<PermisoFilaDto>> porModulo=new LinkedHashMap<>(); Map<Long,Modulo> modulos=new LinkedHashMap<>();
  for(Permiso p:ps){Map<String,Boolean> porRol=new LinkedHashMap<>();for(Rol r:rs)porRol.put(String.valueOf(r.getIdRol()),valores.getOrDefault(r.getIdRol()+":"+p.getIdPermiso(),false));modulos.putIfAbsent(p.getModulo().getIdModulo(),p.getModulo());porModulo.computeIfAbsent(p.getModulo().getIdModulo(),x->new ArrayList<>()).add(new PermisoFilaDto(p.getIdPermiso(),p.getClave(),p.getNPermiso(),porRol));}
  return new MatrizPermisosDto(rs.stream().map(r->new RolResumenDto(r.getIdRol(),r.getNRol())).toList(),modulos.entrySet().stream().map(e->new ModuloPermisosDto(e.getKey(),e.getValue().getNModulo(),porModulo.get(e.getKey()))).toList());
 }
 @Transactional public void guardar(MatrizPermisosRequest request){
  for(CambioPermisoRequest c:request.cambios()){Rol r=roles.findActivoPorId(c.idRol()).orElseThrow(()->bad("Rol inactivo o inexistente."));Permiso p=permisos.findById(c.idPermiso()).filter(x->Boolean.TRUE.equals(x.getEstado())).orElseThrow(()->bad("Permiso inexistente."));if("ADMINISTRADOR".equals(r.getNRol())&&PERMISOS_ADMIN.contains(p.getClave())&&!c.concedido())throw bad("No se puede revocar "+p.getClave()+" al ADMINISTRADOR.");RolPermiso rp=rolPermisos.findByRol_IdRolAndPermiso_IdPermiso(c.idRol(),c.idPermiso()).orElseGet(()->{RolPermiso n=new RolPermiso();n.setRol(r);n.setPermiso(p);return n;});rp.setConcedido(c.concedido()?"1":"0");rp.setEstado(true);rolPermisos.save(rp);}
 }
 private ResponseStatusException bad(String m){return new ResponseStatusException(HttpStatus.BAD_REQUEST,m);}
}
