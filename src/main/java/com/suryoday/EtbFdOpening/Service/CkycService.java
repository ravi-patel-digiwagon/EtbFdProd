package com.suryoday.EtbFdOpening.Service;

import org.json.JSONObject;
import org.springframework.stereotype.Component;

@Component
public interface CkycService {

	JSONObject ckycSearch(JSONObject jsonObject, JSONObject header);

	JSONObject ckycDownload(JSONObject jsonObject, JSONObject header);

}
