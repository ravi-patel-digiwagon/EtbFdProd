package com.suryoday.EtbFdOpening.Service;

import org.json.JSONObject;
import org.springframework.stereotype.Component;

@Component
public interface DedupeService {

	JSONObject checkDedupe(String panNo, JSONObject header);

	JSONObject panCardValidation(String panCardNo, JSONObject header);

	JSONObject panValidation(JSONObject jsonObject, JSONObject header);

	JSONObject aadharPanLinkStatus(JSONObject jsonObject, JSONObject header);

}
