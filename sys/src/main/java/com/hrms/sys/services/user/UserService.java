package com.hrms.sys.services.user;

import com.hrms.sys.dtos.UpdateUserDTO;
import com.hrms.sys.dtos.UserRegisterDTO;
import com.hrms.sys.exceptions.InvalidDataException;
import com.hrms.sys.exceptions.NotFoundException;
import com.hrms.sys.models.Employee;
import com.hrms.sys.models.Role;
import com.hrms.sys.models.Token;
import com.hrms.sys.models.User;
import com.hrms.sys.repositories.EmployeeRepository;
import com.hrms.sys.repositories.RoleRepository;
import com.hrms.sys.repositories.TokenRepository;
import com.hrms.sys.repositories.UserRepository;
import com.hrms.sys.responses.UserChatResponse;
import com.hrms.sys.services.user.IUserService;
import com.hrms.sys.utils.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class UserService implements IUserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtil jwtTokenUtil;
    private final AuthenticationManager authenticationManager;
    private final TokenRepository tokenRepository;
    private final EmployeeRepository employeeRepository;

    @Override
    public List<User> getUsers() throws Exception {
        return userRepository.findAll();
    }

    @Override
    public User getUser(Long id) throws Exception {
        Optional<User> userOptional = userRepository.findById(id);
        return userOptional.orElse(null);
    }

    @Override
    public void changePassword(Long id, String oldPassword, String newPassword) throws Exception {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new InvalidDataException("User not found"));

        // Kiểm tra mật khẩu cũ
        if (!passwordEncoder.matches(oldPassword, existingUser.getPassword())) {
            throw new InvalidDataException("Mật khẩu cũ không chính xác");
        }

        String encodedNewPassword = passwordEncoder.encode(newPassword);
        existingUser.setPassword(encodedNewPassword);

        userRepository.save(existingUser);
    }


    @Override
    public void deleteUser(Long id) throws Exception {
        userRepository.deleteById(id);
    }

    @Override
    public User resetUser(String username) throws Exception {
//        Employee employee = employeeRepository.findById(id).orElseThrow(() -> new InvalidDataException("Employee not found"));

        Optional<User> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();


            LocalDate currentDate = LocalDate.now();

            // Chuyển đổi ngày hiện tại thành chuỗi theo định dạng "yyyyMMdd"
            String newPassword = currentDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            String encodeNewPass = passwordEncoder.encode(newPassword);

            user.setPassword(encodeNewPass);
            // Lưu người dùng với mật khẩu mới
            userRepository.save(user);
            return user;
        } else {
            throw new Exception("User not found");
        }
    }

    @Override
    public String login(String username, String password) throws Exception {

        Optional<User> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isEmpty()) {
            throw new InvalidDataException("Invalid phonenumber / password");
        }

        User existingUser = optionalUser.get();

        // Check password
        if (!passwordEncoder.matches(password, existingUser.getPassword())) {
            throw new BadCredentialsException("Wrong phone number / password");
        }

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                username, password, existingUser.getAuthorities()
        );

        authenticationManager.authenticate(authenticationToken);
        Long roleId = existingUser.getRole().getId();

        return jwtTokenUtil.generateToken(existingUser);

    }

    //Lấy thông tin chi tieets của người dùng
    @Override
    public User getUserDetailsFromToken(String token) throws Exception {
        if (jwtTokenUtil.isTokenExpired(token)) {
            throw new Exception("Token is expired");
        }

        String username = jwtTokenUtil.extractUsername(token);
        Optional<User> optionalUser = userRepository.findByUsername(username);

        if (optionalUser.isPresent()) {
            return optionalUser.get();
        } else {
            throw new Exception("User not found");
        }
    }

    public User createUser(UserRegisterDTO userDTO) throws Exception {
        String username = userDTO.getUsername();
        if (userRepository.existsByUsername(username))
            throw new DataIntegrityViolationException("Phong number already exists");

        Role role = roleRepository.findById(userDTO.getRoleId())
                .orElseThrow(() -> new InvalidDataException("Role not found"));

        User newUser = User.builder()
                .username(userDTO.getUsername())
                .password(userDTO.getPassword())
                .role(role)
                .email(userDTO.getEmail())
                .build();

        newUser.setRole(role);

            String password = userDTO.getPassword();
            String encodedPassword = passwordEncoder.encode(password);
            newUser.setPassword(encodedPassword);

        return userRepository.save(newUser) ;
    }

    @Override
    public User getUserDetailsFromRefreshToken(String refreshToken) throws Exception {
        Token existingToken = tokenRepository.findByRefreshToken(refreshToken);
        return getUserDetailsFromToken(existingToken.getToken());
    }

    @Override
    public User updateUser(Long userId, UpdateUserDTO updatedUserDTO) throws Exception {
        return null;
    }

    @Override
    public Page<User> findAll(String keyword, Pageable pageable) {
//        return userRepository.findAll(keyword, pageable);
        return null;
    }

    @Override
    @Transactional
    public void resetPassword(Long userId, String newPassword)
            throws Exception {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new InvalidDataException("User not found"));
        String encodedPassword = passwordEncoder.encode(newPassword);
        existingUser.setPassword(encodedPassword);
        userRepository.save(existingUser);
        //reset password => clear token
        List<Token> tokens = tokenRepository.findByUser(existingUser);
        for (Token token : tokens) {
            tokenRepository.delete(token);
        }
    }

    @Override
    @Transactional
    public void blockOrEnable(Long userId, Boolean active) throws InvalidDataException {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new InvalidDataException("User not found"));
        userRepository.save(existingUser);
    }

    public User getUserById(Long userId) throws NotFoundException {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + userId));
    }

    public List<UserChatResponse> getAllUser() throws Exception {
        List<UserChatResponse> userChatResponses = new ArrayList<>();
        List<User> users = userRepository.findAll();
        for (User user : users) {
            UserChatResponse userChatResponse = new UserChatResponse();
            userChatResponse.setId(user.getId());
            userChatResponse.setUsername(user.getUsername());
            userChatResponse.setFullname(user.getFullName());
            userChatResponse.setRole(user.getRole().getName());
            userChatResponse.setEmail(user.getEmail());
            userChatResponses.add(userChatResponse);
        }
        return userChatResponses;
    }

    public User updateUserRole(Long userId, Long roleId) throws NotFoundException, InvalidDataException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new InvalidDataException("Role not found"));

        user.setRole(role);
        return userRepository.save(user);
    }


}
