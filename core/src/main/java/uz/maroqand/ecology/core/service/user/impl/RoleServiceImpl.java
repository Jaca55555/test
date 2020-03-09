package uz.maroqand.ecology.core.service.user.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.stereotype.Service;
import uz.maroqand.ecology.core.entity.user.Role;
import uz.maroqand.ecology.core.repository.user.RoleRepository;
import uz.maroqand.ecology.core.service.user.RoleService;

import java.util.List;

@Service
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    @Autowired
    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public DataTablesOutput<Role> getAll(DataTablesInput input) {
        return roleRepository.findAll(input);
    }

    @Override
    public DataTablesOutput<Role> getAll(Integer organizationId, DataTablesInput input) {
        return roleRepository.findAll(input);
    }

    @Override
    public Role getById(Integer id) {
        return roleRepository.findById(id).get();
    }

    @Override
    public List<Role> getRoleList() {
        return roleRepository.findAll();
    }

    @Override
    public Role createRole(Role role) {
        return roleRepository.save(role);
    }

    @Override
    public Role updateRole(Role role) {
        return roleRepository.save(role);
    }

    @Override
    public void deleteRole(Role role) {
        roleRepository.save(role);
    }

}
