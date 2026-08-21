package com.suryoday.EtbFdOpening.Service;

import org.json.JSONObject;
import org.springframework.stereotype.Component;

import com.suryoday.EtbFdOpening.Pojo.FdOpening;
import com.suryoday.EtbFdOpening.Pojo.FdOpeningNTB;

@Component
public interface FdOpeningService {

	JSONObject createDeposit(JSONObject jsonObject, JSONObject header);

	String saveFdData(String mobileNo, String x_Session_ID, JSONObject jsonObject);

	JSONObject CloseFd(JSONObject jsonObject, JSONObject header);

	void save(FdOpening fdopening);

	JSONObject createWorkItem(JSONObject jsonObject, JSONObject header);

	JSONObject createOrder(JSONObject jsonObject, JSONObject header);

	JSONObject fetchOrder(JSONObject jsonObject, JSONObject header);

	FdOpeningNTB fetchByApplicationNo(long applicationNo);

	FdOpeningNTB fetchByTrackingId(String trackingId);

	String saveNtbFdData(JSONObject jsonObject);

	JSONObject FdMaturityChange(JSONObject jsonObject, JSONObject header);

	JSONObject createDepositNtb(JSONObject jsonObject, JSONObject header);

	

	

}
