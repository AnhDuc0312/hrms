//package com.hrms.sys.components;
//
//import com.github.javafaker.Faker;
//import com.hrms.sys.models.TimeSheet;
//import com.hrms.sys.models.User;
//import com.hrms.sys.repositories.TimeSheetRepository;
//import com.hrms.sys.repositories.UserRepository;
//import jakarta.annotation.PostConstruct;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//import java.time.*;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.concurrent.ThreadLocalRandom;
//
//@Component
//public class FakeDataGenerator {
//    @Autowired
//    private TimeSheetRepository timeSheetRepository;
//    @Autowired
//    private UserRepository userRepository;
//
//    @PostConstruct
//    public void generateFakeData() {
//        User user = userRepository.findById(11);
//        Faker faker = new Faker();
//        List<TimeSheet> timeSheets = new ArrayList<>();
//
//        LocalDate startDate = LocalDate.now().minusMonths(15).withDayOfMonth(15);
//        LocalDate endDate = LocalDate.now().getDayOfMonth() < 15 ? LocalDate.now() : LocalDate.now().withDayOfMonth(15);
//
//        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
//            // Skip Saturdays and Sundays
//            if (date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY) {
//                continue;
//            }
//
//            // Random check-in between 07:50 and 08:30
//            LocalDateTime checkIn = date.atTime(randomTimeBetween(7, 30, 8, 30));
//            // Random check-out between 17:30 and 18:00
//            LocalDateTime checkOut = date.atTime(randomTimeBetween(17, 0, 18, 0));
//
//            TimeSheet timeSheet = TimeSheet.builder()
//                    .inTime(checkIn)
//                    .outTime(checkOut)
//                    .status("Completed")
//                    .workingHours(0) // This will be updated later
//                    .overtimeHours(0) // This will be updated later
//                    .leaveHours(0)
//                    .user(user)  // Assuming you have a User entity with ID
//                    .recordDate(date)
//                    .checkIn(checkIn)
//                    .checkOut(checkOut)
//                    .typeWork(faker.options().option("Remote", "Onsite", "Overtime"))
//                    .code(date.toString() + user.getUsername())
//                    .build();
//
//            // Calculate working hours based on the type of work
//            float calculatedHours = calculateWorkHours(timeSheet);
//            if (timeSheet.getTypeWork().equals("Remote") || timeSheet.getTypeWork().equals("Onsite")) {
//                timeSheet.setWorkingHours(calculatedHours);
//            } else if (timeSheet.getTypeWork().equals("Overtime")) {
//                timeSheet.setOvertimeHours(calculatedHours);
//            } else {
//                timeSheet.setWorkingHours(calculatedHours);
//            }
//
//            timeSheets.add(timeSheet);
//        }
//
//        timeSheetRepository.saveAll(timeSheets);
//    }
//
//    private float calculateWorkHours(TimeSheet timeSheet) {
//        Duration duration = Duration.between(timeSheet.getCheckIn(), timeSheet.getCheckOut());
//        long hours = duration.toHours();
//        long minutes = duration.toMinutes() % 60;
//
//        float totalHours = hours + minutes / 60.0f;
//
//        if (totalHours >= 8) {
//            totalHours = 8;
//        }
//
//        return totalHours;
//    }
//
//    private LocalTime randomTimeBetween(int startHour, int startMinute, int endHour, int endMinute) {
//        int start = startHour * 60 + startMinute;
//        int end = endHour * 60 + endMinute;
//        int randomMinute = ThreadLocalRandom.current().nextInt(start, end);
//        return LocalTime.of(randomMinute / 60, randomMinute % 60);
//    }
//}
