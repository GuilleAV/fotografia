package ar.com.gav.backend.fotografo.service;


import ar.com.gav.backend.fotografo.dto.EtiquetaDTO;
import ar.com.gav.backend.fotografo.entity.Etiqueta;
import ar.com.gav.backend.fotografo.mapper.EtiquetaMapper;

import javax.ejb.Stateless;
import java.util.List;
import java.util.stream.Collectors;

@Stateless
public class EtiquetaService extends BaseService<Etiqueta> {

    public EtiquetaService() {
        super(Etiqueta.class);
    }

    public void crear(EtiquetaDTO dto) {
        Etiqueta entidad = EtiquetaMapper.toEntity(dto);
        super.crear(entidad);
        dto.setIdEtiqueta(entidad.getIdEtiqueta());
    }

    public EtiquetaDTO actualizar(EtiquetaDTO dto) {
        Etiqueta entidad = EtiquetaMapper.toEntity(dto);
        Etiqueta actualizado = super.actualizar(entidad);
        return EtiquetaMapper.toDTO(actualizado);
    }

    public void eliminar(Long id) {
        super.eliminar(id);
    }

    public EtiquetaDTO buscarPorId(Long id) {
        Etiqueta entidad = super.buscarPorId(id);
        return EtiquetaMapper.toDTO(entidad);
    }

    public List<EtiquetaDTO> listarDto() {
        return super.listar().stream()
                .map(EtiquetaMapper::toDTO)
                .collect(Collectors.toList());
    }
}
