package uz.maroqand.ecology.core.service.expertise.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import uz.maroqand.ecology.core.entity.expertise.Coordinate;
import uz.maroqand.ecology.core.entity.expertise.CoordinateLatLong;
import uz.maroqand.ecology.core.repository.expertise.CoordinateLatLongRepository;
import uz.maroqand.ecology.core.repository.expertise.CoordinateRepository;
import uz.maroqand.ecology.core.service.expertise.CoordinateService;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

@Service
public class CoordinateServiceImpl implements CoordinateService {

    private final CoordinateRepository coordinateRepository;
    private final CoordinateLatLongRepository coordinateLatLongRepository;

    @Autowired
    public CoordinateServiceImpl(CoordinateRepository coordinateRepository, CoordinateLatLongRepository coordinateLatLongRepository){
        this.coordinateRepository = coordinateRepository;
        this.coordinateLatLongRepository = coordinateLatLongRepository;
    }

    public Page<Coordinate> findFiltered(Integer reviewId,Integer id, Integer tin, String name, String number, Integer regionId, Integer subRegionId, Date dateBegin, Date dateEnd, Pageable pageable) {
        return coordinateRepository.findAll(getFilteringSpecification(reviewId,id, tin, name, number, regionId, subRegionId, dateBegin, dateEnd),pageable);
    }

    private static Specification<Coordinate> getFilteringSpecification(final Integer reviewId, final Integer id, final Integer tin, final String name, final String number, final Integer regionId, final Integer subRegionId, final Date dateBegin, final Date dateEnd) {
        return new Specification<Coordinate>() {
            @Override
            public Predicate toPredicate(Root<Coordinate> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new LinkedList<>();

                if (reviewId!=null){
                    predicates.add(criteriaBuilder.equal(root.join("regApplication").get("reviewId"), reviewId));
                }

                if(id != null){
                    predicates.add(criteriaBuilder.equal(root.get("id"), id));
                }

                if(tin != null){
                    predicates.add(criteriaBuilder.equal(root.join("client").get("tin"), tin));
                }

                if(name != null){
                    predicates.add(criteriaBuilder.like(root.join("client").get("name"), "%"+name+"%"));
                }

                if(number != null){
                    predicates.add(criteriaBuilder.equal(root.get("number"), number));
                }

                if(regionId != null){
                    predicates.add(criteriaBuilder.equal(root.get("regionId"), regionId));
                }

                if(subRegionId != null){
                    predicates.add(criteriaBuilder.equal(root.get("subRegionId"), subRegionId));
                }

                if(dateBegin != null && dateEnd != null){
                    predicates.add(criteriaBuilder.between(root.get("date"), dateBegin, dateEnd));
                }

                if(dateBegin != null && dateEnd == null){
                    predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("date"), dateBegin));
                }

                if(dateBegin == null && dateEnd != null){
                    predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("date"), dateEnd));
                }

                Predicate overAll = criteriaBuilder.and(predicates.toArray(new Predicate[0]));
                return overAll;
            }
        };
    }

    public Coordinate findById(Integer id){
        return coordinateRepository.findTop1ByIdOrderByIdDesc(id);
    }

    public void coordinateView(Integer regApplicationId, Model model){
        Coordinate coordinate = coordinateRepository.findTop1ByRegApplicationIdAndDeletedFalseOrderByIdDesc(regApplicationId);
        if(coordinate != null){
            List<CoordinateLatLong> coordinateLatLongList = coordinateLatLongRepository.getByCoordinateIdAndDeletedFalse(coordinate.getId());
            model.addAttribute("coordinate", coordinate);
            model.addAttribute("coordinateLatLongList", coordinateLatLongList);
        }
    }

    @Override
    public Coordinate getRegApplicationId(Integer id) {
        return coordinateRepository.findByRegApplicationIdAndDeletedFalse(id);
    }

    @Override
    public Coordinate saveForEdit(Coordinate coordinate) {
        return coordinateRepository.save(coordinate);
    }
}
