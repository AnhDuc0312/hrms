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

    @PutMapping("/{id}")
    public ResponseEntity<?> updateEmployee(
            @PathVariable("id") Long employeeId
    ){
        return ResponseEntity.badRequest().body("");

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEmployee(
            @PathVariable("id") Long employeeId
    ){
        try {
            employeeService.deleteEmployee(employeeId);
            return ResponseEntity.ok().body("Employee with ID " + employeeId + " has been deleted successfully.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error deleting employee with ID " + employeeId + ": " + e.getMessage());
        }

    }



}
