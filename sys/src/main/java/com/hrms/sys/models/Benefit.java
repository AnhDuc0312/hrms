package com.hrms.sys.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "benefits")
@Data
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Benefit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

//    @OneToOne
//    @JoinColumn
//    private User user;

    // Phụ cấp
    @Column(name = "allowance")
    private Float allowance;

    // Thưởng
    @Column(name = "bonus")
    private Float bonus;

    // Bảo hiểm xã hội
    @Column(name = "social_insurance")
    private Float socialInsurance;

    // Bảo hiểm y tế
    @Column(name = "health_insurance")
    private Float healthInsurance;

    // Bảo hiểm tai nạn lao động
    @Column(name = "work_accident_insurance")
    private Float workAccidentInsurance;

    // Bảo hiểm thai sản
    @Column(name = "maternity_insurance")
    private Float maternityInsurance;

    // Phụ cấp khuyết tật
    @Column(name = "disability_allowance")
    private Float disabilityAllowance;
}
