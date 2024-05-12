package com.hrms.sys.models;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;

@Entity
@Table(name = "leaves")
@Data
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Leave extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "from_datetime")
    private LocalDateTime fromDatetime;

    @Column(name = "to_datetime")
    private LocalDateTime toDatetime;

    private String type; //Loại nghỉ  phép : có lương , không lương , chế độ , bào hiểm

    private String reason; //Lý do

    @Column(name = "total_hours")
    private Float totalHours;

    private String evident;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approver_id", referencedColumnName = "id")
    private User approver;

    private String comment;

    private String status;

    @Column(name = "code")
    private String code;

}
