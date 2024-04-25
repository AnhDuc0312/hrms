//package com.hrms.sys.controllers;
//
//import com.hrms.sys.dtos.EmployeeDTO;
//import lombok.AllArgsConstructor;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//public class DepartmentDetailsController {
//    import com.hrms.sys.dtos.DepartmentDetailDTO;
//import com.hrms.sys.services.DepartmentDetailService;
//import lombok.AllArgsConstructor;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//    @RestController
//    @RequestMapping("/api/v1/departments/details")
//    @AllArgsConstructor
//    public class DepartmentDetailController {
//
//        private final DepartmentDetailService departmentDetailService;
//
//        @PostMapping("/{departmentId}/employees/{employeeId}")
//        public ResponseEntity<?> addEmployeeToDepartment(@PathVariable("departmentId") Long departmentId,
//                                                         @PathVariable("employeeId") Long employeeId) {
//            DepartmentDetailDTO departmentDetailDTO = departmentDetailService.addEmployeeToDepartment(departmentId, employeeId);
//            return new ResponseEntity<>(departmentDetailDTO, HttpStatus.CREATED);
//        }
//
//        @DeleteMapping("/{departmentId}/employees/{employeeId}")
//        public ResponseEntity<?> removeEmployeeFromDepartment(@PathVariable("departmentId") Long departmentId,
//                                                              @PathVariable("employeeId") Long employeeId) {
//            departmentDetailService.removeEmployeeFromDepartment(departmentId, employeeId);
//            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
//        }
//
//        @GetMapping("/{departmentId}/employees")
//        public ResponseEntity<?> getAllEmployeesInDepartment(@PathVariable("departmentId") Long departmentId) {
//            List<EmployeeDTO> employees = departmentDetailService.getAllEmployeesInDepartment(departmentId);
//            return new ResponseEntity<>(employees, HttpStatus.OK);
//        }
//
//        // Thêm các endpoint khác nếu cần thiết, chẳng hạn như tìm kiếm nhân viên trong phòng ban.
//    }
//
//}
