package com.hrms.sys.controllers;

import com.hrms.sys.dtos.OvertimeDTO;
import com.hrms.sys.models.Overtime;
import com.hrms.sys.models.Remote;
import com.hrms.sys.services.overtime.OvertimeService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@AllArgsConstructor
@RequestMapping("${api.prefix}/overtimes")
@RestController
public class OvertimeController {
    private final OvertimeService overtimeService;



    @PostMapping("/create")
    public ResponseEntity<?> createOvertime(@RequestBody OvertimeDTO overtimeDTO) throws Exception {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Object principal = authentication.getPrincipal();
            UserDetails userDetails = (UserDetails) principal;
            String username = userDetails.getUsername();
            Overtime createdOvertime = overtimeService.createOvertime(username, overtimeDTO);
            HttpStatus status = (createdOvertime.getStatus().equals("Approved")) ? HttpStatus.CREATED : HttpStatus.OK;
            return ResponseEntity.status(status).body(createdOvertime);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Đăng kí leave trước 1 ngày"); // Trả về lỗi 400 nếu có ngoại lệ
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getAllOvertimesByUserId(@PathVariable long userId) {
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

    @PostMapping("/approve/{username}")
    public ResponseEntity<?> approveOvertime(@PathVariable String username) {
        try {
            overtimeService.approveOvertime(username);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/reject/{username}")
    public ResponseEntity<?> rejectOvertime(@PathVariable String username) {
        try {
            overtimeService.rejectOvertime(username);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Long>> getOvertimeStatisticsForToday() {
        try {
            Map<String, Long> statistics = overtimeService.getRemoteStatisticsForToday();
            return ResponseEntity.ok(statistics);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }
}
