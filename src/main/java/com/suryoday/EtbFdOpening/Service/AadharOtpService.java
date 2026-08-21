package com.suryoday.EtbFdOpening.Service;

import org.json.JSONObject;
import org.springframework.stereotype.Component;

import com.suryoday.EtbFdOpening.Pojo.FdOpening;

@Component
public interface AadharOtpService {

	String getXmlRequest(String aadharNo, String stan) throws Exception;

	String sendEkyc(String xmlRequest);

	JSONObject createAadharReference(JSONObject jsonObject, JSONObject header);


}
