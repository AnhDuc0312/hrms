package com.hrms.sys.controllers;

import com.hrms.sys.models.Payroll;
import com.hrms.sys.models.PayrollHistory;
import com.hrms.sys.responses.PayrollResponse;
import com.hrms.sys.services.payroll.PayrollService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/payroll")
@AllArgsConstructor
public class PayrollController {

    private final PayrollService payrollService;

    @GetMapping("/calculate")
    public ResponseEntity<List<Payroll>> calculateAllPayrolls() throws Exception {
        List<Payroll> allPayrolls = payrollService.calculateAllPayrolls();
        return ResponseEntity.ok(allPayrolls);
    }

    @GetMapping("/all")
    public ResponseEntity<List<Payroll>> getAll() {
        List<Payroll> payrolls = payrollService.getAllPayrolls();
        return ResponseEntity.ok(payrolls);
    }

    @GetMapping("/payrolls")
    public List<PayrollResponse> getPayrollsByMonthAndYear(@RequestParam int year, @RequestParam int month) {
        return payrollService.getPayrollsByMonthAndYear(year, month);
    }

    @PostMapping("/calculatePayrollForAll")
    public String calculatePayrollForAllEmployees(@RequestParam int year, @RequestParam int month) {
        try {
            payrollService.calculatePayrollForAll(year, month);
            return "Payroll calculation for all employees successful for " + year + "/" + month;
        } catch (Exception e) {
            return "Error calculating payroll for all employees: " + e.getMessage();
        }
    }
}

