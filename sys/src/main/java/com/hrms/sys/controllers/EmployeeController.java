package com.hrms.sys.controllers;

import com.hrms.sys.dtos.EmployeeDTO;
import com.hrms.sys.models.Employee;
import com.hrms.sys.responses.EmployeeListResponse;
import com.hrms.sys.responses.EmployeeResponse;
import com.hrms.sys.services.employee.EmployeeRedisService;
import com.hrms.sys.services.employee.EmployeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;
    private final EmployeeRedisService employeeRedisService;



    @PostMapping(value = "" ,consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createEmployee(
            @Valid @ModelAttribute EmployeeDTO employeeDTO,
            @ModelAttribute MultipartFile avatar,
            BindingResult result){
        try {
            if(result.hasErrors()) {
                List<String> errorMessages = result.getFieldErrors()
                        .stream()
                        .map(FieldError::getDefaultMessage)
                        .toList();
                return ResponseEntity.badRequest().body(errorMessages);
            }
            Employee newEmployee = employeeService.createEmployee(employeeDTO,avatar);
            return ResponseEntity.ok("CREATE EMPLOYEE SUCCESSFULLY");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("")
    public ResponseEntity<EmployeeListResponse> getAllEmployees(@RequestParam(defaultValue = "0") int page,
                                          @RequestParam( defaultValue = "10") int size,
                                          @RequestParam( defaultValue = "1", name = "department_id") Long departmentId,
                                          @RequestParam( defaultValue = "") String keyword) throws Exception {

        int totalPages = 0;

        PageRequest pageRequest = PageRequest.of(
                page,size, Sort.by("id").ascending()
        );
        List<EmployeeResponse> employeeResponses = employeeRedisService.getAllEmployees(keyword , departmentId, pageRequest);

        if (employeeResponses != null && !employeeResponses.isEmpty()) {
            totalPages = employeeResponses.get(0).getTotalPages();
        }
        if (employeeResponses == null) {
            Page<EmployeeResponse> responsePage = employeeService.getAllEmployees( pageRequest,  departmentId, keyword);
            totalPages = responsePage.getTotalPages();
            employeeResponses = responsePage.getContent();

            for (EmployeeResponse employee : employeeResponses) {
                employee.setTotalPages(totalPages);
            }

//            employeeRedisService.saveAllEmployees(
//                    employeeResponses,keyword, departmentId, pageRequest
//
//            );
        }
        return ResponseEntity.ok(EmployeeListResponse
                .builder()
                        .employees(employeeResponses)
                        .totalPages(totalPages)
                .build()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getEmployeeById(
            @PathVariable("id") Long employeeId
    ) {
        try {
            EmployeeResponse employee = employeeService.getEmployeeById(employeeId);
            return ResponseEntity.ok().body(employee);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Employee not found with ID: " + employeeId);
        }

    }

    @GetMapping("/user/{username}")
    public ResponseEntity<?> getEmployeeByUsername(
            @PathVariable("username") String username
    ) {
        try {
            EmployeeResponse employee = employeeService.getEmployeeByUsername(username);
            return ResponseEntity.ok().body(employee);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Employee not found with ID: " + username);
        }

    }

    @PutMapping("/{username}")
    public ResponseEntity<?> updateEmployee(
            @PathVariable("username") String username,
            @RequestBody EmployeeDTO employeeDTO
    ) {
        try {
            // Gọi service để cập nhật thông tin của nhân viên với ID được cung cấp
            employeeService.updateEmployee(username, employeeDTO);
            return ResponseEntity.ok("{\"message\": \"Employee updated successfully\"}");
        } catch (Exception e) {
            // Trả về lỗi nếu có bất kỳ ngoại lệ nào xảy ra trong quá trình cập nhật
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating employee");
        }
    }

    @DeleteMapping("/{username}")
    public ResponseEntity<?> deleteEmployee(
            @PathVariable("username") String username
    ){
        try {
            employeeService.deleteEmployee(username);
            return ResponseEntity.ok().body("{\"message\": \"Employee deleted successfully\"}");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error deleting employee with ID " + username + ": " + e.getMessage());
        }

    }



}
