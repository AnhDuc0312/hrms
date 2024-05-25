package com.hrms.sys.repositories;

import com.hrms.sys.models.Benefit;
import com.hrms.sys.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BenefitRepository extends JpaRepository<Benefit, Long> {

}
