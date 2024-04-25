package com.hrms.sys.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "remotes")
@Data
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Remote extends BaseEntity{
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

    @Column(name = "allowed_hours")
    private Float allowedHours;

    @Column(name = "worked_hours")
    private Float workedHours;

    private String reason;

    private String evident;



    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approver_id", referencedColumnName = "id")
    private User approver;

    private String comment;

    private String status;






}
