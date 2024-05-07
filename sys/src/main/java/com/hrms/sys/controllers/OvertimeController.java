package com.hrms.sys.controllers;

import com.hrms.sys.dtos.OvertimeDTO;
import com.hrms.sys.models.Overtime;
import com.hrms.sys.services.overtime.OvertimeService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RequestMapping("${api.prefix}/overtimes")
@RestController
public class OvertimeController {
    private final OvertimeService overtimeService;



    @PostMapping("/create")
    public ResponseEntity<?> createOvertime(@RequestBody OvertimeDTO overtimeDTO) {
//        try {
//            Overtime createdOvertime = overtimeService.createOvertime(overtimeDTO);
//            return ResponseEntity.status(HttpStatus.CREATED).body(createdOvertime);
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("");
//        }
        return null;
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getAllOvertimesByUserId(@PathVariable Long userId) {
        try {
            List<Overtime> overtimes = overtimeService.getAllOvertimesByUserId(userId);
            return ResponseEntity.ok(overtimes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<?> getAllOvertimesByEmployeeId(@PathVariable Long employeeId) {
        try {
            List<Overtime> overtimes = overtimeService.getAllOvertimesByEmployeeId(employeeId);
            return ResponseEntity.ok(overtimes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllOvertimes() {
        try {
            List<Overtime> overtimes = overtimeService.getAllOvertimes();
            return ResponseEntity.ok(overtimes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getOvertimeById(@PathVariable Long id) {
        try {
            Overtime overtime = overtimeService.getOvertimeById(id);
            if (overtime != null) {
                return ResponseEntity.ok(overtime);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteOvertime(@PathVariable Long id) {
        try {
            overtimeService.deleteOvertime(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateOvertime(@PathVariable Long id, @RequestBody OvertimeDTO overtimeDTO) {
        try {
            Overtime updatedOvertime = overtimeService.updateOvertime(id, overtimeDTO);
            if (updatedOvertime != null) {
                return ResponseEntity.ok(updatedOvertime);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/approve/{id}")
    public ResponseEntity<?> approveOvertime(@PathVariable Long id) {
        try {
            overtimeService.approveOvertime(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/reject/{id}")
    public ResponseEntity<?> rejectOvertime(@PathVariable Long id) {
        try {
            overtimeService.rejectOvertime(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
