package com.suryoday.FdOpening.Service;

import org.json.JSONObject;
import org.springframework.stereotype.Component;

import com.suryoday.FdOpening.Pojo.FdOpening;

@Component
public interface FdRecieptService {

	String downloadRecietPdf(StringBuilder htmlString, String applicationNo,FdOpening fdopening);

	FdOpening fetchByMobNoAndSessionId(String mobileNo, String x_Session_ID);

	JSONObject createLead(JSONObject jsonObject, JSONObject header);

}
