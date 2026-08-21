package com.suryoday.FdOpening.Service;

import org.json.JSONObject;

public interface CityPincodeService {

	
	public JSONObject getCityPincode(String cityPincode, JSONObject header);

	public JSONObject createWorkitem(JSONObject req, JSONObject header);
}
