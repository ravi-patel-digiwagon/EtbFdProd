package com.suryoday.EtbFdOpening.Service;

import org.json.JSONObject;
import org.springframework.stereotype.Component;

@Component
public interface PennyDropService {

	JSONObject pennyDrop(String accountNo,String ifsc,JSONObject header);

	JSONObject searchIfsc(String ifscCode, JSONObject header);

}
