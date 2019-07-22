package uz.maroqand.ecology.core.service.user.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uz.maroqand.ecology.core.entity.sys.File;
import uz.maroqand.ecology.core.entity.user.EvidinceStatus;
import uz.maroqand.ecology.core.entity.user.UserEvidence;
import uz.maroqand.ecology.core.repository.user.UserEvidenceRepository;
import uz.maroqand.ecology.core.service.user.UserEvidenceService;

import java.util.List;
import java.util.Set;

@Service
public class UserEvidenceServiceImpl implements UserEvidenceService {

    private final UserEvidenceRepository userEvidenceRepository;

    @Autowired
    public UserEvidenceServiceImpl(UserEvidenceRepository userEvidenceRepository) {
        this.userEvidenceRepository = userEvidenceRepository;
    }

    @Override
    public UserEvidence save(UserEvidence userEvidence) {
        return userEvidenceRepository.save(userEvidence);
    }

    @Override
    public List<UserEvidence> getListByUserId(Integer userId) {
        return userEvidenceRepository.findByUserId(userId);
    }

    @Override
    public UserEvidence getById(Integer id) {
        return userEvidenceRepository.findById(id).get();
    }
}