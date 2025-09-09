package com.ahmedabasimel.myhotel.service.impl;

import com.ahmedabasimel.myhotel.exception.RoleAlreadyExistsException;
import com.ahmedabasimel.myhotel.exception.UserAlreadyExistsException;
import com.ahmedabasimel.myhotel.models.Role;
import com.ahmedabasimel.myhotel.models.User;
import com.ahmedabasimel.myhotel.repository.RoleRepository;
import com.ahmedabasimel.myhotel.repository.UserRepository;
import com.ahmedabasimel.myhotel.service.RoleService;
import com.ahmedabasimel.myhotel.service.UserService;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    @Override
    public List<Role> getRoles() {
        return roleRepository.findAll();

    }

    @Override
    public Role createRole(Role role) {
       String roleName = "ROLE_"+ role.getName().toUpperCase();
       Role newRole = new Role(roleName);
       if(roleRepository.existsByName(newRole)){
           throw new RoleAlreadyExistsException(role.getName() +"already exists");
       }
       return roleRepository.save(newRole);
    }

    @Override
    public void deleteRole(Long roleId) {
        this.removeAllUsersFromRole(roleId);
        roleRepository.deleteById(roleId);


    }

    @Override
    public Role findByName(String name) {
        return roleRepository.findByName(name).get();
    }



    @Override

    public User removeUserFromRole(Long userId, Long roleId) {
        Optional<User> user = userRepository.findById(userId);
        Optional<Role>  role = roleRepository.findById(roleId);
        if (role.isPresent() && role.get().getUsers().contains(user.get())){
            role.get().removeUserFromRole(user.get());
            roleRepository.save(role.get());
            return user.get();
        }
        throw new UsernameNotFoundException("User not found");
    }

    @Override
    public User assignRoleToUser(Long userId, Long roleId) {
        Optional<User> user = userRepository.findById(userId);
        Optional<Role> role = roleRepository.findById(roleId);
        if(user.isPresent() && user.get().getRoles().contains(role.get())){
            throw new UserAlreadyExistsException(user.get().getFirstName() + " is already assigned to the " + role.get().getName() +"role");
        }
        if(role.isPresent()){
            role.get().assignRoleToUser(user.get());
            roleRepository.save(role.get());
        }
        return user.get();

    }

    @Override
    public Role removeAllUsersFromRole(Long roleId) {
        Optional<Role> role = roleRepository.findById(roleId);
        role.get().removeAllUsersFromRole();
        return roleRepository.save(role.get());


    }


}
