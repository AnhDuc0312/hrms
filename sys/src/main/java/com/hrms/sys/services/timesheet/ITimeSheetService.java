package com.hrms.sys.services.timesheet;

import com.hrms.sys.dtos.TimeSheetDTO;
import com.hrms.sys.exceptions.NotFoundException;
import com.hrms.sys.models.TimeSheet;

import java.time.LocalDate;
import java.util.List;

public interface ITimeSheetService {

    TimeSheet saveOrUpdateTimeSheet(TimeSheet timeSheet);

    void checkIn(TimeSheetDTO timeSheetDTO) throws NotFoundException;

    void checkOut(TimeSheetDTO timeSheetDTO) throws NotFoundException;

    void updateTimeSheet(Long id, TimeSheetDTO timeSheetDTO) throws NotFoundException;

    List<TimeSheetDTO> getAllTimeSheetsSortedByDateTime();

    List<TimeSheetDTO> getTimeSheetsByDateAndSortByCheckIn(LocalDate date);

    List<TimeSheetDTO> getTimeSheetsByUserIdAndSortByDate(Long userId);


}
