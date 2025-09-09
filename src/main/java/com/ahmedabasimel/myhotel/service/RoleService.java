package com.ahmedabasimel.myhotel.service;

import com.ahmedabasimel.myhotel.models.Role;
import com.ahmedabasimel.myhotel.models.User;

import java.util.List;

public interface RoleService {

    List<Role> getRoles();
    Role createRole(Role role);
    void deleteRole(Long id);
    Role findByName(String name);

    User removeUserFromRole(Long userId, Long roleId);

    User assignRoleToUser(Long userId, Long roleId);

    Role removeAllUsersFromRole(Long roleId);



}
