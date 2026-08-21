package com.suryoday.EtbFdOpening.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.suryoday.EtbFdOpening.Pojo.OtpValidation;

public interface OtpValidationRepo extends JpaRepository<OtpValidation,Long>{

	@Query(value = "Select a from OtpValidation a where a.mobileNo=:mobileNo")
	Optional<OtpValidation> fetchOtpData(String mobileNo);

}
