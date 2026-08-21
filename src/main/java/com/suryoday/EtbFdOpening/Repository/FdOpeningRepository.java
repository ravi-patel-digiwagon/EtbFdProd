package com.suryoday.EtbFdOpening.Repository;

import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import com.suryoday.EtbFdOpening.Pojo.FdOpening;

@Repository
@Component
public interface FdOpeningRepository extends JpaRepository<FdOpening, Long> {

	@Query(value = "SELECT top 1 a.id from tbl_fd_opening a order by a.id desc", nativeQuery = true)
	Optional<Long> fetchLastId();

	@Query(value = "Select top 1 a.session_id from tbl_fd_opening a where a.mobile_no=:mobileNo order by a.created_date desc;", nativeQuery = true)
	Optional<String> getSessionId(String mobileNo);

	@Transactional
	@Modifying
	@Query(value = "Delete from tbl_fd_opening  where mobile_no=:mobileNo and session_id!=:sessionId and status!='Completed'", nativeQuery = true)
	void deleteAllSessions(String mobileNo, String sessionId);

	@Query("Select a from FdOpening a where a.mobileNo=:mobileNo and a.sessionId=:x_Session_ID ")
	Optional<FdOpening> fetchBymobNo(String mobileNo, String x_Session_ID);

	@Query("Select a from FdOpening a where a.mobileNo=:mobileNo and a.custType=:type and a.status=:status")
	Optional<FdOpening> fetchData(String mobileNo, String type, String status);
}
