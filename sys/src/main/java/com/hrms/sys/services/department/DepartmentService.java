package com.hrms.sys.services.department;

import com.hrms.sys.dtos.DepartmentDTO;
import com.hrms.sys.models.Department;
import com.hrms.sys.repositories.DepartmentRepository;
import com.hrms.sys.services.department.IDepartmentService;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class DepartmentService implements IDepartmentService {

    private final DepartmentRepository departmentRepository;

    private final ModelMapper modelMapper;

    public DepartmentDTO convertToDto(Department department) {
        return modelMapper.map(department, DepartmentDTO.class);
    }

    public Department convertToEntity(DepartmentDTO departmentDTO) {
        return modelMapper.map(departmentDTO, Department.class);
    }
    @Override
    public List<Department> getAllDepartments() throws Exception {
        return departmentRepository.findAll();
    }

    @Override
    public Department getDepartmentById(long id) throws Exception {
        Optional<Department> departmentOptional = departmentRepository.findById(id);
        return departmentOptional.orElse(null);
    }

    @Override
    public Department createDepartment(DepartmentDTO departmentDTO) throws Exception {
        Department department = convertToEntity(departmentDTO);
        return departmentRepository.save(department);
//        Department department = new Department();
//        department.setName(departmentDTO.getName());
//        department.setDescription(departmentDTO.getDescription());
//        return departmentRepository.save(department);
    }

    @Override
    public Department updateDepartment(long id, DepartmentDTO departmentDTO) throws Exception {
        Department existingDepartment = departmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Department not found"));

        Department updatedDepartment = convertToEntity(departmentDTO);
        updatedDepartment.setId(existingDepartment.getId()); // Đảm bảo rằng ID không thay đổi
        return departmentRepository.save(updatedDepartment);
//        Department department = departmentRepository.findById(id)
//                .orElseThrow(() -> new Exception("Department not found"));
//        department.setName(departmentDTO.getName());
//        department.setDescription(departmentDTO.getDescription());
//        // Cập nhật các trường khác nếu cần
//        return departmentRepository.save(department);
    }

    @Override
    public void deleteDepartment(long id) throws Exception {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new Exception("Department not found"));
        departmentRepository.delete(department);
    }
}
