package com.hrms.sys.controllers;

import com.hrms.sys.dtos.*;
import com.hrms.sys.exceptions.InvalidDataException;
import com.hrms.sys.exceptions.NotFoundException;
import com.hrms.sys.models.Token;
import com.hrms.sys.models.User;
import com.hrms.sys.responses.LoginResponse;
import com.hrms.sys.responses.RegisterResponse;
import com.hrms.sys.responses.ResponseObject;
import com.hrms.sys.responses.UserResponse;
import com.hrms.sys.services.token.TokenService;
import com.hrms.sys.services.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("${api.prefix}/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final TokenService tokenService;

    //    @GetMapping("")
//    public ResponseEntity<List<User>> getAllUsers(
//            @RequestParam(defaultValue = "") String keyword,
//            @RequestParam(defaultValue = "0") Long employeeId,
//            @RequestParam(defaultValue = "0") Long departmentId,
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "10") int size
//    ) {
//        try {
//            List<User> users = (List<User>) userService.getAllUsers(keyword,employeeId,departmentId);
//            return ResponseEntity.ok(users);
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
//        }
//    }
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody UserLoginDTO userLoginDTO) {
        //Kiểm tra thông tin đăng nhập và sinh token

        try {
            String token = userService.login(
                    userLoginDTO.getUsername(),
                    userLoginDTO.getPassword(),
                    userLoginDTO.getRoleId() == null ? 1 : userLoginDTO.getRoleId());

            return ResponseEntity.ok(LoginResponse.builder()
                    .message("LOGIN_SUCCESSFULLY")
                    .token(token)
                            .username(userLoginDTO.getUsername())
                    .build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    LoginResponse.builder()
                            .message("LOGIN_FAIL")
                            .build());
        }

    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> createUser(
            @Valid @RequestBody UserRegisterDTO userDTO,
            BindingResult result
    ){
        RegisterResponse registerResponse = new RegisterResponse();

        if (result.hasErrors()) {
            List<String> errorMessages = result.getFieldErrors()
                    .stream()
                    .map(FieldError::getDefaultMessage)
                    .toList();
            registerResponse.setMessage(errorMessages.toString());
            return ResponseEntity.badRequest().body(registerResponse);
        }
        if (!userDTO.getPassword().equals(userDTO.getRetypePassword())){
            registerResponse.setMessage("PASSWORD_NOT_MATCH");
            return ResponseEntity.badRequest().body(registerResponse);
        }
        try {
            User user = userService.createUser(userDTO);
            registerResponse.setMessage("REGISTER_SUCCESSFULLY");
            registerResponse.setUser(user);
            return ResponseEntity.ok(registerResponse);
        }catch (Exception e) {
            registerResponse.setMessage(e.getMessage());
            return ResponseEntity.badRequest().body(registerResponse);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable("id") Long id) {
        try {
            User user = userService.getUser(id);
            if (user != null) {
                return ResponseEntity.ok(user);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PutMapping("/{id}/change-password")
    public ResponseEntity<String> changePassword(
            @PathVariable("id") Long id,
            @RequestParam("oldPassword") String oldPassword,
            @RequestParam("newPassword") String newPassword) {
        try {
            userService.changePassword(id, oldPassword, newPassword);
            return ResponseEntity.ok().body("{\"message\": \"Password updated successfully\"}");
        } catch (InvalidDataException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    //Xóa employee thì mới xóa được user or auto xóa
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable("id") Long id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    // Reset password
    @PostMapping("/{username}/reset-password")
    public ResponseEntity<User> resetUser(@PathVariable("username") String username) {
        try {
            User user = userService.resetUser(username);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @PostMapping("/refreshToken")
    public ResponseEntity<ResponseObject> refreshToken(
            @Valid @RequestBody RefreshTokenDTO refreshTokenDTO
    ) throws Exception {
        User userDetail = userService.getUserDetailsFromRefreshToken(refreshTokenDTO.getRefreshToken());
        Token jwtToken = tokenService.refreshToken(refreshTokenDTO.getRefreshToken(), userDetail);
        LoginResponse loginResponse = LoginResponse.builder()
                .message("Refresh token successfully")
                .token(jwtToken.getToken())
                .tokenType(jwtToken.getTokenType())
                .refreshToken(jwtToken.getRefreshToken())
                .username(userDetail.getUsername())
                .roles(userDetail.getAuthorities().stream().map(item -> item.getAuthority()).toList())
                .id(userDetail.getId()).build();
        return ResponseEntity.ok().body(
                ResponseObject.builder()
                        .data(loginResponse)
                        .message(loginResponse.getMessage())
                        .status(HttpStatus.OK)
                        .build());

    }
    private boolean isMobileDevice(String userAgent) {
        // Kiểm tra User-Agent header để xác định thiết bị di động
        // Ví dụ đơn giản:
        return userAgent.toLowerCase().contains("mobile");
    }
    @PostMapping("/details")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public ResponseEntity<ResponseObject> getUserDetails(
            @RequestHeader("Authorization") String authorizationHeader
    ) throws Exception {
        String extractedToken = authorizationHeader.substring(7); // Loại bỏ "Bearer " từ chuỗi token
        User user = userService.getUserDetailsFromToken(extractedToken);
        return ResponseEntity.ok().body(
                ResponseObject.builder()
                        .message("Get user's detail successfully")
                        .data(UserResponse.fromUser(user))
                        .status(HttpStatus.OK)
                        .build()
        );
    }
    @PutMapping("/details/{userId}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    @Operation(security = { @SecurityRequirement(name = "bearer-key") })
    public ResponseEntity<ResponseObject> updateUserDetails(
            @PathVariable Long userId,
            @RequestBody UpdateUserDTO updatedUserDTO,
            @RequestHeader("Authorization") String authorizationHeader
    ) throws Exception{
        String extractedToken = authorizationHeader.substring(7);
        User user = userService.getUserDetailsFromToken(extractedToken);
        // Ensure that the user making the request matches the user being updated
        if (user.getId() != userId) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        User updatedUser = userService.updateUser(userId, updatedUserDTO);
        return ResponseEntity.ok().body(
                ResponseObject.builder()
                        .message("Update user detail successfully")
                        .data(UserResponse.fromUser(updatedUser))
                        .status(HttpStatus.OK)
                        .build()
        );
    }
    @PutMapping("/reset-password/{userId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ResponseObject> resetPassword(@Valid @PathVariable long userId){
        try {
            String newPassword = UUID.randomUUID().toString().substring(0, 5); // Tạo mật khẩu mới
            userService.resetPassword(userId, newPassword);
            return ResponseEntity.ok(ResponseObject.builder()
                    .message("Reset password successfully")
                    .data(newPassword)
                    .status(HttpStatus.OK)
                    .build());
        } catch (InvalidDataException e) {
            return ResponseEntity.ok(ResponseObject.builder()
                    .message("Invalid password")
                    .data("")
                    .status(HttpStatus.BAD_REQUEST)
                    .build());
        } catch (NotFoundException e) {
            return ResponseEntity.ok(ResponseObject.builder()
                    .message("User not found")
                    .data("")
                    .status(HttpStatus.BAD_REQUEST)
                    .build());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    @PutMapping("/block/{userId}/{active}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ResponseObject> blockOrEnable(
            @Valid @PathVariable long userId,
            @Valid @PathVariable int active
    ) throws Exception {
        userService.blockOrEnable(userId, active > 0);
        String message = active > 0 ? "Successfully enabled the user." : "Successfully blocked the user.";
        return ResponseEntity.ok().body(ResponseObject.builder()
                .message(message)
                .status(HttpStatus.OK)
                .data(null)
                .build());
    }
}
