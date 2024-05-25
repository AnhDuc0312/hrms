package com.hrms.sys.services.employee;

import com.hrms.sys.dtos.EmployeeDTO;
import com.hrms.sys.exceptions.InvalidDataException;
import com.hrms.sys.models.*;
import com.hrms.sys.repositories.*;
import com.hrms.sys.responses.EmployeeResponse;
import com.hrms.sys.services.user.UserService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmployeeService implements IEmployeeService {
    private final EmployeeRepository employeeRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final BenefitRepository benefitRepository;
    private final PayrollRepository payrollRepository;
    private final DepartmentRepository departmentRepository;
    private final UserService userService;
    @Value("${upload.dir}")
    private String uploadDir;


    @Override
    public Employee createEmployee(EmployeeDTO employeeDTO, MultipartFile avatar) throws Exception {
        Benefit benefit = Benefit.builder()
                .allowance(employeeDTO.getAllowance())
                .bonus(1000000F)
                .disabilityAllowance(0F)
                .healthInsurance(0F)
                .maternityInsurance(0F)
                .socialInsurance(0F)
                .workAccidentInsurance(0F)
                .build();
        Optional<Department> department = departmentRepository.findById(1L);
        benefitRepository.save(benefit);
        Payroll payroll = Payroll.builder()
                .basicSalary(employeeDTO.getBasicSalary())
                .tax(0F)
                .basicSalaryReceived(0F)
                .benefit(benefit.getBonus())
                .date(LocalDateTime.now())
                .lateDeduction(0F)
                .overtimeSalary(0F)
                .permittedLeave(0F)
                .quarterlyAward(0F)
                .salaryOvertime(0F)
                .insurance(0F)
                .totalHoursWorked(0F)
                .totalSalary(0F)
                .totalHoursOvertime(0F)

                .build();
        payrollRepository.save(payroll);
        Employee newEmployee = Employee.builder()
                .fullName(employeeDTO.getFullName())
                .gender(employeeDTO.getGender())
                .email(employeeDTO.getEmail())
                .address(employeeDTO.getAddress())
                .phoneNumber(employeeDTO.getPhoneNumber())
                .dateOfBirth(employeeDTO.getDateOfBirth())
                .contactStartDate(employeeDTO.getContactStartDate())
                .contactEndDate(employeeDTO.getContactEndDate())
                .position(employeeDTO.getPosition())
                .education(employeeDTO.getEducation())
                .benifit(benefit)
                .payroll(payroll)
                .department(department.get())
                .remainingOvertimeHours(30)
                .remainingPaidLeaveDays(12)
                .remainingRemoteDays(48)
                .build();

        Employee savedEmployee = employeeRepository.save(newEmployee);


        Role role = roleRepository.findById(3L)
                .orElseThrow(() -> new InvalidDataException("Role not found"));



        User user = new User();
        String username = generateRandomUsername();
        String password = formatDate(employeeDTO.getDateOfBirth());
        String encodedPassword = passwordEncoder.encode(password);
        user.setUsername(username);
        user.setPassword(encodedPassword);
        user.setEmail(employeeDTO.getEmail());
        user.setRole(role);

        user.setFullName(savedEmployee.getFullName());

        savedEmployee.setUser(user);

//        payroll.setUser(user);
//        benefit.setUser(user);


        userRepository.save(user);
        payrollRepository.save(payroll);
        benefitRepository.save(benefit);
        employeeRepository.save(savedEmployee);

//        newEmployee.setUser(user);




        if (avatar != null && !avatar.isEmpty()) {
            try {
                // Tạo tên file duy nhất
                String fileName = UUID.randomUUID().toString() + "_" + avatar.getOriginalFilename();

                // Tạo đường dẫn tới thư mục lưu trữ và tạo file
                Path path = Paths.get(uploadDir + "/" + fileName);
                Files.copy(avatar.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

                // Gán đường dẫn URL của hình ảnh cho nhân viên
                newEmployee.setAvatarUrl("/image/" + fileName);
                employeeRepository.save(savedEmployee);
            } catch (IOException e) {
                e.printStackTrace();
                // Xử lý nếu có lỗi khi tải lên
                // throw new RuntimeException("Error uploading avatar");
            }
        }
        else {
            newEmployee.setAvatarUrl(uploadDir + "/avatar-test.jpg" );
        }
        return null;
    }

    @Override
    public List<EmployeeResponse> getAllEmployees( String keyword) throws Exception{
        // Tạo đối tượng Pageable để phân trang và sắp xếp
        List<Employee> employeeList;
        employeeList = employeeRepository.searchEmployees(keyword);
        return employeeList.stream()
                .map(EmployeeResponse::fromEmployee)
                .collect(Collectors.toList());
    }

    @Override
    public EmployeeResponse getEmployeeById(Long id) throws Exception {
        Optional<Employee> employeeOptional = employeeRepository.findById(id);
        if (employeeOptional.isPresent()) {
            Employee employee = employeeOptional.get();
            return new EmployeeResponse(
                    employee.getFullName(),
                    employee.getGender(),
                    employee.getDateOfBirth(),
                    employee.getEmail(),
                    employee.getPhoneNumber(),
                    employee.getAddress(),
                    employee.getContactStartDate(),
                    employee.getContactEndDate(),
                    employee.getPosition(),
                    employee.getEducation(),
                    employee.getUser().getUsername(),
                    employee.getUser().getDepartment() != null ? employee.getUser().getDepartment().getName() : "chưa xét",
                    employee.getBenifit() !=null ? employee.getBenifit().getAllowance() : 0 ,
                    employee.getPayroll() != null ? employee.getPayroll().getBasicSalary() : 0,
                    0
                    );
        } else {
            throw new Exception("Employee not found with ID: " + id);
        }
    }

    public EmployeeResponse getEmployeeByUsername(String username) throws Exception {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Optional<Employee> employeeOptional = employeeRepository.findByUser(user);
        if (employeeOptional.isPresent()) {
            Employee employee = employeeOptional.get();
            return new EmployeeResponse(
                    employee.getFullName(),
                    employee.getGender(),
                    employee.getDateOfBirth(),
                    employee.getEmail(),
                    employee.getPhoneNumber(),
                    employee.getAddress(),
                    employee.getContactStartDate(),
                    employee.getContactEndDate(),
                    employee.getPosition(),
                    employee.getEducation(),
                    employee.getUser().getUsername(),
                    employee.getDepartment() != null ? employee.getDepartment().getName() : "chưa xét",
                    employee.getBenifit() !=null ? employee.getBenifit().getAllowance() : 0 ,
                    employee.getPayroll() != null ? employee.getPayroll().getBasicSalary() : 0,
                    0
            );
        } else {
            throw new Exception("Employee not found with ID: " + username);
        }
    }

    @Override
    public void deleteEmployee(String username) throws Exception {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Optional<Employee> employeeOptional = employeeRepository.findByUser(user);

        employeeOptional.ifPresent(employee -> {
            // Xóa người dùng liên kết với nhân viên trước
            User userRe = employee.getUser();
            if (userRe != null) {
                employee.setUser(null);
                userRepository.delete(userRe);
            }
            // Xóa nhân viên sau
            employeeRepository.delete(employee);
            Optional<Benefit> benefit = benefitRepository.findById(employeeOptional.get().getBenifit().getId());
            benefitRepository.delete(benefit.get());
            Optional<Payroll> payroll =  payrollRepository.findById(employeeOptional.get().getPayroll().getId());
            payrollRepository.delete(payroll.get());
        });


    }

    @Override
    public Employee updateEmployee(String username, EmployeeDTO employeeDTO) throws Exception {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Optional<Employee> employeeOptional = employeeRepository.findByUser(user);
        if (employeeOptional.isPresent()) {
            Employee employee = employeeOptional.get();

            // Cập nhật thông tin từ employeeDTO vào đối tượng Employee
            employee.setEmail(employeeDTO.getEmail());
            employee.setAddress(employeeDTO.getAddress());
            employee.setPhoneNumber(employeeDTO.getPhoneNumber());

            // Lưu và trả về nhân viên đã được cập nhật
            return employeeRepository.save(employee);
        } else {
            throw new Exception("Employee not found");
        }
    }


    private String generateRandomUsername() {
        return RandomStringUtils.randomNumeric(8);
    }


    public String formatDate(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyyyy");
        return date.format(formatter);
    }
}
