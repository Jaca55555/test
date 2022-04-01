package uz.maroqand.ecology.core.repository.sys;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.maroqand.ecology.core.constant.sys.AppealStatus;
import uz.maroqand.ecology.core.entity.sys.Appeal;

import java.util.List;

@Repository
public interface AppealRepository extends JpaRepository<Appeal, Integer>, JpaSpecificationExecutor<Appeal> {

    Appeal findByIdAndCreatedByIdAndDeletedFalse(Integer id,Integer createdById);
    Integer countAllByAppealStatusAndDeletedFalse(AppealStatus status);
    @Query("SELECT a.createdById,SUM(a.showUserCommentCount) FROM Appeal a WHERE a.showUserCommentCount>0 GROUP BY a.createdById")
    List<Object[]> findIdByShowUserCommentCount();

    @Query("SELECT SUM(a.showUserCommentCount) FROM Appeal a WHERE a.showUserCommentCount>0 and a.createdById =:userId GROUP BY a.createdById")
    Integer findIdByShowUserCommentCountUserId(@Param("userId") Integer userId);

}
