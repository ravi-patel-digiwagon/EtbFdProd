//package com.suryoday.EtbFdOpening.Repository;
//
//import java.util.List;
//
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.repository.query.Param;
//import org.springframework.stereotype.Component;
//import org.springframework.stereotype.Repository;
//
//import com.suryoday.EtbFdOpening.Pojo.RefundNtbFd;
//
//@Repository
//@Component
//public interface FdRefundRepository extends JpaRepository<RefundNtbFd, Long> {
//
//	
//	@Query("SELECT f FROM RefundNtbFd f WHERE f.isRefundDone = :isRefundDone")
//	List<RefundNtbFd> refundNtbFdList(@Param("isRefundDone") String isRefundDone);
//	
//}
