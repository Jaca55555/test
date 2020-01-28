package uz.maroqand.ecology.docmanagment.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import uz.maroqand.ecology.core.util.Common;
import uz.maroqand.ecology.core.util.DateParser;
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
    @Override
    public Page<Folder> findFiltered(Integer id, String name, String dateBegin, String dateEnd, Pageable pageable){
        Date begin = DateParser.TryParse(dateBegin, Common.uzbekistanDateFormat);
        Date end = DateParser.TryParse(dateEnd, Common.uzbekistanDateFormat);
        return folderRepository.findAll(getFilteringSpecification(id, name, begin, end), pageable);
    }

    private static Specification<Folder> getFilteringSpecification(Integer id, String name, Date dateBegin, Date dateEnd){
        return new Specification<Folder>(){
          @Override
          public Predicate toPredicate(Root<Folder> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder){
              List<Predicate> predicates = new LinkedList<>();

              if(id != null)
                  predicates.add(criteriaBuilder.equal(root.get("id"), id));

              if(name != null)
                  predicates.add(criteriaBuilder.like(root.get("name"), "%" + name + "%"));

              if (dateBegin != null && dateEnd == null)
                  predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt").as(Date.class), dateBegin));
              else if (dateEnd != null && dateBegin == null)
                  predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("createdAt").as(Date.class), dateEnd));
              else if (dateBegin != null && dateEnd != null)
                  predicates.add(criteriaBuilder.between(root.get("createdAt").as(Date.class), dateBegin, dateEnd));

              predicates.add( criteriaBuilder.equal(root.get("deleted"), false) );
              Predicate overAll = criteriaBuilder.and(predicates.toArray(new Predicate[0]));

              return overAll;
          }
        };
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
    @Override
    public Folder get(Integer id){

        return folderRepository.getOne(id);
    }
    @Override
    public Folder update(Folder folder){
        return folderRepository.save(folder);
    }
}