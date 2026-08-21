package com.suryoday.FdOpening.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.suryoday.FdOpening.Pojo.MerchantTCDetails;
@Repository
public interface MerchantConsentDetailsRepo extends JpaRepository<MerchantTCDetails,Long>{

	@Query(value="Select a from MerchantTCDetails a where a.accountNo=:accountNo order by a.createdDate desc")
	List<MerchantTCDetails> fetchByAccountId(String accountNo);

	@Query(value="Select a from MerchantTCDetails a where a.mobileNo=:mobileNo order by a.createdDate desc")
	List<MerchantTCDetails> fetchByMobNo(String mobileNo);

}
