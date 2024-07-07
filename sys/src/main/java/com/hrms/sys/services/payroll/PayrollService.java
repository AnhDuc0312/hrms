package com.hrms.sys.services.payroll;

import com.hrms.sys.dtos.TotalHoursDTO;
import com.hrms.sys.models.Employee;
import com.hrms.sys.models.Payroll;
import com.hrms.sys.models.PayrollHistory;
import com.hrms.sys.repositories.*;
import com.hrms.sys.responses.PayrollResponse;
import com.hrms.sys.services.leave.LeaveService;
import com.hrms.sys.services.timesheet.TimeSheetService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class PayrollService {
    private final PayrollRepository payrollRepository;
    private final EmployeeRepository employeeRepository;
    private final TimeSheetRepository timeSheetRepository;
    private final TimeSheetService timeSheetService;
    private final LeaveService leaveService;
    private final PayrollHistoryRepository payrollHistoryRepository;

    public List<Payroll> calculateAllPayrolls() throws Exception {
        List<Payroll> allPayrolls = payrollRepository.findAll();
        LocalDateTime localDateTime = LocalDateTime.now();

        // Lấy tháng và năm hiện tại
        LocalDate now = LocalDate.now();
        int year = now.getYear();
        int month = now.getMonthValue() - 1;

        // Tính số ngày trong tháng và số ngày làm việc (trừ đi 8 ngày nghỉ)
        YearMonth yearMonth = YearMonth.of(year, month);
        int daysInMonth = yearMonth.lengthOfMonth();
        int workingDays = daysInMonth - 8;


        for (Payroll payroll : allPayrolls) {
            Employee employee = employeeRepository.findByPayroll(payroll);
            TotalHoursDTO totalHoursDTO = timeSheetService.getTotalHoursForMonth(employee.getUser().getId());
            long count = leaveService.countApprovedLeavesFromStartOfMonthToEndOfMonth(employee.getUser().getId());

            Float basicSalary = payroll.getBasicSalary();
            Float salary = (float) (totalHoursDTO.getTotalWorkingHours()*(basicSalary/workingDays/8));
            Float overtimeSalary = (float) (totalHoursDTO.getTotalOvertimeHours()*(basicSalary/workingDays/8)*1.5);
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
            float totalSalary = (float) (overtimeSalary + salary - insurance - tax + permittedLeave);
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

            // Lưu thông tin lương vào bảng PayrollHistory
//            savePayrollHistory(employee, year, month, salary, overtimeSalary, benefit, gross, insurance, tax, totalSalary, permittedLeave, totalHoursDTO.getTotalWorkingHours(), totalHoursDTO.getTotalOvertimeHours(), localDateTime);
            saveOrUpdatePayrollHistory(employee, year, month, salary, overtimeSalary, benefit, gross, insurance, tax, totalSalary, permittedLeave, totalHoursDTO.getTotalWorkingHours(), totalHoursDTO.getTotalOvertimeHours(), localDateTime, basicSalary);
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

    private void savePayrollHistory(Employee employee, int year, int month, float salary, float overtimeSalary, float benefit, float gross, double insurance, double tax, float totalSalary, float permittedLeave, float totalHoursWorked, float totalHoursOvertime, LocalDateTime date) {
        PayrollHistory payrollHistory = new PayrollHistory();
        payrollHistory.setEmployee(employee);
        payrollHistory.setYear(year);
        payrollHistory.setMonth(month);
        payrollHistory.setBasicSalaryReceived(salary);
        payrollHistory.setOvertimeSalary(overtimeSalary);
        payrollHistory.setBenefit(benefit);
        payrollHistory.setGross(gross);
        payrollHistory.setInsurance((float) insurance);
        payrollHistory.setTax((float) tax);
        payrollHistory.setTotalSalary(totalSalary);
        payrollHistory.setPermittedLeave(permittedLeave);
        payrollHistory.setTotalHoursWorked(totalHoursWorked);
        payrollHistory.setTotalHoursOvertime(totalHoursOvertime);
        payrollHistory.setDate(date);
        payrollHistoryRepository.save(payrollHistory);
    }

    private void saveOrUpdatePayrollHistory(Employee employee, int year, int month, float salary, float overtimeSalary, float benefit, float gross, double insurance, double tax, float totalSalary, float permittedLeave, float totalHoursWorked, float totalHoursOvertime, LocalDateTime date, float basicSalary) {
        Optional<PayrollHistory> existingPayrollHistory = payrollHistoryRepository.findByEmployeeAndYearAndMonth(employee, year, month);
        if (existingPayrollHistory.isPresent()) {
            // Cập nhật bản ghi lịch sử lương nếu đã tồn tại
            PayrollHistory payrollHistory = existingPayrollHistory.get();
            payrollHistory.setBasicSalaryReceived(salary);
            payrollHistory.setOvertimeSalary(overtimeSalary);
            payrollHistory.setBenefit(benefit);
            payrollHistory.setGross(gross);
            payrollHistory.setInsurance((float) insurance);
            payrollHistory.setTax((float) tax);
            payrollHistory.setTotalSalary(totalSalary);
            payrollHistory.setPermittedLeave(permittedLeave);
            payrollHistory.setTotalHoursWorked(totalHoursWorked);
            payrollHistory.setTotalHoursOvertime(totalHoursOvertime);
            payrollHistory.setDate(date);
            payrollHistory.setBasicSalary(basicSalary);
            payrollHistoryRepository.save(payrollHistory);
        } else {
            // Tạo mới bản ghi lịch sử lương nếu chưa tồn tại
            PayrollHistory payrollHistory = new PayrollHistory();
            payrollHistory.setEmployee(employee);
            payrollHistory.setYear(year);
            payrollHistory.setMonth(month);
            payrollHistory.setBasicSalaryReceived(salary);
            payrollHistory.setOvertimeSalary(overtimeSalary);
            payrollHistory.setBenefit(benefit);
            payrollHistory.setGross(gross);
            payrollHistory.setInsurance((float) insurance);
            payrollHistory.setTax((float) tax);
            payrollHistory.setTotalSalary(totalSalary);
            payrollHistory.setPermittedLeave(permittedLeave);
            payrollHistory.setTotalHoursWorked(totalHoursWorked);
            payrollHistory.setTotalHoursOvertime(totalHoursOvertime);
            payrollHistory.setDate(date);
            payrollHistory.setBasicSalary(basicSalary);
            payrollHistoryRepository.save(payrollHistory);
        }

    }

    public List<PayrollResponse> getPayrollsByMonthAndYear(int year, int month) {
//        return payrollHistoryRepository.findByYearAndMonth(year, month);
        List<PayrollHistory> payrollHistories = payrollHistoryRepository.findByYearAndMonth(year, month);
        List<PayrollResponse> payrollResponses = new ArrayList<>();

        for (PayrollHistory payrollHistory : payrollHistories) {
            PayrollResponse payrollResponse = new PayrollResponse(payrollHistory);
            payrollResponses.add(payrollResponse);
        }
        return payrollResponses;
    }



    public List<Payroll> calculatePayrollsForMonthAndYear(int year, int month) throws Exception {
        List<Employee> employees = employeeRepository.findAll(); // Lấy tất cả nhân viên

        for (Employee employee : employees) {
            // Tính toán bảng lương cho từng nhân viên
            calculatePayrollForEmployee(employee, year, month);
        }

        return payrollRepository.findAll();
    }

    private void calculatePayrollForEmployee(Employee employee, int year, int month) throws Exception {
        // Lấy thông tin về tổng số giờ làm việc và số ngày nghỉ có phép trong tháng và năm cụ thể
        TotalHoursDTO totalHoursDTO = timeSheetService.getTotalHoursForMonthAndYear(employee.getUser().getId(), year, month);
        long countApprovedLeaves = leaveService.countApprovedLeavesForMonthAndYear(employee.getUser().getId(), year, month);

        // Tính lương
        Float basicSalary = employee.getPayroll().getBasicSalary();
        int workingDaysInMonth = calculateWorkingDays(year, month);
        Float salary = (float) (totalHoursDTO.getTotalWorkingHours() * (basicSalary / workingDaysInMonth / 8));
        Float overtimeSalary = (float) (totalHoursDTO.getTotalOvertimeHours() * (salary / workingDaysInMonth / 8) * 1.5);
        Float benefit = employee.getPayroll().getBenefit();
        Float grossSalary = salary + overtimeSalary + benefit;
        double insurance = basicSalary * 0.105;
        double tax = calculateTax(grossSalary);
        float permittedLeaveSalary = (float) (countApprovedLeaves * 8) * (basicSalary / 22 / 8);
        Float totalSalary = grossSalary - (float) insurance - (float) tax + permittedLeaveSalary;

        // Lưu thông tin vào bảng lương
//        Payroll payroll = new Payroll();
//        payroll.setEmployee(employee);
//        payroll.setYear(year);
//        payroll.setMonth(month);
//        payroll.setBasicSalary(basicSalary);
//        payroll.setBasicSalaryReceived(salary);
//        payroll.setOvertimeSalary(overtimeSalary);
//        payroll.setBenefit(benefit);
//        payroll.setTotalSalary(totalSalary);
//        payroll.setTax((float) tax);
//        payroll.setInsurance((float) insurance);
//        payroll.setPermittedLeave(permittedLeaveSalary);
//        // Lưu bảng lương
//        payrollRepository.save(payroll);

        // Lưu thông tin lương vào bảng PayrollHistory
        saveOrUpdatePayrollHistory(employee, year, month, salary, overtimeSalary, benefit, grossSalary, insurance, tax, totalSalary, permittedLeaveSalary, totalHoursDTO.getTotalWorkingHours(), totalHoursDTO.getTotalOvertimeHours(), LocalDateTime.now(), basicSalary);
    }


    private int calculateWorkingDays(int year, int month) {
        YearMonth yearMonth = YearMonth.of(year, month);
        int daysInMonth = yearMonth.lengthOfMonth();
        return daysInMonth - 8; // Giả sử mỗi tháng có 8 ngày nghỉ
    }

    private double calculateTax(float grossSalary) {
        double taxDependent = grossSalary - 11000000;
        if (taxDependent <= 0) {
            return 0;
        }
        double[] taxRanges = {5000000, 10000000, 18000000, 32000000, 52000000, 80000000};
        double[] percentage = {0.05, 0.1, 0.15, 0.2, 0.25, 0.3, 0.35};
        for (int index = 0; index < taxRanges.length; index++) {
            if (taxDependent <= taxRanges[index]) {
                return taxDependent * percentage[index];
            }
        }
        return 0; // Nếu lương vượt quá mức thuế tối đa
    }

    public void calculatePayrollForAll(int year, int month) throws Exception {
        // Retrieve all payrolls
        List<Payroll> allPayrolls = payrollRepository.findAll();

        // Iterate over each payroll
        for (Payroll payroll : allPayrolls) {
            // Retrieve all employees associated with the current payroll
            Employee employee = employeeRepository.findByPayroll(payroll);
                // Calculate payroll for the employee
                calculatePayrollForEmployee(employee, year, month);

        }
    }

}
