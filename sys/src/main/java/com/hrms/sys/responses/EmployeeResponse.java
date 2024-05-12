package com.hrms.sys.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hrms.sys.models.Employee;
import com.hrms.sys.models.User;
import lombok.*;

import java.time.LocalDate;
@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeResponse {
    @JsonProperty("full_name")
    private String fullName;


    @JsonProperty("gender")
    private String gender;

    //    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonProperty("date_of_birth")
    private LocalDate dateOfBirth;

    @JsonProperty("email")
    private String email;

    @JsonProperty("phone_number")
    private String phoneNumber;

    @JsonProperty("address")
    private String address;

    //    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonProperty("contact_start_date")
    private LocalDate contactStartDate;

    //    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonProperty("contact_end_date")
    private LocalDate contactEndDate;

    @JsonProperty("position")
    private String position;

    @JsonProperty("education")
    private String education;

    @JsonProperty("username")
    private String username;

    @JsonProperty("department")
    private String department;

    @JsonProperty("allowance")
    private Float allowance;

    @JsonProperty("hourly_wage")
    private Float hourlyWage;

    private int totalPages;

    public EmployeeResponse(String fullName, String gender, LocalDate dateOfBirth, String email, String phoneNumber, String address, LocalDate contactStartDate, LocalDate contactEndDate, String position, String education, String username) {
    }


    public static EmployeeResponse fromEmployee(Employee employee) {
        EmployeeResponse employeeResponse = EmployeeResponse.builder()
                .fullName(employee.getFullName())
                .address(employee.getAddress())
                .dateOfBirth(employee.getDateOfBirth())
                .contactStartDate(employee.getContactStartDate())
                .contactEndDate(employee.getContactEndDate())
                .email(employee.getEmail())
                .education(employee.getEducation())
                .gender(employee.getGender())
                .position(employee.getPosition())
                .username(employee.getUser().getUsername())
                .phoneNumber(employee.getPhoneNumber())
                .department(employee.getDepartment().getName())
                .allowance(employee.getBenifit() !=null ? employee.getBenifit().getAllowance() : 0 )
                .hourlyWage(employee.getHourlyWage())
                .build();

        return employeeResponse;
    }
}
