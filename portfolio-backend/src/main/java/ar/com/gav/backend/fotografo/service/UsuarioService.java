package ar.com.gav.backend.fotografo.service;


import ar.com.gav.backend.fotografo.dto.UsuarioCreateDTO;
import ar.com.gav.backend.fotografo.dto.UsuarioDTO;
import ar.com.gav.backend.fotografo.entity.Usuario;
import ar.com.gav.backend.fotografo.mapper.UsuarioMapper;

import javax.ejb.Stateless;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Stateless
public class UsuarioService extends BaseService<Usuario> {

    private static final Logger LOG = Logger.getLogger(UsuarioService.class.getName());

    public UsuarioService() {
        super(Usuario.class);
    }

    public void crear(UsuarioDTO dto) {
        LOG.info("=== CREAR USUARIO === Username: " + dto.getUsername());
        try {
            Usuario entidad = UsuarioMapper.toEntity(dto);
            super.crear(entidad);
            dto.setIdUsuario(entidad.getId());
            LOG.info("User CREATED: " + dto.getUsername() + " (id: " + entidad.getId() + ")");
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Error creating user: " + dto.getUsername(), e);
            throw e;
        }
    }

    public void crear(UsuarioCreateDTO dto, String hashedPassword) {
        LOG.info("=== CREAR USUARIO (CreateDTO) === Username: " + dto.getUsername() + ", Rol: " + dto.getRol());
        try {
            Usuario entidad = new Usuario();
            entidad.setNombreUsuario(dto.getUsername());
            entidad.setPassword(hashedPassword);
            entidad.setEmail(dto.getEmail());
            entidad.setNombre(dto.getNombre());
            entidad.setApellido(dto.getApellido());
            entidad.setRol(dto.getRol());
            entidad.setActivo(dto.getActivo() != null ? dto.getActivo() : true);
            super.crear(entidad);
            LOG.info("User CREATED: " + dto.getUsername() + " (id: " + entidad.getId() + ")");
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Error creating user: " + dto.getUsername(), e);
            throw e;
        }
    }

    public UsuarioDTO actualizar(UsuarioDTO dto) {
        LOG.info("=== ACTUALIZAR USUARIO === ID: " + dto.getIdUsuario());
        try {
            Usuario existing = em.find(Usuario.class, dto.getIdUsuario());
            if (existing == null) {
                throw new RuntimeException("Usuario no encontrado");
            }

            // Actualizar solo campos editables
            existing.setNombreUsuario(dto.getUsername());
            existing.setEmail(dto.getEmail());
            existing.setNombre(dto.getNombre());
            existing.setApellido(dto.getApellido());
            existing.setRol(dto.getRol());
            existing.setActivo(dto.getActivo());
            if (dto.getFotoPerfil() != null) {
                existing.setFotoPerfil(dto.getFotoPerfil());
            }
            // NO tocamos password — se maneja por separado

            Usuario actualizado = em.merge(existing);
            LOG.info("User UPDATED: " + dto.getIdUsuario());
            return UsuarioMapper.toDTO(actualizado);
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Error updating user: " + dto.getIdUsuario(), e);
            throw e;
        }
    }

    public void eliminar(Integer id) {
        LOG.info("=== ELIMINAR USUARIO === ID: " + id);
        try {
            super.eliminar(id);
            LOG.info("User DELETED: " + id);
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Error deleting user: " + id, e);
            throw e;
        }
    }

    public UsuarioDTO buscarPorId(Integer id) {
        Usuario entidad = super.buscarPorId(id);
        return UsuarioMapper.toDTO(entidad);
    }

    public List<UsuarioDTO> listarDto() {
        LOG.fine("=== LISTAR USUARIOS ===");
        List<UsuarioDTO> usuarios = super.listar().stream()
                .map(UsuarioMapper::toDTO)
                .collect(Collectors.toList());
        LOG.fine("Found " + usuarios.size() + " users");
        return usuarios;
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
            LOG.warning("User NOT FOUND: " + username);
            return null;
        }
    }
}
