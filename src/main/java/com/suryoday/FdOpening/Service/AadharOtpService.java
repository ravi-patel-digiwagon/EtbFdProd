package com.suryoday.FdOpening.Service;

import org.json.JSONObject;
import org.springframework.stereotype.Component;

import com.suryoday.FdOpening.Pojo.FdOpening;

@Component
public interface AadharOtpService {

	String getXmlRequest(String aadharNo, String stan) throws Exception;

	String sendEkyc(String xmlRequest);

	JSONObject createAadharReference(JSONObject jsonObject, JSONObject header);


}
