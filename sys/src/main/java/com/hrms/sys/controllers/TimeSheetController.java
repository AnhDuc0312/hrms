package com.hrms.sys.controllers;

import com.hrms.sys.dtos.DailyWorkDetailDTO;
import com.hrms.sys.dtos.MonthSummaryDTO;
import com.hrms.sys.dtos.MonthlyWorkSummaryDTO;
import com.hrms.sys.dtos.TimeSheetDTO;
import com.hrms.sys.exceptions.NotFoundException;
import com.hrms.sys.services.timesheet.TimeSheetService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;

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
//        if (!isCompanyNetworkIp(clientIp)) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized access");
//        }

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

    @GetMapping("/monthly-summary/{year}")
    public ResponseEntity<List<MonthlyWorkSummaryDTO>> getMonthlyWorkSummaryForAllEmployeesByYear(@PathVariable int year) {
        try {
            List<MonthlyWorkSummaryDTO> monthlySummaries = timeSheetService.getMonthlyWorkSummaryForAllEmployeesByYear(year);
            return ResponseEntity.ok(monthlySummaries);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    @GetMapping("/monthly-summary/{year}/{username}")
    public ResponseEntity<List<DailyWorkDetailDTO>> getMonthlyWorkSummaryByYear(@PathVariable int year, @PathVariable String username) {
        try {
            List<DailyWorkDetailDTO> monthlySummaries = timeSheetService.getMonthlyWorkSummaryByYear(year, username);
            if (monthlySummaries != null && !monthlySummaries.isEmpty()) {
                return ResponseEntity.ok(monthlySummaries);
            } else {
                return ResponseEntity.notFound().build(); // Hoặc mã trạng thái NOT_FOUND nếu không tìm thấy dữ liệu
            }
        } catch (Exception e) {
            // Log lỗi hoặc xử lý ngoại lệ ở đây
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null); // Thay vì trả về null, có thể trả về một message hoặc object khác để mô tả lỗi
        }
    }


    @GetMapping("/day-of-week")
    public ResponseEntity<List<Map<String, Object>>> getAttendanceCountByDayOfWeek() {
        List<Map<String, Object>> attendanceMap = timeSheetService.getAttendanceCountByCurrentWeek();
        return new ResponseEntity<>(attendanceMap, HttpStatus.OK);
    }

    @GetMapping("/month")
    public ResponseEntity<List<Map<String, Object>>> getAttendanceCountByMonth(
            @RequestParam("year") int year,
            @RequestParam("month") int month) {
        List<Map<String, Object>> attendanceMap = timeSheetService.getAttendanceCountByMonth(year, month);
        return new ResponseEntity<>(attendanceMap, HttpStatus.OK);
    }

    @GetMapping("/exportExcel")
    public void exportExcel(HttpServletResponse response) throws IOException {

        // Tạo workbook và thêm dữ liệu vào đây sử dụng Apache POI
        List<TimeSheetDTO> timeSheets = timeSheetService.getAllTimeSheetsSortedByDateTime();

        // Định nghĩa một danh sách mới để lưu trữ TimeSheetDTO của tháng đầu vào
        LocalDate currentDate = LocalDate.now();
        int currentMonth = currentDate.getMonthValue();
        int currentYear = currentDate.getYear();

// Định nghĩa một danh sách mới để lưu trữ TimeSheetDTO của tháng đầu vào
        List<TimeSheetDTO> timeSheetsInFirstMonth = new ArrayList<>();

// Lặp qua danh sách timeSheets và lọc theo tháng đầu vào
        for (TimeSheetDTO timeSheet : timeSheets) {
            LocalDate timeSheetDate = timeSheet.getRecordDate();
            int timeSheetMonth = timeSheetDate.getMonthValue();
            int timeSheetYear = timeSheetDate.getYear();

            if (timeSheetMonth == currentMonth && timeSheetYear == currentYear) {
                timeSheetsInFirstMonth.add(timeSheet);
            }
        }
        timeSheetsInFirstMonth.sort(new TimeSheetComparator());

        XSSFWorkbook workbook = new XSSFWorkbook();

        XSSFSheet sheet = workbook.createSheet();

        CellStyle titleStyle = workbook.createCellStyle();
        Font titleFont = workbook.createFont();
        titleFont.setFontHeightInPoints((short) 16);
        titleFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
        titleStyle.setFont(titleFont);
        titleStyle.setAlignment(CellStyle.ALIGN_CENTER);
        titleStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);

        String title = "Bảng chấm công tháng " + currentMonth;
        Row titleRow = sheet.createRow(0);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue(title);
        titleCell.setCellStyle(titleStyle);

        sheet.addMergedRegion(new CellRangeAddress(0, 1, 0, 12 ));

        for (int i = 0; i < 12; i++) {
            sheet.setColumnWidth(i, 20*256);
        }

        int RowIndex = 2;
        Row headerRow = sheet.createRow(RowIndex++);

        headerRow.createCell(1).setCellValue("USER ID");
        headerRow.createCell(2).setCellValue("Họ và tên");
        headerRow.createCell(3).setCellValue("Ngày");
        headerRow.createCell(4).setCellValue("Check in");
        headerRow.createCell(5).setCellValue("Check out");
        headerRow.createCell(6).setCellValue("In time");
        headerRow.createCell(7).setCellValue("Out time");
        headerRow.createCell(8).setCellValue("Loại");
        headerRow.createCell(9).setCellValue("Code");
        headerRow.createCell(10).setCellValue("Thời gian làm việc");
        headerRow.createCell(11).setCellValue("Status");

        for (TimeSheetDTO timeSheet : timeSheetsInFirstMonth) {
            Row row = sheet.createRow(RowIndex++);
            row.createCell(1).setCellValue(timeSheet.getUserId());
            row.createCell(2).setCellValue(timeSheet.getFullName());
            row.createCell(3).setCellValue(String.valueOf(timeSheet.getRecordDate()));
            row.createCell(4).setCellValue(String.valueOf(timeSheet.getCheckIn()));
            row.createCell(5).setCellValue(String.valueOf(timeSheet.getCheckOut()));
            row.createCell(6).setCellValue(String.valueOf(timeSheet.getInTime()));
            row.createCell(7).setCellValue(String.valueOf(timeSheet.getOutTime()));
            row.createCell(8).setCellValue(timeSheet.getTypeWork());
            row.createCell(9).setCellValue(timeSheet.getCode());
            row.createCell(10).setCellValue(timeSheet.getWorkingHours() != 0 ? timeSheet.getWorkingHours() : timeSheet.getOvertimeHours());
            row.createCell(11).setCellValue(timeSheet.getStatus());
//            row.createCell(1).setCellValue(timeSheet.getUserId());
        }



        // Xác định loại file và thiết lập header
        response.setContentType("application/vnd.ms-excel");
//        response.setHeader("Content-Disposition", "attachment; filename=example.xlsx");

        // Xuất workbook ra OutputStream của phản hồi HTTP
        workbook.write(response.getOutputStream());

    }

    class TimeSheetComparator implements Comparator<TimeSheetDTO> {
        public int compare(TimeSheetDTO a, TimeSheetDTO b) {
            return Integer.compare(Math.toIntExact(a.getUserId()), Math.toIntExact(b.getUserId()));
        }
    }

    @PutMapping("/update/{code}")
    public ResponseEntity<String> updateCheckInOutTime(@PathVariable String code, @RequestBody TimeSheetDTO timeSheetDTO) {
        try {
            timeSheetService.updateCheckInOutTime(code, timeSheetDTO);
            return ResponseEntity.ok("{\"message\": \"Updated check-in/out time successfully\"}");
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
