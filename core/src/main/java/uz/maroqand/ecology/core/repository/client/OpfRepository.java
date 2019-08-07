package uz.maroqand.ecology.core.repository.client;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.maroqand.ecology.core.entity.client.Opf;

import java.util.List;
import java.util.Set;

@Repository
public interface OpfRepository extends JpaRepository<Opf, Integer> {

    List<Opf> findByIdInOrParentIdInOrderByIdAsc(Set<Integer> ids, Set<Integer> parentIds);

    List<Opf> findByOrderByIdAsc();

    Opf findFirstByNameRu(String nameRu);

}
