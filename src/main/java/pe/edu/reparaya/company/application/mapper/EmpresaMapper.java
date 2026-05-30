package pe.edu.reparaya.company.application.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pe.edu.reparaya.company.application.dto.CompanyDtos.*;
import pe.edu.reparaya.company.domain.model.EmpresaServicio;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EmpresaMapper {

    @Mapping(target = "porcentajeCarga",             expression = "java(empresa.porcentajeCarga())")
    @Mapping(target = "contratoVigente",             expression = "java(empresa.contratoVigente())")
    @Mapping(target = "contratoVenceProximamente",   expression = "java(empresa.contratoVenceProximamente(30))")
    EmpresaResponse toResponse(EmpresaServicio empresa);

    List<EmpresaResponse> toResponseList(List<EmpresaServicio> empresas);

    @Mapping(target = "porcentajeCarga", expression = "java(empresa.porcentajeCarga())")
    DisponibilidadResponse toDisponibilidadResponse(EmpresaServicio empresa);

    List<DisponibilidadResponse> toDisponibilidadList(List<EmpresaServicio> empresas);
}
