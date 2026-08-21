package com.suryoday.EtbFdOpening.Service;

import org.json.JSONObject;
import org.springframework.stereotype.Component;

@Component
public interface UpiMapperService {

	JSONObject upiMapper(JSONObject jsonObject, JSONObject header);

	JSONObject paymentVpa(JSONObject jsonObject, JSONObject header);

	JSONObject generateQR(JSONObject json) throws Exception;

	JSONObject faceMatch(JSONObject jsonObject);

}
