package com.hrms.sys.controllers;

import com.hrms.sys.dtos.LeaveDTO;
import com.hrms.sys.models.Leave;
import com.hrms.sys.models.User;
import com.hrms.sys.repositories.UserRepository;
import com.hrms.sys.services.leave.LeaveService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@RestController
@RequestMapping("${api.prefix}/leaves")
public class LeaveController {
    private final LeaveService leaveService;
    private final UserRepository userRepository;



    @PostMapping("/create")
    public ResponseEntity<?> createLeave(@RequestBody LeaveDTO leaveDTO) throws Exception {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Object principal = authentication.getPrincipal();
            UserDetails userDetails = (UserDetails) principal;
            String username = userDetails.getUsername();
            Leave createdLeave = leaveService.createLeave(username, leaveDTO);
            HttpStatus status = (createdLeave.getStatus().equals("Approved")) ? HttpStatus.CREATED : HttpStatus.OK;
            return ResponseEntity.status(status).body("Successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Đăng kí leave trước 1 ngày"); // Trả về lỗi 400 nếu có ngoại lệ
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<Leave>> getAllLeaves() throws Exception {
        List<Leave> leaves = leaveService.getAllLeaves();
        return ResponseEntity.ok(leaves);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Leave> getLeaveById(@PathVariable Long id) throws Exception {
        Leave leave = leaveService.getLeaveById(id);
        if (leave != null) {
            return ResponseEntity.ok(leave);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    @GetMapping("/my")
    public ResponseEntity <List<Leave>> getLeaveByUserId() throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();
        UserDetails userDetails = (UserDetails) principal;
        String username = userDetails.getUsername();
        Optional<User> user = userRepository.findByUsername(username);
        Long id = user.get().getId();
        List<Leave> leaves = leaveService.getLeaveByUserId(id);
        if (leaves != null) {
            return ResponseEntity.ok(leaves);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteLeave(@PathVariable Long id) throws Exception {
        leaveService.deleteLeave(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/approve/{id}")
    public ResponseEntity<Leave> approveLeave(@PathVariable Long id) throws Exception {
        Leave approvedLeave = leaveService.approveLeave(id);
        if (approvedLeave != null) {
            return ResponseEntity.ok(approvedLeave);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/reject/{id}")
    public ResponseEntity<Leave> rejectLeave(@PathVariable Long id) throws Exception {
        Leave rejectedLeave = leaveService.rejectLeave(id);
        if (rejectedLeave != null) {
            return ResponseEntity.ok(rejectedLeave);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    @GetMapping("/by-date")
    public ResponseEntity<List<Leave>> getLeavesByDate() throws Exception {
        List<Leave> leaves = leaveService.getLeavesByDate();
        return ResponseEntity.ok(leaves);
    }
}
