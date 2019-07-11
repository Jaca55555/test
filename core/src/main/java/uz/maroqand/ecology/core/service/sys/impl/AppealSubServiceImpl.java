package uz.maroqand.ecology.core.service.sys.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uz.maroqand.ecology.core.entity.sys.AppealSub;
import uz.maroqand.ecology.core.entity.user.User;
import uz.maroqand.ecology.core.repository.sys.AppealSubRepository;
import uz.maroqand.ecology.core.service.sys.AppealSubService;

import java.util.Date;
import java.util.List;

@Service
public class AppealSubServiceImpl implements AppealSubService {

    private AppealSubRepository appealSubRepository;

    @Autowired
    public AppealSubServiceImpl(AppealSubRepository appealSubRepository) {
        this.appealSubRepository = appealSubRepository;
    }

    public List<AppealSub> getById(Integer id){
        return appealSubRepository.findByAppealIdAndDeletedFalseOrderByIdAsc(id);
    }

    public AppealSub create(AppealSub appealSub, User user){
        appealSub.setDeleted(false);
        appealSub.setAdminWrite(false);
        appealSub.setInProgress(false);
        appealSub.setClosed(false);
        appealSub.setCreatedById(user.getId());
        appealSub.setCreatedAt(new Date());
        return appealSubRepository.save(appealSub);
    }

    @Override
    public AppealSub update(AppealSub appealSub,User user) {
        appealSub.setDeleted(false);
        appealSub.setAdminWrite(true);
        appealSub.setCreatedById(user.getId());
        appealSub.setCreatedAt(new Date());
        return appealSubRepository.save(appealSub);
    }
}
