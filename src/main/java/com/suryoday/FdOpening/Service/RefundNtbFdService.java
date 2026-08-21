package com.suryoday.FdOpening.Service;

import com.suryoday.FdOpening.Pojo.FdOpeningNTB;
import org.json.JSONObject;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface RefundNtbFdService {

//	RefundNtbFd save(RefundNtbFd refundFd);
//
//	List<RefundNtbFd> getAllRefundNtbFdList(String isRefundDone);

    JSONObject paymentRefundTransactionRequest(JSONObject payload);

    JSONObject paymentTransactionPushSer(JSONObject payload);

    List<FdOpeningNTB> findRefundNtbFdList(String isActive, String isRefundDone, String isPaymentDone,String isRefundProcessedApproved);

}
