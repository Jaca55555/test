package uz.maroqand.ecology.core.repository.sys;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.maroqand.ecology.core.entity.sys.Opf;

import java.util.List;

@Repository
public interface OpfRepository extends JpaRepository<Opf, Integer> {

    List<Opf> findByOrderByIdAsc();

}
