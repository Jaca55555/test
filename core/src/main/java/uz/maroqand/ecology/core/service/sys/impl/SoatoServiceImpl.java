package uz.maroqand.ecology.core.service.sys.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import uz.maroqand.ecology.core.constant.expertise.LogType;
import uz.maroqand.ecology.core.dto.expertise.FilterDto;
import uz.maroqand.ecology.core.entity.expertise.RegApplication;
import uz.maroqand.ecology.core.entity.sys.Soato;
import uz.maroqand.ecology.core.entity.user.User;
import uz.maroqand.ecology.core.repository.sys.SoatoRepository;
import uz.maroqand.ecology.core.service.sys.SoatoService;
import uz.maroqand.ecology.core.util.Common;
import uz.maroqand.ecology.core.util.DateParser;

import javax.persistence.criteria.*;
import java.util.*;

@Service
public class SoatoServiceImpl implements SoatoService {

    private SoatoRepository soatoRepository;

    @Autowired
    public SoatoServiceImpl(SoatoRepository soatoRepository) {
        this.soatoRepository = soatoRepository;
    }

    @Override
    public Soato getById(Integer id) {
        System.out.println("soatId == " + id);
        Soato soato = soatoRepository.getOne(id);
        System.out.println(soato.getName() + " " + soato.getNameRu() + " " +soato.getNameOz());
        return soato;
    }

    @Cacheable("SoatoServiceImpl.getRegions")
    public List<Soato> getRegions() {
        return soatoRepository.getAll();
    }

    @Cacheable("SoatoServiceImpl.getSubRegions")
    public List<Soato> getSubRegions() {
        return soatoRepository.findByLevelOrderByNameAsc(2);
    }
    public List<Soato> getSubregionsbyregionId(Long id){
        return soatoRepository.findByParentId(id);
    }
    public Page<Soato> getFiltered(Integer regionId, Set<Integer> subRegionIds, Integer organizationId, Pageable pageable){
        return soatoRepository.findAll(getFilteringSpecification(regionId,subRegionIds,organizationId),pageable);
    }
    private static Specification<Soato> getFilteringSpecification(
            final Integer regionId,
            final Set<Integer> subRegionIds,
            final Integer organizationId
    ) {
        return new Specification<Soato>() {
            @Override
            public Predicate toPredicate(Root<Soato> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new LinkedList<>();

                if(regionId!=null){
                    predicates.add(criteriaBuilder.equal(root.get("id"),regionId));
                }
                if(root.get("parentId")!=null) {
                    if (subRegionIds != null) {
                        predicates.add(criteriaBuilder.in(root.get("id")).value(subRegionIds));
                    }
                }
                return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
            }
        };
    }

}
