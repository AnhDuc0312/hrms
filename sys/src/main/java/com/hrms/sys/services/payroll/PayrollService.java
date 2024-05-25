package com.hrms.sys.services.payroll;

import com.hrms.sys.dtos.TotalHoursDTO;
import com.hrms.sys.models.Employee;
import com.hrms.sys.models.Payroll;
import com.hrms.sys.repositories.EmployeeRepository;
import com.hrms.sys.repositories.LeaveRepository;
import com.hrms.sys.repositories.PayrollRepository;
import com.hrms.sys.repositories.TimeSheetRepository;
import com.hrms.sys.services.leave.LeaveService;
import com.hrms.sys.services.timesheet.TimeSheetService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;
import java.util.List;

@Service
@AllArgsConstructor
public class PayrollService {
    private final PayrollRepository payrollRepository;
    private final EmployeeRepository employeeRepository;
    private final TimeSheetRepository timeSheetRepository;
    private final TimeSheetService timeSheetService;
    private final LeaveService leaveService;

    public List<Payroll> calculateAllPayrolls() throws Exception {
        List<Payroll> allPayrolls = payrollRepository.findAll();
        LocalDateTime localDateTime = LocalDateTime.now();


        for (Payroll payroll : allPayrolls) {
            Employee employee = employeeRepository.findByPayroll(payroll);
            TotalHoursDTO totalHoursDTO = timeSheetService.getTotalHoursForLastFifteenDays(employee.getUser().getId());
            long count = leaveService.countApprovedLeavesFromPrevious15thToCurrent15th(employee.getUser().getId());

            Float basicSalary = payroll.getBasicSalary();
            Float salary = (float) (totalHoursDTO.getTotalWorkingHours()*(basicSalary/22/8));
            Float overtimeSalary = (float) (totalHoursDTO.getTotalOvertimeHours()*(salary/22/8)*1.5);
            Float benefit = payroll.getBenefit();


            // Tính toán tổng lương
            Float gross = salary + overtimeSalary + benefit;

            double insurance = basicSalary * 0.105; //bảo hiểm
            //tính thuế cá nhân
            double taxDependent = gross - 11000000;
            double tax = 0;
            if (taxDependent > 0) {
                double[] taxRanges = {5000000, 10000000, 18000000, 32000000, 52000000, 80000000};
                double[] percentage = {0.05, 0.1, 0.15, 0.2, 0.25, 0.3, 0.35};
                for (int index = 0; index < taxRanges.length; index++) {
                    if (taxDependent <= taxRanges[index]) {
                        tax = taxDependent * percentage[index];
                        break;
                    }
                }
            }

            float permittedLeave = (float) (count*8) * (basicSalary/22/8);
            float totalSalary = (float) (gross - insurance - tax + permittedLeave);
            payroll.setPermittedLeave(permittedLeave);
            payroll.setDate(localDateTime);
            payroll.setTotalHoursWorked(totalHoursDTO.getTotalWorkingHours());
            payroll.setTotalHoursOvertime(totalHoursDTO.getTotalOvertimeHours());
            payroll.setBasicSalaryReceived(salary);
            payroll.setOvertimeSalary(overtimeSalary);
            payroll.setTotalSalary(totalSalary);
            payroll.setTax((float)tax);
            payroll.setInsurance((float)insurance);
            payroll.setBasicSalaryReceived(salary);
            payroll.setSalaryOvertime(overtimeSalary);
            payrollRepository.save(payroll);
        }


        return allPayrolls;
    }

    //Lấy lương cơ bản * (số giờ làm / số giờ phải làm)
    //Lương overtime : Lấy thời gian overtime * lương overtime (tỉ lệ với lương cơ bản theo giờ)
    //Phúc lợi : Thưởng , trợ cấp ,
    //Thuế :
    //BHXH :
    //Đi làm muộn :


    public static Date getStartDate() {
        LocalDate now = LocalDate.now();
        LocalDate startDate = now.minusMonths(15).with(TemporalAdjusters.firstDayOfMonth());
        return Date.from(startDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    public static Date getEndDate() {
        LocalDate now = LocalDate.now();
        LocalDate endDate = now.withDayOfMonth(15);
        if (now.getDayOfMonth() < 15) {
            endDate = now;
        }
        return Date.from(endDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    public List<Payroll> getAllPayrolls() {
        List<Payroll> payrolls = payrollRepository.findAll();
        return payrolls;
    }

}
