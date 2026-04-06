package ar.com.gav.backend.fotografo.service;



import ar.com.gav.backend.fotografo.dto.UsuarioDTO;
import ar.com.gav.backend.fotografo.entity.Usuario;
import ar.com.gav.backend.fotografo.mapper.UsuarioMapper;

import javax.ejb.Stateless;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Stateless
public class UsuarioService extends BaseService<Usuario> {

    public UsuarioService() {
        super(Usuario.class);
    }

    public void crear(UsuarioDTO dto) {
        Usuario entidad = UsuarioMapper.toEntity(dto);
        super.crear(entidad);
        dto.setIdUsuario(entidad.getId());
    }

    public UsuarioDTO actualizar(UsuarioDTO dto) {
        Usuario entidad = UsuarioMapper.toEntity(dto);
        Usuario actualizado = super.actualizar(entidad);
        return UsuarioMapper.toDTO(actualizado);
    }

    public void eliminar(Long id) {
        super.eliminar(id);
    }

    public UsuarioDTO buscarPorId(Long id) {
        Usuario entidad = super.buscarPorId(id);
        return UsuarioMapper.toDTO(entidad);
    }

    public List<UsuarioDTO> listarDto() {
        return super.listar().stream()
                .map(UsuarioMapper::toDTO)
                .collect(Collectors.toList());
    }

    public Usuario buscarUserName(String nombreUsuario) {
        try {
            return em.createQuery(
                            "SELECT u FROM Usuario u WHERE u.nombreUsuario = :nombreUsuario", Usuario.class)
                    .setParameter("nombreUsuario", nombreUsuario)
                    .getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Busca usuario por username y retorna la entidad completa.
     * Usado por FotoService para verificar permisos.
     */
    public Usuario buscarPorUsernameEntity(String username) {
        try {
            return em.createQuery(
                    "SELECT u FROM Usuario u WHERE u.nombreUsuario = :username", Usuario.class)
                    .setParameter("username", username)
                    .getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }
}
