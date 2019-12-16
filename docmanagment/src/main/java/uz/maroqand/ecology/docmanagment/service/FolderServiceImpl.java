package uz.maroqand.ecology.docmanagment.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import uz.maroqand.ecology.docmanagment.entity.Folder;
import uz.maroqand.ecology.docmanagment.repository.FolderRepository;
import uz.maroqand.ecology.docmanagment.service.interfaces.FolderService;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Utkirbek Boltaev on 01.04.2019.
 * (uz)
 * (ru)
 */
@Service
public class FolderServiceImpl implements FolderService {

    private final FolderRepository folderRepository;

    @Autowired
    public FolderServiceImpl(FolderRepository folderRepository) {
        this.folderRepository = folderRepository;
    }

    @Override
    public List<Folder> getFolderList() {
        return folderRepository.findByDeletedFalse();
    }

    @Override
    public Page<Folder> getFolderList(String name,Pageable pageable) {
        return folderRepository.findAll(getFilteringSpecification(name), pageable);
    }

    private static Specification<Folder> getFilteringSpecification(final String name) {
        return new Specification<Folder>() {
            @Override
            public Predicate toPredicate(Root<Folder> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new LinkedList<>();
                if(name!=null){
                    predicates.add( criteriaBuilder.like(root.get("name"), "%" + name + "%") );
                }
                predicates.add( criteriaBuilder.equal(root.get("deleted"), false) );
                Predicate overAll = criteriaBuilder.and(predicates.toArray(new Predicate[0]));
                return overAll;
            }
        };
    }

    @Override
    public Folder create(Folder folder, Integer userId) {
        folder.setDeleted(Boolean.FALSE);
        folder.setCreatedAt(new Date());
        folder.setCreatedById(userId);
        return folderRepository.save(folder);
    }
}