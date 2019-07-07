package uz.maroqand.ecology.core.service.client;

import uz.maroqand.ecology.core.entity.client.Opf;

import java.util.List;

public interface OpfService {

    List<Opf> getOpfLegalEntityList();

    List<Opf> getOpfIndividualList();

    List<Opf> getOpfList();

    Opf getById(Integer id);

}
