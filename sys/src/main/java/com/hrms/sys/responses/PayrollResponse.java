package com.hrms.sys.responses;

import com.hrms.sys.models.PayrollHistory;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PayrollResponse {
    private Long id;
    private String username;
    private String fullName;
    private String email;
    private String position ;
    private int year;
    private int month;
    private Float basicSalary;
    private Float basicSalaryReceived;
    private Float overtimeSalary;
    private Float benefit;
    private Float gross;
    private Float insurance;
    private Float tax;
    private Float totalSalary;
    private Float permittedLeave;
    private Float totalHoursWorked;
    private Float totalHoursOvertime;

    // Hàm tạo từ một đối tượng PayrollHistory
    public PayrollResponse(PayrollHistory payrollHistory) {
        this.id = payrollHistory.getEmployee().getId();
        this.username = payrollHistory.getEmployee().getUser().getUsername();
        this.fullName = payrollHistory.getEmployee().getFullName();
        this.email = payrollHistory.getEmployee().getEmail();
        this.position = payrollHistory.getEmployee().getPosition();
        this.year = payrollHistory.getYear();
        this.month = payrollHistory.getMonth();
        this.basicSalary = payrollHistory.getBasicSalary();
        this.basicSalaryReceived = payrollHistory.getBasicSalaryReceived();
        this.overtimeSalary = payrollHistory.getOvertimeSalary();
        this.benefit = payrollHistory.getBenefit();
        this.gross = payrollHistory.getGross();
        this.insurance = payrollHistory.getInsurance();
        this.tax = payrollHistory.getTax();
        this.totalSalary = payrollHistory.getTotalSalary();
        this.permittedLeave = payrollHistory.getPermittedLeave();
        this.totalHoursWorked = payrollHistory.getTotalHoursWorked();
        this.totalHoursOvertime = payrollHistory.getTotalHoursOvertime();
    }
}
