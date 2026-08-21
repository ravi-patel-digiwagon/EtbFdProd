package com.suryoday.EtbFdOpening.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Component;

import com.suryoday.EtbFdOpening.Pojo.InterestRates;

@Component
public interface InteresrRatesRepo extends JpaRepository<InterestRates,Integer>{

	@Query(value = "SELECT a from InterestRates a")
	List<InterestRates> fetchInterestRates();

}
