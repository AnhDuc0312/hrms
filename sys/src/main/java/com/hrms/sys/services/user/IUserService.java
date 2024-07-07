package com.hrms.sys.services.user;

import com.hrms.sys.dtos.UpdateUserDTO;
import com.hrms.sys.exceptions.InvalidDataException;
import com.hrms.sys.exceptions.NotFoundException;
import com.hrms.sys.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface IUserService {
    List<User> getUsers() throws Exception;

//    Page<User> getAllUsers(String keyword, Long employeeId, Long departmentId) throws Exception;

    User getUser(Long id) throws Exception;

    void changePassword(Long id, String oldPassword, String newPassword)throws Exception;

    void deleteUser(Long id) throws Exception;

    User resetUser(String username) throws Exception;

    String login(String username , String password ) throws Exception;

    User getUserDetailsFromToken(String token) throws Exception;


    User getUserDetailsFromRefreshToken(String token) throws Exception;
    User updateUser(Long userId, UpdateUserDTO updatedUserDTO) throws Exception;

    Page<User> findAll(String keyword, Pageable pageable) throws Exception;
    void resetPassword(Long userId, String newPassword)
            throws Exception ;
    public void blockOrEnable(Long userId, Boolean active) throws InvalidDataException;

    User getUserById(Long userId) throws NotFoundException;
}
