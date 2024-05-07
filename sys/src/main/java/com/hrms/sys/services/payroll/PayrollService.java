package com.hrms.sys.services.payroll;

import com.hrms.sys.models.Payroll;
import com.hrms.sys.repositories.PayrollRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class PayrollService {
    private PayrollRepository payrollRepository;

    public List<Payroll> calculateAllPayrolls() {
        List<Payroll> allPayrolls = payrollRepository.findAll();

        for (Payroll payroll : allPayrolls) {
            Float basicSalary = payroll.getBasicSalary();
            Float overtimeSalary = payroll.getOvertimeSalary();
            Float benefit = payroll.getBenefit();
            Float lateDeduction = payroll.getLateDeduction();

            // Tính toán tổng lương
            Float totalSalary = basicSalary + overtimeSalary + benefit - lateDeduction;
            payroll.setTotalSalary(totalSalary);
        }

        return allPayrolls;
    }

    //Lấy lương cơ bản * (số giờ làm / số giờ phải làm)
    //Lương overtime : Lấy thời gian overtime * lương overtime (tỉ lệ với lương cơ bản theo giờ)
    //Phúc lợi : Thưởng , trợ cấp ,
    //Thuế :
    //BHXH :
    //Đi làm muộn :

}
