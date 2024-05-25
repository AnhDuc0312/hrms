package com.hrms.sys.controllers;

import com.hrms.sys.models.Payroll;
import com.hrms.sys.services.payroll.PayrollService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}

