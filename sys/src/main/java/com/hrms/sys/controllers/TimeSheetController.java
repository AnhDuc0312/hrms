package com.hrms.sys.controllers;

import com.hrms.sys.dtos.MonthSummaryDTO;
import com.hrms.sys.dtos.TimeSheetDTO;
import com.hrms.sys.exceptions.NotFoundException;
import com.hrms.sys.services.timesheet.TimeSheetService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("${api.prefix}/timesheets")
@RequiredArgsConstructor
public class TimeSheetController {

    private final TimeSheetService timeSheetService;
    @PostMapping("/check-in")
    public ResponseEntity<?> checkIn(@RequestBody TimeSheetDTO timeSheetDTO, HttpServletRequest request) {
        // Lấy địa chỉ IP của người dùng
//        String clientIp = request.getRemoteAddr();

        // Kiểm tra xem địa chỉ IP có thuộc mạng công ty không (thay bằng logic của bạn)
//        if (!isCompanyNetworkIp(clientIp)) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized access");
//        }

        // Tiếp tục xử lý chấm công nếu địa chỉ IP hợp lệ
        try {
            timeSheetService.checkIn(timeSheetDTO);
            String jsonResponse = "{\"message\": \"Check-in successful\"}";
            return new ResponseEntity<>(jsonResponse, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/check-out")
    public ResponseEntity<String> checkOut(@RequestBody TimeSheetDTO timeSheetDTO, HttpServletRequest request) {
        // Lấy địa chỉ IP của người dùng
//        String clientIp = request.getRemoteAddr();

        // Kiểm tra xem địa chỉ IP có thuộc mạng công ty không (thay bằng logic của bạn)
//        if (!isCompanyNetworkIp(clientIp)) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized access");
//        }

        // Tiếp tục xử lý chấm công nếu địa chỉ IP hợp lệ
        try {
            timeSheetService.checkOut(timeSheetDTO);
            String jsonResponse = "{\"message\": \"Check-out successful\"}";
            return new ResponseEntity<>(jsonResponse, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

    @PutMapping("/update-time-sheet/{id}")
    public ResponseEntity<String> updateTimeSheet(@PathVariable Long id, @RequestBody TimeSheetDTO timeSheetDTO, HttpServletRequest request) {
        // Lấy địa chỉ IP của người dùng
        String clientIp = request.getRemoteAddr();

        // Kiểm tra xem địa chỉ IP có thuộc mạng công ty không (thay bằng logic của bạn)
        if (!isCompanyNetworkIp(clientIp)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized access");
        }

        // Tiếp tục xử lý cập nhật thông tin chấm công nếu địa chỉ IP hợp lệ
        try {
            timeSheetService.updateTimeSheet(id, timeSheetDTO);
            return ResponseEntity.ok("Time sheet updated successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

    // Hàm kiểm tra địa chỉ IP có thuộc mạng công ty hay không
    private boolean isCompanyNetworkIp(String ip) {
        // Thực hiện logic kiểm tra địa chỉ IP có thuộc mạng công ty
        // Trả về true nếu hợp lệ, ngược lại trả về false
        return ip.startsWith("192.168"); // Ví dụ: IP mạng công ty bắt đầu bằng "192.168"
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<TimeSheetDTO>> getTimeSheetsByUserId(@PathVariable Long userId) {
        try {
            List<TimeSheetDTO> timeSheets = timeSheetService.getTimeSheetsByUserIdAndSortByDate(userId);
            return ResponseEntity.ok(timeSheets);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/date/{date}")
    public ResponseEntity<List<TimeSheetDTO>> getTimeSheetsByDate(@PathVariable LocalDate date) {
        try {
            List<TimeSheetDTO> timeSheets = timeSheetService.getTimeSheetsByDateAndSortByCheckIn(date);
            return ResponseEntity.ok(timeSheets);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<TimeSheetDTO>> getAllTimeSheets() {
        try {
            List<TimeSheetDTO> timeSheets = timeSheetService.getAllTimeSheetsSortedByDateTime();
            return ResponseEntity.ok(timeSheets);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/checked-in-count")
    public long getCheckedInCount(@RequestParam LocalDate date) {
        return timeSheetService.getCheckedInCountByDate(date);
    }

    @GetMapping("/workdays")
    public Map<Long, Long> getWorkDays(@RequestParam LocalDate startDate, @RequestParam LocalDate endDate) {
        return timeSheetService.getWorkDaysForEachEmployee(startDate, endDate);
    }

    @GetMapping("/leave-days")
    public Map<Long, Long> getLeaveDays(@RequestParam LocalDate startDate, @RequestParam LocalDate endDate) {
        return timeSheetService.getLeaveDaysForEachEmployee(startDate, endDate);
    }

    @GetMapping("/user/{userId}/monthly-summary")
    public List<MonthSummaryDTO> getMonthlyWorkSummaryByUserId(@PathVariable Long userId) throws NotFoundException {
        return timeSheetService.getMonthlyWorkSummaryByUserIdAndYear(userId, 2024);
    }


}
