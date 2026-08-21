package com.suryoday.FdOpening.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;

import com.suryoday.FdOpening.Pojo.FdOpeningNTB;

@Component
public interface NtbFdRepo extends JpaRepository<FdOpeningNTB, Long> {

	@Query(value = "Select a from FdOpeningNTB a where a.mobileNo=:mobileNo and a.status=:status order by a.createdDate desc")
	List<FdOpeningNTB> fetchByMobileNo(String mobileNo, String status);

	@Query(value = "SELECT top 1 a.application_no from tbl_fd_opening_ntb a order by a.application_no desc", nativeQuery = true)
	Optional<String> fetchLastApplicationNo();

	@Query(value = "Select a from FdOpeningNTB a where a.applicationNo=:applicationNo")
	Optional<FdOpeningNTB> fetchByApplicationNo(long applicationNo);

	@Query(value = "Select COUNT(*) from FdOpeningNTB a where a.mobileNo=:mobileNo and a.status=:status")
	int count(String mobileNo, String status);

//	@Query(value = "Select a.vkycTrackingId from FdOpeningNTB a where a.isActive=:isActive")
	@Query(value = "Select vkyc_tracking_id from tbl_fd_opening_ntb where vkyc_tracking_id is not null and (is_fd_created is null or is_fd_created='N') and is_active=:isActive", nativeQuery = true)
	List<String> getAllTrackingIds(String isActive);

	@Query(value = "Select a from FdOpeningNTB a where a.vkycTrackingId=:trackingId")
	Optional<FdOpeningNTB> fetchByTrackingId(String trackingId);

	// List<FdOpeningNTB>
	// findByIsActiveAndIsPartialVkycAndIsFdCreatedOrIsFdCreatedIsNull(String
	// isActive,String isPartialVkyc,String isFdCreated);

	@Query("SELECT f FROM FdOpeningNTB f WHERE f.isActive = :isActive AND f.isPartialVkyc = :isPartialVkyc AND (f.isFdCreated = :isFdCreated OR f.isFdCreated IS NULL)")
	List<FdOpeningNTB> findActivePartialVkycWhereFdCreatedOrNull(@Param("isActive") String isActive,@Param("isPartialVkyc") String isPartialVkyc, @Param("isFdCreated") String isFdCreated);

	@Query("SELECT f FROM FdOpeningNTB f WHERE f.isActive = :isActive AND f.isDmsUpload = :isDmsUpload AND f.isCifCreated = :isCifCreated AND f.isEkyc = :isEkyc")
	List<FdOpeningNTB> findActiveDmsCifEkyc(@Param("isActive") String isActive,@Param("isDmsUpload") String isDmsUpload, @Param("isCifCreated") String isCifCreated,@Param("isEkyc") String isEkyc);

	// Refund NTB FD List where isActive = Y and isRefundDone = N and isPaymentDone = Y
	@Query("SELECT f FROM FdOpeningNTB f WHERE f.isActive = :isActive AND f.isRefundDone = :isRefundDone  AND f.isPaymentDone = :isPaymentDone AND f.isRefundProcessedApproved = :isRefundProcessedApproved")
	List<FdOpeningNTB> findRefundNtbFdList(@Param("isActive") String isActive,@Param("isRefundDone") String isRefundDone,@Param("isPaymentDone") String isPaymentDone,@Param("isRefundProcessedApproved") String isRefundProcessedApproved);
	
//	@Query(value = "SELECT f FROM FdOpeningNTB f WHERE f.isFdCreated = :isFdCreated")
//	List<FdOpeningNTB> getAllCreatedFd(@Param("isFdCreated") String isFdCreated);
	
	
	@Query(value = "select a from FdOpeningNTB a where a.cifCustomerId =:customerId")
	Optional<FdOpeningNTB> fetchByCifCustomerId(String customerId);
	

	


}
