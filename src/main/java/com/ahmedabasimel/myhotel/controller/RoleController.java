package com.ahmedabasimel.myhotel.controller;


import com.ahmedabasimel.myhotel.exception.RoleAlreadyExistsException;
import com.ahmedabasimel.myhotel.models.Role;
import com.ahmedabasimel.myhotel.models.User;
import com.ahmedabasimel.myhotel.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/roles")
public class RoleController {
    private final RoleService roleService;

    @GetMapping("/all-roles")
    public ResponseEntity<List<Role>> getAllRoles(){
        return new ResponseEntity<>(roleService.getRoles(), HttpStatus.FOUND);
    }

    @PostMapping("/create-new-role")
    public ResponseEntity<String> createRole(@RequestBody Role role){
        try{
            roleService.createRole(role);
            return ResponseEntity.ok("New role created successfully");

        }catch(RoleAlreadyExistsException e){
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @DeleteMapping("delete/{roleId}")
    public void deleteRole(@PathVariable("roleId") Long roleId){
        roleService.deleteRole(roleId);

    }

    @PostMapping("/remove-all-users/{roleId}")
    public Role removeAllUsersFromRole(@PathVariable("roleId") Long roleId ){
        return roleService.removeAllUsersFromRole(roleId);
    }

    @PostMapping("/remove-user-from-role")
    public User removeUserFromRole(
            @RequestParam Long userId,
            @RequestParam Long roleId){
        return roleService.removeUserFromRole(userId, roleId);
    }

    public User assignRoleToUser(@RequestParam Long userId,
                                 @RequestParam Long roleId){
        return roleService.assignRoleToUser(userId, roleId);


    }

}
