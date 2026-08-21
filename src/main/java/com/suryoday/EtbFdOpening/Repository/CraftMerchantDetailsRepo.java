package com.suryoday.EtbFdOpening.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.suryoday.EtbFdOpening.Pojo.CraftMerchantDetails;

@Repository
public interface CraftMerchantDetailsRepo extends JpaRepository<CraftMerchantDetails, String> {
	Optional<CraftMerchantDetails> findByDocumentId(String documentId);

	@Procedure("usp_GetReferralSummaryByMobile")
	List<Object[]> getDashboard(@Param("MobileNo") String MobileNo);
	
	@Query("Select a from CraftMerchantDetails a where a.createdDate between :s and :e")
	List<CraftMerchantDetails> findByDate(@Param("s") LocalDateTime startDate,@Param("e") LocalDateTime endDate);
	
	@Query("Select a from CraftMerchantDetails a where a.isQrGenerated='Y'")
	List<CraftMerchantDetails> findAllData();

}
