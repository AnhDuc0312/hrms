//package com.hrms.sys.services;
//
//import com.hrms.sys.dtos.DepartmentDetailDTO;
//import com.hrms.sys.dtos.EmployeeDTO;
//import com.hrms.sys.models.Department;
//import com.hrms.sys.models.DepartmentDetail;
//import com.hrms.sys.models.Employee;
//import com.hrms.sys.repositories.DepartmentDetailRepository;
//import com.hrms.sys.repositories.DepartmentRepository;
//import com.hrms.sys.repositories.EmployeeRepository;
//import lombok.AllArgsConstructor;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//import java.util.stream.Collectors;
//
//@Service
//@AllArgsConstructor
//public class DepartmentDetailService {
//
//    private final DepartmentDetailRepository departmentDetailRepository;
//    private final DepartmentRepository departmentRepository;
//    private final EmployeeRepository employeeRepository;
//
//    public DepartmentDetailDTO addEmployeeToDepartment(Long departmentId, Long employeeId) {
//        Department department = departmentRepository.findById(departmentId)
//                .orElseThrow(() -> new RuntimeException("Department not found"));
//
//        Employee employee = employeeRepository.findById(employeeId)
//                .orElseThrow(() -> new RuntimeException("Employee not found"));
//
//        DepartmentDetail departmentDetail = DepartmentDetail.builder()
//                .department(department)
//                .employee(employee)
//                .build();
//
//        departmentDetailRepository.save(departmentDetail);
//
//        return mapToDto(departmentDetail);
//    }
//
//    public void removeEmployeeFromDepartment(Long departmentId, Long employeeId) {
//        DepartmentDetail departmentDetail = departmentDetailRepository.findByDepartmentIdAndEmployeeId(departmentId, employeeId)
//                .orElseThrow(() -> new RuntimeException("Department detail not found"));
//
//        departmentDetailRepository.delete(departmentDetail);
//    }
//
//    public List<EmployeeDTO> getAllEmployeesInDepartment(Long departmentId) {
//        List<DepartmentDetail> departmentDetails = departmentDetailRepository.findByDepartmentId(departmentId);
//        return departmentDetails.stream()
//                .map(departmentDetail -> mapToEmployeeDto(departmentDetail.getEmployee()))
//                .collect(Collectors.toList());
//    }
//
//    // Thêm các phương thức khác nếu cần thiết, chẳng hạn như tìm kiếm nhân viên trong phòng ban.
//
//    private DepartmentDetailDTO mapToDto(DepartmentDetail departmentDetail) {
//        // Viết phương thức này để chuyển đổi DepartmentDetail thành DepartmentDetailDTO
//    }
//
//    private EmployeeDTO mapToEmployeeDto(Employee employee) {
//        // Viết phương thức này để chuyển đổi Employee thành EmployeeDTO
//    }
//}
//
