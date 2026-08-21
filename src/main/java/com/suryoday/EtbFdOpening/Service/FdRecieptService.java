package com.suryoday.EtbFdOpening.Service;

import org.json.JSONObject;
import org.springframework.stereotype.Component;

import com.suryoday.EtbFdOpening.Pojo.FdOpening;

@Component
public interface FdRecieptService {

	String downloadRecietPdf(StringBuilder htmlString, String applicationNo,FdOpening fdopening);

	FdOpening fetchByMobNoAndSessionId(String mobileNo, String x_Session_ID);

	JSONObject createLead(JSONObject jsonObject, JSONObject header);

}
