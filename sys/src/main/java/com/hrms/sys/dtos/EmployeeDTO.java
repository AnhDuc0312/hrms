package com.hrms.sys.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.Date;

@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeDTO {
    @JsonProperty("full_name")
    private String fullName;


    private String gender;

//    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonProperty("date_of_birth")
    private LocalDate dateOfBirth;

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

    private String position;

    private String education;

    private String department;

    private Float allowance;


}
