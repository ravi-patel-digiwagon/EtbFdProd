package com.suryoday.FdOpening.Service;

import org.json.JSONObject;

import com.suryoday.FdOpening.Pojo.CraftMerchantDetails;

public interface LeegalityService {

	JSONObject sendLeegality(JSONObject jsonObject,String type) throws Exception;

	JSONObject fetchLeegality(JSONObject jsonObject,String type) throws Exception;

	JSONObject saveData(CraftMerchantDetails craftMerchantDetails);

	JSONObject fetchQr(JSONObject jsonObject) throws Exception;

	JSONObject dashboard(JSONObject jsonObject);
	
	CraftMerchantDetails fetchByMobileNo(JSONObject jsonObject);

	JSONObject fetchAllDataWeb(JSONObject jsonObject);

}
